package com.picpay.vendas.exception;

import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ErrorDecoderProdutoTest {

    private ErrorDecoderProduto decoder;

    private final Request dummyRequest = Request.create(
            Request.HttpMethod.GET,
            "http://localhost/test",
            Map.of(),
            null,
            null,
            null
    );

    @BeforeEach
    void setUp() {
        decoder = new ErrorDecoderProduto();
    }

    @Test
    @DisplayName("Deve retornar ErroAoConectarComMsException com mensagem de produto não encontrado para status 404")
    void deveRetornarExcecaoParaStatus404() {
        Response response = Response.builder()
                .status(404)
                .reason("Not Found")
                .request(dummyRequest)
                .headers(Map.of())
                .build();

        Exception resultado = decoder.decode("ProdutoClient#buscarProduto", response);

        assertInstanceOf(ErroAoConectarComMsException.class, resultado);
        assertEquals("Produto não encontrado", resultado.getMessage());
    }

    @Test
    @DisplayName("Deve retornar ErroAoConectarComMsException com mensagem de API indisponível para status 500")
    void deveRetornarExcecaoParaStatus500() {
        Response response = Response.builder()
                .status(500)
                .reason("Internal Server Error")
                .request(dummyRequest)
                .headers(Map.of())
                .build();

        Exception resultado = decoder.decode("ProdutoClient#buscarProduto", response);

        assertInstanceOf(ErroAoConectarComMsException.class, resultado);
        assertEquals("API indisponível no momento", resultado.getMessage());
    }

    @Test
    @DisplayName("Deve retornar ErroAoConectarComMsException com status no corpo para status inesperado")
    void deveRetornarExcecaoParaStatusDefault() {
        Response response = Response.builder()
                .status(503)
                .reason("Service Unavailable")
                .request(dummyRequest)
                .headers(Map.of())
                .build();

        Exception resultado = decoder.decode("ProdutoClient#buscarProduto", response);

        assertInstanceOf(ErroAoConectarComMsException.class, resultado);
        assertTrue(resultado.getMessage().contains("503"));
    }

    @Test
    @DisplayName("Deve retornar ErroAoConectarComMsException para status 400")
    void deveRetornarExcecaoParaStatus400() {
        Response response = Response.builder()
                .status(400)
                .reason("Bad Request")
                .request(dummyRequest)
                .headers(Map.of())
                .build();

        Exception resultado = decoder.decode("ProdutoClient#buscarProduto", response);

        assertInstanceOf(ErroAoConectarComMsException.class, resultado);
        assertTrue(resultado.getMessage().contains("400"));
    }
}
