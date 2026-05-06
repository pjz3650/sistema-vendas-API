package com.picpay.vendas.model;


import java.math.BigDecimal;

public interface CalculoDesconto {

    BigDecimal calcularDesconto(BigDecimal valor);

    TipoPagamento getTipoPagamento();
}
