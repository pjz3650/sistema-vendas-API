package com.picpay.vendas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@Builder
@Document(collection = "vendas")
public class Venda {

    @Id
    private String id;
    private List<Long> idProduto;
    private Cliente cliente;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double valorCompra;
    private TipoPagamento tipoPagamento;


    @Data
    @Builder
    public static class Cliente {

        private String id;
        private String nome;
        private String sobrenome;
        private Double credito;
    }

}
