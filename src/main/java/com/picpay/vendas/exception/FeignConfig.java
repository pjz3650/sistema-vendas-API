package com.picpay.vendas.exception;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor clientIdentificationAndVersion(
            @Value("${app.client-id}") String clientId,
            @Value("${integrations.produto.api-version}") String apiVersion
    ) {
        return template -> {
            template.header("X-Client-Id", clientId);
            template.query("apiVersion", apiVersion);
        };
    }

    @Bean
    ErrorDecoder errorDecoder() {
        return new ErrorDecoderProduto();
    }
}
