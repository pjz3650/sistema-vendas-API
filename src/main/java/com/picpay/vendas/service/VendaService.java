package com.picpay.vendas.service;

import com.picpay.vendas.exception.ErroAoConectarComMs;
import com.picpay.vendas.exception.VendaJaExistenteException;
import com.picpay.vendas.model.Produto;
import com.picpay.vendas.model.Venda;
import com.picpay.vendas.repository.ProdutoClient;
import com.picpay.vendas.repository.VendaRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class VendaService {

    private final VendaRepository repository;

    private final ProdutoClient client;


    public VendaService(VendaRepository vendaRepository, ProdutoClient produtoClient) {
        this.repository = vendaRepository;
        this.client = produtoClient;
    }

    @Retry(name = "adicionarVendaRetry", fallbackMethod = "adicionarFallback")
    public Venda adicionar(Venda venda) {

        double valorTotal = venda.getIdProduto().stream()
                .map(client::buscarProduto)
                .mapToDouble(Produto::getPreco)
                .sum();

        venda.setValorCompra(valorTotal);

        try {
            return repository.save(venda);
        } catch (DuplicateKeyException e) {
            throw new VendaJaExistenteException("Essa venda já foi registrada...");
        }
    }

    public List<Venda> listar() {
        return repository.findAll();
    }

    public Optional<Venda> buscar(String id) {
        return repository.findById(id);
    }

    public boolean deletar(String id) {

        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        return true;
    }

    public Venda atualizar(Venda venda) {
        return repository.findById(venda.getId())
                .map(existente -> {
                    existente.setCliente(venda.getCliente());
                    existente.setIdProduto(venda.getIdProduto());

                    double valorTotal = existente.getIdProduto().stream()
                            .map(client::buscarProduto)
                            .mapToDouble(Produto::getPreco)
                            .sum();

                    existente.setValorCompra(valorTotal);

                    return repository.save(existente);
                })
                .orElseThrow(() -> {
                    log.warn("Venda não encontrada para atualização - id: {}", venda.getId());
                    return new RuntimeException("Venda não encontrada");
                });
    }

    public Venda adicionarFallback(Venda input, Exception e) {
        log.error("Serviço indisponível - causa: {}", e.getMessage());
        throw new ErroAoConectarComMs("Serviço de produtos indisponível");
    }

}
