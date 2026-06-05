package com.picpay.vendas.core.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("TipoPagamentoInvalidoException - Testes Unitários")
class TipoPagamentoInvalidoExceptionTest {

    @Test
    @DisplayName("deve criar exceção com mensagem")
    void deveCriarExcecaoComMessagem() {
        String mensagem = "Tipo de pagamento não suportado";

        TipoPagamentoInvalidoException exception = new TipoPagamentoInvalidoException(mensagem);

        assertThat(exception.getMessage()).isEqualTo(mensagem);
    }

    @Test
    @DisplayName("deve ser uma RuntimeException")
    void deveSerRuntimeException() {
        TipoPagamentoInvalidoException exception = new TipoPagamentoInvalidoException("teste");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
