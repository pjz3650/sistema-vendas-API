package com.picpay.vendas.core.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class VendaEvent {
    private String id;
    private List<String> idProduto;
    private ClienteEvent cliente;
    private BigDecimal valorCompra;
    private TipoPagamento tipoPagamento;
    private BigDecimal valorPago;

    @Getter
    @Builder
    public static class ClienteEvent {
        private String id;
        private String nome;
        private String sobrenome;
        private BigDecimal credito;
    }

    public static VendaEvent fromVenda(Venda venda) {
        return VendaEvent.builder()
                .id(venda.getId())
                .idProduto(venda.getIdProduto())
                .cliente(ClienteEvent.builder()
                        .id(venda.getCliente().getId())
                        .nome(venda.getCliente().getNome())
                        .sobrenome(venda.getCliente().getSobrenome())
                        .credito(venda.getCliente().getCredito())
                        .build())
                .valorCompra(venda.getValorCompra())
                .tipoPagamento(venda.getTipoPagamento())
                .valorPago(venda.getValorPago())
                .build();
    }
}
