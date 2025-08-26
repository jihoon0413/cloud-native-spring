package com.polarbookshop.order_service.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@ConfigurationProperties(prefix = "polar")
public record ClientProperties(

        @NotNull
        URI catalogServiceUri
) {
}
