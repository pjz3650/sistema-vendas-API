package com.picpay.vendas.model;

import java.util.Arrays;

public enum TipoPagamento {
    PIX ("PIX"),
    CARTAO_CREDITO ("CARTAO_CREDITO"),
    CARTAO_DEBITO ("CARTAO_DEBITO");

    private final String codigo;

    TipoPagamento(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }


}
