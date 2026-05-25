package com.picpay.vendas.service;

import com.picpay.vendas.exception.ErroAoConectarComMsException;
import com.picpay.vendas.exception.TipoPagamentoInvalidoException;
import com.picpay.vendas.exception.VendaJaExistenteException;
import com.picpay.vendas.exception.VendaNaoEncontradaException;
import com.picpay.vendas.model.*;
import com.picpay.vendas.publisher.VendaPublisher;
import com.picpay.vendas.repository.ProdutoClient;
import com.picpay.vendas.repository.VendaRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class VendaService {

    private final VendaRepository repository;

    private final Fabrica fabrica;

    private final VendaPublisher publisher;

    private final ProdutoClient client;


    public VendaService(VendaRepository vendaRepository, ProdutoClient produtoClient, Fabrica fabrica, VendaPublisher publisher) {
        this.repository = vendaRepository;
        this.client = produtoClient;
        this.fabrica = fabrica;
        this.publisher = publisher;
    }

    @Retry(name = "adicionarVendaRetry", fallbackMethod = "adicionarFallback")
    public Venda adicionar(Venda venda) {

        if (venda.getTipoPagamento() == null) {
             throw new TipoPagamentoInvalidoException("Tipo de pagamento é obrigatório");
        }

        BigDecimal valorTotal = venda.getIdProduto().stream()
                .map(client::buscarProduto)
                .map(Produto::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        var implementacao = fabrica.devolverImplementacao(venda.getTipoPagamento());

        BigDecimal valorFinal = implementacao.calcularDesconto(valorTotal);

        venda.setValorCompra(valorFinal);

        try {
            var vendaSalva = repository.save(venda);
            publisher.publicar(vendaSalva);
            return vendaSalva;
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

        if (venda.getTipoPagamento() == null) {
            throw new TipoPagamentoInvalidoException("Tipo de pagamento é obrigatório");
        }

        return repository.findById(venda.getId())
                .map(existente -> {
                    existente.setCliente(venda.getCliente());
                    existente.setIdProduto(venda.getIdProduto());

                    BigDecimal valorTotal = venda.getIdProduto().stream()
                            .map(client::buscarProduto)
                            .map(Produto::getPreco)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);


                    var implementacao = fabrica.devolverImplementacao(venda.getTipoPagamento());

                    BigDecimal valorFinal = implementacao.calcularDesconto(valorTotal);

                    venda.setValorCompra(valorFinal);

                    try {
                        var vendaSalva = repository.save(venda);
                        publisher.publicar(vendaSalva);
                        return vendaSalva;
                    } catch (DuplicateKeyException e) {
                        throw new VendaJaExistenteException("Essa venda já foi registrada...");
                    }
                })
                .orElseThrow(() -> {
                    return new VendaNaoEncontradaException("Venda não encontrada");
                });
    }

    public Venda adicionarFallback(Venda input, Exception e) {
        throw new ErroAoConectarComMsException("Serviço de produtos indisponível");
    }

}
