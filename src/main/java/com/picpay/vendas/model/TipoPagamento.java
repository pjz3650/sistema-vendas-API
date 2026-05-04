package com.picpay.vendas.model;

import java.util.Arrays;

public enum TipoPagamento {
    PIX ("PIX"),
    CARTAO_CRADITO ("CARTAO_CREDITO"),
    CARTAO_DEBITO ("CARTAO_DEBITO");

    private final String codigo;

    TipoPagamento(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public static boolean isCodigoValido(String codigo) {
        return Arrays.stream(values())
                .anyMatch(e -> e.codigo.equalsIgnoreCase(codigo));
    }

}
