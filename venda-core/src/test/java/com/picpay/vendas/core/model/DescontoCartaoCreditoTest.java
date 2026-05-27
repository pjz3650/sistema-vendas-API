package com.picpay.vendas.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DescontoCartaoCredito - Testes Unitários")
class DescontoCartaoCreditoTest {

    private final DescontoCartaoCredito descontoCredito = new DescontoCartaoCredito();

    @Nested
    @DisplayName("Ao calcular desconto")
    class CalcularDesconto {

        @ParameterizedTest
        @CsvSource({
                "100.00, 100.00",
                "50.00, 50.00",
                "200.00, 200.00",
                "1.00, 1.00",
                "1000.00, 1000.00"
        })
        @DisplayName("não deve aplicar desconto - retorna valor original")
        void naoDeveAplicarDesconto(String valorEntrada, String valorEsperado) {
            BigDecimal resultado = descontoCredito.calcularDesconto(new BigDecimal(valorEntrada));

            assertThat(resultado).isEqualByComparingTo(valorEsperado);
        }

        @ParameterizedTest
        @ValueSource(strings = {"0.00", "0.01", "99.99"})
        @DisplayName("deve manter valor original para qualquer quantidade")
        void deveManterValorOriginal(String valor) {
            BigDecimal resultado = descontoCredito.calcularDesconto(new BigDecimal(valor));

            assertThat(resultado).isEqualByComparingTo(valor);
        }

        @Test
        @DisplayName("deve retornar zero quando valor for zero")
        void deveRetornarZeroQuandoValorZero() {
            BigDecimal resultado = descontoCredito.calcularDesconto(BigDecimal.ZERO);

            assertThat(resultado).isEqualByComparingTo("0.00");
        }
    }

    @Test
    @DisplayName("deve retornar tipo de pagamento CARTAO_CREDITO")
    void deveRetornarTipoPagamentoCredito() {
        assertThat(descontoCredito.getTipoPagamento()).isEqualTo(TipoPagamento.CARTAO_CREDITO);
    }
}
