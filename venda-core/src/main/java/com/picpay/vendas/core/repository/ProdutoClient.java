package com.picpay.vendas.core.repository;

import com.picpay.vendas.core.model.Produto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "produto-client",
        url = "${integrations.produto.url}",
        contextId = "produtoClient"
)
public interface ProdutoClient {

    @GetMapping("${integrations.produto.api-version}/test-api/procurar/{id}")
    Produto buscarProduto(@PathVariable("id") String id);
}
