package com.polarbookshop.catalog_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan  // 스프링 콘텍스트에 설정 데이터를 빈을 로드한다.
//@EnableConfigurationProperties(PolarProperties.class)  // EnableConfigurationProperties 로 직접 설정도 가능
public class CatalogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogServiceApplication.class, args);
	}

}
