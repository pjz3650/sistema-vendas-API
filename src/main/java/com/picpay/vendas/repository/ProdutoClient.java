package com.picpay.vendas.repository;


import com.picpay.vendas.configuration.FeignConfig;
import com.picpay.vendas.model.Produto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "produto-client",
        url = "http://localhost:8081",
        configuration = FeignConfig.class,
        contextId = "produtoClient"
)
public interface ProdutoClient {

    @GetMapping("/v1/test-api/procurar/{id}")
    Produto buscarProduto(@PathVariable("id") String id);
}
