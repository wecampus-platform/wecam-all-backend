package org.example.wecambackend.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final CurrentUserArgumentResolver currentUserArgumentResolver;

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgumentResolver);
    }

    /**
     * '/uploads/**' 요청을 로컬 디스크의 'uploadDir' 경로와 매핑
     *  Ex) GET /uploads/profile/abc.jpg → file:uploadDir/profile/abc.jpg
     * @param registry
     */
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/")
                .setCachePeriod(3600) // 캐시 유지 시간 (초)
                .resourceChain(true);
    }
}
