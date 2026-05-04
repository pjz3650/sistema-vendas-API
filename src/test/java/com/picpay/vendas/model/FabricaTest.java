package com.picpay.vendas.model;

import com.picpay.vendas.exception.TipoPagamentoInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FabricaTest {

    private Fabrica fabrica;

    @BeforeEach
    void setUp() {
        fabrica = new Fabrica(List.of(
                new DescontoPix(),
                new DescontoCartaoDebito(),
                new DescontoCartaoCredito()
        ));
    }

    @Test
    @DisplayName("Deve retornar DescontoPix para TipoPagamento.PIX")
    void deveRetornarDescontoPixParaPix() {
        CalculoDesconto resultado = fabrica.devolverImplementacao(TipoPagamento.PIX);

        assertNotNull(resultado);
        assertEquals(TipoPagamento.PIX, resultado.getTipoPagamento());
        assertInstanceOf(DescontoPix.class, resultado);
    }

    @Test
    @DisplayName("Deve retornar DescontoCartaoDebito para TipoPagamento.CARTAO_DEBITO")
    void deveRetornarDescontoDebitoParaCartaoDebito() {
        CalculoDesconto resultado = fabrica.devolverImplementacao(TipoPagamento.CARTAO_DEBITO);

        assertNotNull(resultado);
        assertEquals(TipoPagamento.CARTAO_DEBITO, resultado.getTipoPagamento());
        assertInstanceOf(DescontoCartaoDebito.class, resultado);
    }

    @Test
    @DisplayName("Deve retornar DescontoCartaoCredito para TipoPagamento.CARTAO_CREDITO")
    void deveRetornarDescontoCreditoParaCartaoCredito() {
        CalculoDesconto resultado = fabrica.devolverImplementacao(TipoPagamento.CARTAO_CREDITO);

        assertNotNull(resultado);
        assertEquals(TipoPagamento.CARTAO_CREDITO, resultado.getTipoPagamento());
        assertInstanceOf(DescontoCartaoCredito.class, resultado);
    }

    @Test
    @DisplayName("Deve lançar TipoPagamentoInvalidoException quando não há implementação para o tipo")
    void deveLancarExcecaoQuandoNaoHaImplementacao() {
        Fabrica fabricaSemImplementacoes = new Fabrica(List.of());

        TipoPagamentoInvalidoException ex = assertThrows(
                TipoPagamentoInvalidoException.class,
                () -> fabricaSemImplementacoes.devolverImplementacao(TipoPagamento.PIX)
        );

        assertTrue(ex.getMessage().contains("PIX"));
    }
}
