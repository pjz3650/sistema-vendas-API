package com.picpay.vendas.core.exception;

import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ErrorDecoderProduto - Testes Unitários")
class ErrorDecoderProdutoTest {

    private ErrorDecoderProduto errorDecoder;

    @BeforeEach
    void setUp() {
        errorDecoder = new ErrorDecoderProduto();
    }

    @Nested
    @DisplayName("Ao decodificar erro")
    class DecodificarErro {

        @Test
        @DisplayName("deve retornar ProdutoNaoEncontradoException para status 404")
        void deveRetornarErroParaStatus404() {
            Response response = criarResponse(404);
            Request request = Request.create(Request.HttpMethod.GET, "/test", Collections.emptyMap(), null, StandardCharsets.UTF_8);

            Exception exception = errorDecoder.decode("buscarProduto", response);

            assertThat(exception).isInstanceOf(ProdutoNaoEncontradoException.class);
            assertThat(exception.getMessage()).isEqualTo("Produto não encontrado");
        }

        @Test
        @DisplayName("deve retornar ErroAoConectarComMsException para status 500")
        void deveRetornarErroParaStatus500() {
            Response response = criarResponse(500);

            Exception exception = errorDecoder.decode("buscarProduto", response);

            assertThat(exception).isInstanceOf(ErroAoConectarComMsException.class);
            assertThat(exception.getMessage()).isEqualTo("API indisponível no momento");
        }

        @Test
        @DisplayName("deve retornar DadosInconsistentesException para status 400")
        void deveRetornarErroParaStatus400() {
            Response response = criarResponse(400);

            Exception exception = errorDecoder.decode("buscarProduto", response);

            assertThat(exception).isInstanceOf(DadosInconsistentesException.class);
            assertThat(exception.getMessage()).isEqualTo("Dados inconsistentes");
        }

        @Test
        @DisplayName("deve retornar ErroAoConectarComMsException para status 503")
        void deveRetornarErroParaStatus503() {
            Response response = criarResponse(503);

            Exception exception = errorDecoder.decode("buscarProduto", response);

            assertThat(exception).isInstanceOf(ErroAoConectarComMsException.class);
            assertThat(exception.getMessage()).contains("API indisponível no momento");
        }
    }


    private Response criarResponse(int status) {
        return Response.builder()
                .status(status)
                .reason("Test reason")
                .request(Request.create(Request.HttpMethod.GET, "/test", Collections.emptyMap(), null, StandardCharsets.UTF_8))
                .headers(Collections.emptyMap())
                .build();
    }
}
