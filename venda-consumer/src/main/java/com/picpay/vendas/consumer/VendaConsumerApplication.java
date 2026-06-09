package com.picpay.vendas.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "com.picpay.vendas.core",
                "com.picpay.vendas.consumer"
        },
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.picpay\\.vendas\\.api\\..*"
        )
)
@EnableFeignClients(basePackages = "com.picpay.vendas.core.repository")
@EnableMongoRepositories(basePackages = "com.picpay.vendas.core.repository")
public class VendaConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(VendaConsumerApplication.class, args);
    }
}