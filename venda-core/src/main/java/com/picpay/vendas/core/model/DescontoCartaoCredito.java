package com.picpay.vendas.core.model;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Qualifier("descontoCredito")
public class DescontoCartaoCredito implements CalculoDesconto {

    public TipoPagamento getTipoPagamento() {
        return TipoPagamento.CARTAO_CREDITO;
    }

    public BigDecimal calcularDesconto(BigDecimal valor) {
        return valor;
    }
}
