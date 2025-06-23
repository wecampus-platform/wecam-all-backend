package org.example.wecambackend.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI speaknoteOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WeCampus API")
                        .description("학생회 업무 효율화 플랫폼 API 문서")
                        .version("v1.0.0"));
    }
}
