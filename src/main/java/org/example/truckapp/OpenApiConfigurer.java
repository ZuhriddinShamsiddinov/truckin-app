package org.example.truckapp;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnProperty(name = "springdoc.swagger-ui.enabled", havingValue = "true", matchIfMissing = true)
public class OpenApiConfigurer {
    private final OpenApiProperties openApiProperties;

    @Autowired
    public OpenApiConfigurer(OpenApiProperties openApiProperties) {
        this.openApiProperties = openApiProperties;
    }


    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(info());
    }

    private Info info() {
        return new Info()
                .title(openApiProperties.getTitle())
                .description(openApiProperties.getDescription())
                .version(openApiProperties.getVersion())
                .contact(new Contact()
                        .name(openApiProperties.getContactName())
                        .email(openApiProperties.getContactEmail())
                        .url(openApiProperties.getContactUrl()));
    }

}
