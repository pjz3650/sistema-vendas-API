package com.picpay.vendas.configuration;

import com.picpay.vendas.exception.ErrorDecoderProduto;
import feign.Client;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor clientIdentification(
            @Value("${app.client-id}") String clientId
    ) {
        return template -> template.header("X-Client-Id", clientId);
    }

    @Bean
    ErrorDecoder errorDecoder0() {
        return new ErrorDecoderProduto();
    }

    @Bean
    public Client feignClient() {
        return new OkHttpClient();
    }
}
