package com.picpay.vendas.core.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ValorRecebidoInvalidoException - Testes Unitários")
class ValorRecebidoInvalidoExceptionTest {

    @Test
    @DisplayName("deve criar exceção com mensagem")
    void deveCriarExcecaoComMessagem() {
        String mensagem = "Valor recebido é menor que o valor da compra";

        ValorRecebidoInvalidoException exception = new ValorRecebidoInvalidoException(mensagem);

        assertThat(exception.getMessage()).isEqualTo(mensagem);
    }

    @Test
    @DisplayName("deve ser uma RuntimeException")
    void deveSerRuntimeException() {
        ValorRecebidoInvalidoException exception = new ValorRecebidoInvalidoException("teste");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
