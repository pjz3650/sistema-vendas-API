package com.picpay.vendas.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = "com.picpay.vendas")
@EnableFeignClients(basePackages = "com.picpay.vendas.core.repository")
@EnableMongoRepositories(basePackages = "com.picpay.vendas.core.repository")
public class VendaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(VendaApiApplication.class, args);
    }
}
