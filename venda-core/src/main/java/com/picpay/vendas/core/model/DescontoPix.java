package com.picpay.vendas.core.model;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Qualifier("descontoPix")
public class DescontoPix implements CalculoDesconto {

    public TipoPagamento getTipoPagamento() {
        return TipoPagamento.PIX;
    }

    public BigDecimal calcularDesconto(BigDecimal valor) {
        return valor.multiply(BigDecimal.valueOf(0.90));
    }
}
