package com.picpay.vendas.core.model;

import com.picpay.vendas.core.exception.TipoPagamentoInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Fabrica - Testes Unitários")
class FabricaTest {

    private Fabrica fabrica;

    @BeforeEach
    void setUp() {
        List<CalculoDesconto> implementacoes = List.of(
                new DescontoPix(),
                new DescontoCartaoCredito(),
                new DescontoCartaoDebito()
        );
        fabrica = new Fabrica(implementacoes);
    }

    @Nested
    @DisplayName("Ao devolver implementação")
    class DevolverImplementacao {

        @Test
        @DisplayName("deve retornar DescontoPix quando tipo for PIX")
        void deveRetornarDescontoPix() {
            CalculoDesconto desconto = fabrica.devolverImplementacao(TipoPagamento.PIX);

            assertThat(desconto).isInstanceOf(DescontoPix.class);
            assertThat(desconto.getTipoPagamento()).isEqualTo(TipoPagamento.PIX);
        }

        @Test
        @DisplayName("deve retornar DescontoCartaoCredito quando tipo for CARTAO_CREDITO")
        void deveRetornarDescontoCredito() {
            CalculoDesconto desconto = fabrica.devolverImplementacao(TipoPagamento.CARTAO_CREDITO);

            assertThat(desconto).isInstanceOf(DescontoCartaoCredito.class);
            assertThat(desconto.getTipoPagamento()).isEqualTo(TipoPagamento.CARTAO_CREDITO);
        }

        @Test
        @DisplayName("deve retornar DescontoCartaoDebito quando tipo for CARTAO_DEBITO")
        void deveRetornarDescontoDebito() {
            CalculoDesconto desconto = fabrica.devolverImplementacao(TipoPagamento.CARTAO_DEBITO);

            assertThat(desconto).isInstanceOf(DescontoCartaoDebito.class);
            assertThat(desconto.getTipoPagamento()).isEqualTo(TipoPagamento.CARTAO_DEBITO);
        }

        @Test
        @DisplayName("deve lançar exceção quando tipo não tiver implementação")
        void deveLancarExcecaoQuandoTipoNaoTiverImplementacao() {
            assertThrows(TipoPagamentoInvalidoException.class,
                    () -> fabrica.devolverImplementacao(null));
        }
    }

    @Nested
    @DisplayName("Ao calcular descontos via Factory")
    class CalcularDescontosViaFactory {

        @Test
        @DisplayName("deve aplicar 10% de desconto para PIX")
        void deveAplicarDescontoPix() {
            CalculoDesconto desconto = fabrica.devolverImplementacao(TipoPagamento.PIX);

            BigDecimal resultado = desconto.calcularDesconto(new BigDecimal("100.00"));

            assertThat(resultado).isEqualByComparingTo("90.00");
        }

        @Test
        @DisplayName("não deve aplicar desconto para cartão de crédito")
        void naoDeveAplicarDescontoCredito() {
            CalculoDesconto desconto = fabrica.devolverImplementacao(TipoPagamento.CARTAO_CREDITO);

            BigDecimal resultado = desconto.calcularDesconto(new BigDecimal("100.00"));

            assertThat(resultado).isEqualByComparingTo("100.00");
        }

        @Test
        @DisplayName("deve aplicar 5% de desconto para cartão de débito")
        void deveAplicarDescontoDebito() {
            CalculoDesconto desconto = fabrica.devolverImplementacao(TipoPagamento.CARTAO_DEBITO);

            BigDecimal resultado = desconto.calcularDesconto(new BigDecimal("100.00"));

            assertThat(resultado).isEqualByComparingTo("95.00");
        }

        @Test
        @DisplayName("deve calcular corretamente para valor zero")
        void deveCalcularParaValorZero() {
            CalculoDesconto desconto = fabrica.devolverImplementacao(TipoPagamento.PIX);

            BigDecimal resultado = desconto.calcularDesconto(BigDecimal.ZERO);

            assertThat(resultado).isEqualByComparingTo("0.00");
        }

        @Test
        @DisplayName("deve calcular corretamente para valores com centavos")
        void deveCalcularParaValoresComCentavos() {
            CalculoDesconto desconto = fabrica.devolverImplementacao(TipoPagamento.PIX);

            BigDecimal resultado = desconto.calcularDesconto(new BigDecimal("99.99"));

            assertThat(resultado).isEqualByComparingTo("89.991");
        }
    }
}
