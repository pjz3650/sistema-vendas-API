package com.picpay.vendas.service;

import com.picpay.vendas.exception.VendaNaoEncontradaException;
import com.picpay.vendas.model.NotaFiscal;
import com.picpay.vendas.model.Produto;
import com.picpay.vendas.model.Venda;
import com.picpay.vendas.model.VendaEvent;
import com.picpay.vendas.repository.ProdutoClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class NotaFiscalService {

    private final VendaService service;
    private final ProdutoClient client;

    public NotaFiscalService(VendaService service, ProdutoClient client) {
        this.service = service;
        this.client = client;
    }

    public NotaFiscal gerarNota(VendaEvent venda) {
        service.buscar(venda.getId())
                .orElseThrow(() -> new VendaNaoEncontradaException("Venda não encontrada"));

        List<Produto> produtos = venda.getIdProduto().stream()
                .map(client::buscarProduto)
                .toList();

        return NotaFiscal.builder()
                .numero(venda.getId())
                .valor(venda.getValorCompra())
                .nomeCliente(venda.getCliente().getNome())
                .produtos(produtos)
                .build();
    }
}
