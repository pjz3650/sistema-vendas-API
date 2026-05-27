package com.picpay.vendas.api.configuration;

import com.picpay.vendas.core.exception.ErrorDecoderProduto;
import feign.Client;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FeignConfig (venda-api) - Testes Unitários")
class FeignConfigTest {

    private final FeignConfig feignConfig = new FeignConfig();

    @Nested
    @DisplayName("Ao criar beans")
    class CriarBeans {

        @Test
        @DisplayName("deve criar RequestInterceptor com header X-Client-Id")
        void deveCriarRequestInterceptor() {
            String clientId = "meu-client-id";

            RequestInterceptor interceptor = feignConfig.clientIdentification(clientId);

            assertThat(interceptor).isNotNull();
        }

        @Test
        @DisplayName("deve criar ErrorDecoder como ErrorDecoderProduto")
        void deveCriarErrorDecoder() {
            ErrorDecoder errorDecoder = feignConfig.errorDecoder();

            assertThat(errorDecoder).isInstanceOf(ErrorDecoderProduto.class);
        }

        @Test
        @DisplayName("deve criar Client Feign")
        void deveCriarClientFeign() {
            Client client = feignConfig.feignClient();

            assertThat(client).isNotNull();
        }
    }
}
