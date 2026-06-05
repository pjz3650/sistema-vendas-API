package com.picpay.vendas.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DescontoCartaoDebito - Testes Unitários")
class DescontoCartaoDebitoTest {

    private final DescontoCartaoDebito descontoDebito = new DescontoCartaoDebito();

    @Nested
    @DisplayName("Ao calcular desconto")
    class CalcularDesconto {

        @ParameterizedTest
        @CsvSource({
                "100.00, 95.00",
                "50.00, 47.50",
                "200.00, 190.00",
                "1.00, 0.95",
                "1000.00, 950.00"
        })
        @DisplayName("deve aplicar 5% de desconto corretamente")
        void deveAplicarDescontoCincoPorcento(String valorEntrada, String valorEsperado) {
            BigDecimal resultado = descontoDebito.calcularDesconto(new BigDecimal(valorEntrada));

            assertThat(resultado).isEqualByComparingTo(valorEsperado);
        }

        @ParameterizedTest
        @ValueSource(strings = {"0.00", "0.01", "0.50"})
        @DisplayName("deve calcular corretamente para valores pequenos")
        void deveCalcularParaValoresPequenos(String valor) {
            BigDecimal resultado = descontoDebito.calcularDesconto(new BigDecimal(valor));

            BigDecimal esperado = new BigDecimal(valor).multiply(new BigDecimal("0.95"));
            assertThat(resultado).isEqualByComparingTo(esperado);
        }

        @Test
        @DisplayName("deve retornar zero quando valor for zero")
        void deveRetornarZeroQuandoValorZero() {
            BigDecimal resultado = descontoDebito.calcularDesconto(BigDecimal.ZERO);

            assertThat(resultado).isEqualByComparingTo("0.00");
        }
    }

    @Test
    @DisplayName("deve retornar tipo de pagamento CARTAO_DEBITO")
    void deveRetornarTipoPagamentoDebito() {
        assertThat(descontoDebito.getTipoPagamento()).isEqualTo(TipoPagamento.CARTAO_DEBITO);
    }
}
