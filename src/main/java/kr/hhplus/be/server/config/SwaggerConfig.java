package kr.hhplus.be.server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(this.apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("E-Commerce Service")
                .description("🛒 e-커머스 서비스")
                .version("1.0")
                .contact(new Contact()
                        .name("Github Repository")
                        .url("https://github.com/eunsol-an/e-commerce-server"));
    }
}
