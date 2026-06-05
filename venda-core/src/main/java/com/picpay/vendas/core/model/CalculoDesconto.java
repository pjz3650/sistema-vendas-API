package com.picpay.vendas.core.model;

import java.math.BigDecimal;

public interface CalculoDesconto {

    BigDecimal calcularDesconto(BigDecimal valor);

    TipoPagamento getTipoPagamento();
}
