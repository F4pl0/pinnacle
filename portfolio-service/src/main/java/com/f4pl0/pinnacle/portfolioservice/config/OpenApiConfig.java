package com.f4pl0.pinnacle.portfolioservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("oauth2", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl("http://localhost:9000/oauth2/authorize")
                                                .tokenUrl("http://localhost:9000/oauth2/token")
                                                .refreshUrl("http://localhost:9000/oauth2/refresh")
                                                .scopes(new Scopes()
                                                        .addString("openid", "OpenID Connect")
                                                        .addString("profile", "User Profile")
                                                )
                                        )
                                )
                        ))
                .info(new Info()
                        .title("Portfolio Service API")
                        .version("0.0.1")
                        .description("This is a Portfolio Service API")
                        .contact(new Contact().name("f4pl0").url("https://github.com/f4pl0"))
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("oauth2"));
    }
}