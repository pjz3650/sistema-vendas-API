package com.picpay.vendas.core.service;

import com.picpay.vendas.core.exception.VendaNaoEncontradaException;
import com.picpay.vendas.core.model.*;
import com.picpay.vendas.core.repository.ProdutoClient;

import java.util.List;

import static com.picpay.vendas.core.model.Status.APROVADA;

public class NotaFiscalService {

    private final VendaService vendaService;
    private final ProdutoClient client;

    public NotaFiscalService(VendaService vendaService, ProdutoClient client) {
        this.vendaService = vendaService;
        this.client = client;
    }

    public NotaFiscal gerarNota(VendaEvent vendaEvent) {
        Venda vendaPersistida = vendaService.buscar(vendaEvent.getId())
                .orElseThrow(() -> new VendaNaoEncontradaException("Venda não encontrada"));

        if (vendaPersistida.getStatus() != APROVADA) {
            throw new IllegalStateException("Só gera nota fiscal para venda aprovada");
        }

        List<Produto> produtos = vendaEvent.getIdProduto().stream()
                .map(client::buscarProduto)
                .toList();

        return NotaFiscal.builder()
                .numero(vendaEvent.getId())
                .valor(vendaEvent.getValorCompra())
                .nomeCliente(vendaEvent.getCliente().getNome())
                .produtos(produtos)
                .build();
    }
}
