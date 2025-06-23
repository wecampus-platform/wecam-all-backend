package org.example.wecambackend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages =
        "org.example.model"                       // domain-common 엔티티
)
@EnableJpaRepositories(basePackages = "org.example.wecambackend.repos") // JPA 리포지토리 위치 지정
public class WecamBackendApplication {


    public static void main(String[] args) {

        //local 용
        // .env 파일 로드
        Dotenv dotenv = Dotenv.configure()
                .directory("./") // .env 파일 경로 설정 (기본: 프로젝트 루트)
                .load();

        // 환경변수를 시스템 프로퍼티에 추가
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
        //local 용

        SpringApplication.run(WecamBackendApplication.class, args);
    }

}
