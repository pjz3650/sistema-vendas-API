package com.picpay.vendas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;


@Data
@Builder
@Document(collection = "vendas")
public class Venda {

    @Id
    private String id;
    @Size(min = 1, message = "A venda deve conter ao menos 1 produto")
    private List<String> idProduto;
    @NotNull(message = "Coloque todas as informações do cliente")
    private Cliente cliente;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal valorCompra;
    @NotNull(message = "Informe a forma de pagamento")
    private TipoPagamento tipoPagamento;


    @Data
    @Builder
    public static class Cliente {

        @NotBlank
        private String id;
        @NotBlank(message = "Informe o nome do cliente")
        private String nome;
        @NotBlank(message = "Informe o sobrenome do cliente")
        private String sobrenome;
        @Positive(message = "O crédito deve ser um valor válido")
        @NotNull(message = "Informe o crédito do cliente")
        private BigDecimal credito;
    }

}
