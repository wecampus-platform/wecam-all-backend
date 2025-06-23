package org.example.wecamadminbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {
        "org.example.model",                       // domain-common 엔티티
        "org.example.wecamadminbackend.entity"     // wecamadminbackend 내부 엔티티
})public class WecamadminbackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WecamadminbackendApplication.class, args);
    }

}
