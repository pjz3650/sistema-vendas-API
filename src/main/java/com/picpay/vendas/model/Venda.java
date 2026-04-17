package com.picpay.vendas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@Getter
@Setter
@Builder
@Document(collection = "vendas")
public class Venda {

    @Id
    private Long id;
    private List<Long> idProduto;
    private Cliente cliente;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double valorCompra;



    @Data
    @Getter
    @Setter
    @Builder
    public static class Cliente {

        @Id
        private Long id;
        private String nome;
        private String sobrenome;
        private Double credito;
    }

}
