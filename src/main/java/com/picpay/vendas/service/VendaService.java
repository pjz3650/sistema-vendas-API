package com.picpay.vendas.service;

import com.picpay.vendas.exception.ConflitoVendaException;
import com.picpay.vendas.exception.VendaJaExistenteException;
import com.picpay.vendas.model.Produto;
import com.picpay.vendas.model.Venda;
import com.picpay.vendas.repository.ProdutoClient;
import com.picpay.vendas.repository.VendaRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
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

        } catch (Exception e){
            throw new VendaJaExistenteException("Essa venda já foi registrada...");
        }
    }

    public List<Venda> listar() {
        return repository.findAll();
    }

    public Optional<Venda> buscar(Long id) {
        return repository.findById(id);
    }

    public boolean deletar(Long id) {

        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        return true;

    }

    public Venda atualizar(Venda venda) {
        return repository.findById(venda.getId())
                .map(p -> {
                    p.setCliente(venda.getCliente());
                    p.setIdProduto(venda.getIdProduto());
                    return adicionar(venda);
                })
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));
    }

    public Venda adicionarFallback(Venda input) {
        throw new ConflitoVendaException("Serviço de produtos indisponível");
    }

}