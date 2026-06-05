package com.picpay.vendas.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status {
    CRIADA("CRIADA"),
    PROCESSANDO("PROCESSANDO"),
    APROVADA("APROVADA"),
    REJEITADA("REJEITADA"),
    CANCELADA("CANCELADA"),
    ESTORNADA("ESTORNADA"),
    ERRO("ERRO");

    private final String codigo;
}
