package com.picpay.vendas.core.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("VendaJaExistenteException - Testes Unitários")
class VendaJaExistenteExceptionTest {

    @Test
    @DisplayName("deve criar exceção com mensagem")
    void deveCriarExcecaoComMessagem() {
        String mensagem = "Essa venda já foi registrada";

        VendaJaExistenteException exception = new VendaJaExistenteException(mensagem);

        assertThat(exception.getMessage()).isEqualTo(mensagem);
    }

    @Test
    @DisplayName("deve ser uma RuntimeException")
    void deveSerRuntimeException() {
        VendaJaExistenteException exception = new VendaJaExistenteException("teste");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
