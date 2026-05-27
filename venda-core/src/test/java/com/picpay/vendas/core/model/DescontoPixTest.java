package com.picpay.vendas.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DescontoPix - Testes Unitários")
class DescontoPixTest {

    private final DescontoPix descontoPix = new DescontoPix();

    @Nested
    @DisplayName("Ao calcular desconto")
    class CalcularDesconto {

        @ParameterizedTest
        @CsvSource({
                "100.00, 90.00",
                "50.00, 45.00",
                "200.00, 180.00",
                "1.00, 0.90",
                "1000.00, 900.00"
        })
        @DisplayName("deve aplicar 10% de desconto corretamente")
        void deveAplicarDescontoDezPorcento(String valorEntrada, String valorEsperado) {
            BigDecimal resultado = descontoPix.calcularDesconto(new BigDecimal(valorEntrada));

            assertThat(resultado).isEqualByComparingTo(valorEsperado);
        }

        @ParameterizedTest
        @ValueSource(strings = {"0.00", "0.01", "0.50"})
        @DisplayName("deve calcular corretamente para valores pequenos")
        void deveCalcularParaValoresPequenos(String valor) {
            BigDecimal resultado = descontoPix.calcularDesconto(new BigDecimal(valor));

            BigDecimal esperado = new BigDecimal(valor).multiply(new BigDecimal("0.90"));
            assertThat(resultado).isEqualByComparingTo(esperado);
        }

        @Test
        @DisplayName("deve retornar zero quando valor for zero")
        void deveRetornarZeroQuandoValorZero() {
            BigDecimal resultado = descontoPix.calcularDesconto(BigDecimal.ZERO);

            assertThat(resultado).isEqualByComparingTo("0.00");
        }
    }

    @Test
    @DisplayName("deve retornar tipo de pagamento PIX")
    void deveRetornarTipoPagamentoPix() {
        assertThat(descontoPix.getTipoPagamento()).isEqualTo(TipoPagamento.PIX);
    }
}
