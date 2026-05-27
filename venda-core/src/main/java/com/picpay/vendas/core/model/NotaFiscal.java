package com.picpay.vendas.core.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class NotaFiscal {
    private String numero;
    private List<Produto> produtos;
    private BigDecimal valor;
    private String nomeCliente;
}
