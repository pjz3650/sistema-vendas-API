package com.picpay.vendas.core.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Produto {

    private String id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer estoque;
}
