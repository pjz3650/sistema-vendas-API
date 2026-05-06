package com.picpay.vendas.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CalculoDescontoTest {

    @Test
    @DisplayName("DescontoPix deve retornar TipoPagamento.PIX")
    void descontoPixDeveRetornarTipoPagamentoPix() {
        DescontoPix desconto = new DescontoPix();
        assertEquals(TipoPagamento.PIX, desconto.getTipoPagamento());
    }

    @Test
    @DisplayName("DescontoPix deve aplicar 10% de desconto")
    void descontoPixDeveAplicar10PorCento() {
        DescontoPix desconto = new DescontoPix();
        BigDecimal resultado = desconto.calcularDesconto(BigDecimal.valueOf(100));
        assertEquals(0, BigDecimal.valueOf(90.0).compareTo(resultado));
    }

    @Test
    @DisplayName("DescontoPix deve retornar zero para valor zero")
    void descontoPixDeveRetornarZeroParaValorZero() {
        DescontoPix desconto = new DescontoPix();
        BigDecimal resultado = desconto.calcularDesconto(BigDecimal.ZERO);
        assertEquals(0, BigDecimal.ZERO.compareTo(resultado));
    }

    @Test
    @DisplayName("DescontoCartaoDebito deve retornar TipoPagamento.CARTAO_DEBITO")
    void descontoDebitoDeveRetornarTipoPagamentoDebito() {
        DescontoCartaoDebito desconto = new DescontoCartaoDebito();
        assertEquals(TipoPagamento.CARTAO_DEBITO, desconto.getTipoPagamento());
    }

    @Test
    @DisplayName("DescontoCartaoDebito deve aplicar 5% de desconto")
    void descontoDebitoDeveAplicar5PorCento() {
        DescontoCartaoDebito desconto = new DescontoCartaoDebito();
        BigDecimal resultado = desconto.calcularDesconto(BigDecimal.valueOf(100));
        assertEquals(0, BigDecimal.valueOf(95.0).compareTo(resultado));
    }

    @Test
    @DisplayName("DescontoCartaoDebito deve retornar zero para valor zero")
    void descontoDebitoDeveRetornarZeroParaValorZero() {
        DescontoCartaoDebito desconto = new DescontoCartaoDebito();
        BigDecimal resultado = desconto.calcularDesconto(BigDecimal.ZERO);
        assertEquals(0, BigDecimal.ZERO.compareTo(resultado));
    }

    @Test
    @DisplayName("DescontoCartaoCredito deve retornar TipoPagamento.CARTAO_CREDITO")
    void descontoCreditoDeveRetornarTipoPagamentoCredito() {
        DescontoCartaoCredito desconto = new DescontoCartaoCredito();
        assertEquals(TipoPagamento.CARTAO_CREDITO, desconto.getTipoPagamento());
    }

    @Test
    @DisplayName("DescontoCartaoCredito deve retornar valor sem desconto")
    void descontoCreditoDeveRetornarValorIntegral() {
        DescontoCartaoCredito desconto = new DescontoCartaoCredito();
        BigDecimal valor = BigDecimal.valueOf(100);
        BigDecimal resultado = desconto.calcularDesconto(valor);
        assertEquals(0, valor.compareTo(resultado));
    }

    @Test
    @DisplayName("DescontoCartaoCredito deve retornar zero para valor zero")
    void descontoCreditoDeveRetornarZeroParaValorZero() {
        DescontoCartaoCredito desconto = new DescontoCartaoCredito();
        BigDecimal resultado = desconto.calcularDesconto(BigDecimal.ZERO);
        assertEquals(0, BigDecimal.ZERO.compareTo(resultado));
    }
}
