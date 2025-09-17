package com.polarbookshop.catalog_service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.polarbookshop.catalog_service.domain.Book;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("integration") // 통합 테스트시 컨테이너 테스트를 가능하게 하기 위해서 해당 yml파일 실행이 필요
@Testcontainers
class CatalogServiceApplicationTests {

	// Customer
	private static KeycloakToken bjornTokens;
	// Customer and employee
	private static KeycloakToken isabelleTokens;

	@Autowired
	private WebTestClient webTestClient;

	@Container // 키크록 컨테이너 정의
	private static final KeycloakContainer keycloakContainer =
			new KeycloakContainer("quay.io/keycloak/keycloak:24.0")
			.withRealmImportFile("/test-realm-config.json");

	@DynamicPropertySource
	static void dynamicProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
				() -> keycloakContainer.getAuthServerUrl() + "/realms/PolarBookshop");
	}

	@BeforeAll
	static void generateAccessToken() {
		WebClient webClient = WebClient.builder()
				.baseUrl(keycloakContainer.getAuthServerUrl() + "/realms/PolarBookshop/protocol/openid-connect/token")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.build();

		isabelleTokens = authenticateWith("isabelle", "password", webClient);
		bjornTokens = authenticateWith("bjorn", "password", webClient);

		logRolesFromToken(isabelleTokens.accessToken);
		logRolesFromToken(bjornTokens.accessToken);

	}

	@Test
	void whenGetRequestWithIdThenBookReturned() {
		var isbn = "3213213124";
		var bookToCreate = Book.of(isbn, "Title", "Author", 6.66, "publisher");

		Book expected = webTestClient.post()
				.uri("/books")
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken))
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Book.class).value(book -> assertThat(book).isNotNull())
				.returnResult().getResponseBody();

		webTestClient
				.get()
				.uri("/books/" + isbn)
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(Book.class).value(book -> {
					assertThat(book).isNotNull();
					assertThat(book.isbn()).isEqualTo(isbn);
				});

	}

	@Test
	void whenPostRequestThenBookCreated() {
		var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "publisher");

		webTestClient
				.post()
				.uri("/books")
				.headers(headers ->
						headers.setBearerAuth(isabelleTokens.accessToken()))
				.bodyValue(expectedBook)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Book.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.isbn())
							.isEqualTo(expectedBook.isbn());
				});

	}

	@Test
	void whenPostRequestUnauthenticatedThen401() {
		var expectedBook = Book.of("1231231233", "Title", "Author", 9.90, "Polar");

		webTestClient.post().uri("/books")
				.bodyValue(expectedBook)
				.exchange()
				.expectStatus().isUnauthorized();

	}

	@Test
	void whenPostRequestUnauthorizedThen403() {
		var expectedBook = Book.of("1231231232", "Title", "Author", 9.90, "Polarsophia");

		webTestClient
				.post()
				.uri("/books")
				.headers(headers -> headers.setBearerAuth(bjornTokens.accessToken()))
				.bodyValue(expectedBook)
				.exchange()
				.expectStatus().isForbidden();
	}

	@Test
	void whenPutRequestThenBookUpdated() {
		var bookIsbn = "4564564566";
		var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, "Polarsophia");
		Book createdBook = webTestClient.post()
				.uri("/books")
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken))
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Book.class).value(book -> assertThat(book).isNotNull())
				.returnResult().getResponseBody();

		var bookToUpdate = new Book(createdBook.id(), createdBook.isbn(), createdBook.title(), createdBook.author(), 45.6,
				createdBook.publisher(), createdBook.createdDate(), createdBook.lastModifiedDate(), createdBook.version());

		webTestClient.put()
				.uri("/books/"+bookIsbn)
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken))
				.bodyValue(bookToUpdate)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Book.class).value(book -> {
					assertThat(book).isNotNull();
					assertThat(book.price()).isEqualTo(bookToUpdate.price());
				});
	}

	@Test
	void whenDeleteRequestThenBookDeleted() {
		var bookIsbn = "1231231233";
		var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, "Polarsophia");


		webTestClient.post()
				.uri("/books")
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken))
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated();

		webTestClient.delete()
				.uri("/books/" + bookIsbn)
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken))
				.exchange()
				.expectStatus().isNoContent();

		webTestClient.get()
				.uri("/books/" + bookIsbn)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody(String.class).value(errorMessage ->
						assertThat(errorMessage).isEqualTo("The book with ISBN " + bookIsbn + " was not found")
				);



	}

	private static KeycloakToken authenticateWith(
			String username, String password, WebClient webClient
	) {
		return webClient
				.post()
				.body(BodyInserters.fromFormData("grant_type", "password")
						.with("client_id", "polar-test")
						.with("username", username)
						.with("password", password)
				)
				.retrieve()
				.bodyToMono(KeycloakToken.class)
				.block();
	}

	private record KeycloakToken(String accessToken) {
		@JsonCreator
		private KeycloakToken(@JsonProperty("access_token") final String accessToken) {
			this.accessToken = accessToken;
		}
	}

	private static void logRolesFromToken(String accessToken) {

		try{
			JWT jwt = JWTParser.parse(accessToken);
			JWTClaimsSet claims = jwt.getJWTClaimsSet();
			System.out.println("please : " + claims.toString());

//			// Keycloak에서 일반적으로 roles는 "realm_access" 클레임의 "roles" 리스트에 있음
//			Map<String, Object> realmAccess = (Map<String, Object>) claims.getClaim("realm_access");
//			if (realmAccess != null) {
//				List<String> roles = (List<String>) realmAccess.get("roles");
//				System.out.println("Roles: " + roles);
//			} else {
//				System.out.println("No roles found in token.");
//			}

		} catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

}
