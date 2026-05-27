package com.picpay.vendas.core.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ErroAoConectarComMsException - Testes Unitários")
class ErroAoConectarComMsExceptionTest {

    @Test
    @DisplayName("deve criar exceção com mensagem")
    void deveCriarExcecaoComMessagem() {
        String mensagem = "Serviço indisponível";

        ErroAoConectarComMsException exception = new ErroAoConectarComMsException(mensagem);

        assertThat(exception.getMessage()).isEqualTo(mensagem);
    }

    @Test
    @DisplayName("deve ser uma RuntimeException")
    void deveSerRuntimeException() {
        ErroAoConectarComMsException exception = new ErroAoConectarComMsException("teste");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("deve permitir criar com mensagem vazia")
    void devePermitirMessagemVazia() {
        ErroAoConectarComMsException exception = new ErroAoConectarComMsException("");

        assertThat(exception.getMessage()).isEmpty();
    }

    @Test
    @DisplayName("deve permitir criar com mensagem nula")
    void devePermitirMessagemNula() {
        ErroAoConectarComMsException exception = new ErroAoConectarComMsException(null);

        assertThat(exception.getMessage()).isNull();
    }
}
