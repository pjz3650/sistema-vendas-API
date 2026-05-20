package com.picpay.vendas.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Getter
    @Builder
    public static class ClienteEvent {
        private String id;
        private String nome;
        private String sobrenome;
        private BigDecimal credito;

    }

    public ClienteEvent toClienteEvent(Venda.Cliente cliente) {
        return ClienteEvent.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .sobrenome(cliente.getSobrenome())
                .credito(cliente.getCredito())
                .build();
    }
}
