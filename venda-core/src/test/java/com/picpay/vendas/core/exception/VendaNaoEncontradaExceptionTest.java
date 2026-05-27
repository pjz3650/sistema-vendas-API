package com.picpay.vendas.core.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("VendaNaoEncontradaException - Testes Unitários")
class VendaNaoEncontradaExceptionTest {

    @Test
    @DisplayName("deve criar exceção com mensagem")
    void deveCriarExcecaoComMessagem() {
        String mensagem = "Venda não encontrada com o ID informado";

        VendaNaoEncontradaException exception = new VendaNaoEncontradaException(mensagem);

        assertThat(exception.getMessage()).isEqualTo(mensagem);
    }

    @Test
    @DisplayName("deve ser uma RuntimeException")
    void deveSerRuntimeException() {
        VendaNaoEncontradaException exception = new VendaNaoEncontradaException("teste");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
