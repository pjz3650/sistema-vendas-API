package com.picpay.vendas.core.service;

import com.picpay.vendas.core.exception.*;
import com.picpay.vendas.core.model.*;
import com.picpay.vendas.core.publisher.VendaPublisher;
import com.picpay.vendas.core.repository.ProdutoClient;
import com.picpay.vendas.core.repository.VendaRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.picpay.vendas.core.model.Status.*;

@Slf4j
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

        log.info("Processando venda. ID: {}, clienteId: {}", venda.getId(), venda.getCliente().getId());

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
            if (!validar(venda)) {
                venda.setStatus(REJEITADA);
                throw new ValorRecebidoInvalidoException("Valor recebido inválido");
            }

            venda.setStatus(APROVADA);
            var vendaSalva = repository.save(venda);
            publisher.publicar(vendaSalva);
            log.info("Venda cadastrada e publicada com sucesso! ID: {}", vendaSalva.getId());
            return vendaSalva;
        } catch (DuplicateKeyException e) {
            error(venda);
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

        log.info("Atualizando venda. ID: {}", venda.getId());

        if (venda.getTipoPagamento() == null) {
            throw new TipoPagamentoInvalidoException("Tipo de pagamento é obrigatório");
        }

        return repository.findById(venda.getId())
                .map(existente -> {
                    existente.setCliente(venda.getCliente());
                    existente.setIdProduto(venda.getIdProduto());
                    existente.setValorPago(venda.getValorPago());
                    existente.setDiferenca(venda.getDiferenca());
                    existente.setTipoPagamento(venda.getTipoPagamento());

                    BigDecimal valorTotal = existente.getIdProduto().stream()
                            .map(client::buscarProduto)
                            .map(Produto::getPreco)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    var implementacao = fabrica.devolverImplementacao(existente.getTipoPagamento());
                    existente.setValorCompra(implementacao.calcularDesconto(valorTotal));

                    if (!validar(existente)) {
                        log.warn("Venda cancelada na atualização. ID: {}, valorPago: {}, valorCompra: {}",
                            venda.getId(), venda.getValorPago(), venda.getValorCompra());
                        cancelar(existente);
                        throw new ValorRecebidoInvalidoException("Valor recebido inválido");
                    }

                    aprovar(existente);
                    var vendaSalva = repository.save(existente);
                    publisher.publicar(vendaSalva);
                    log.info("Venda atualizada com sucesso. ID: {}", vendaSalva.getId());
                    return vendaSalva;
                })
                .orElseThrow(() -> {
                    error(venda);
                    return new VendaNaoEncontradaException("Venda não encontrada");
                });
    }

    public void aprovar(Venda venda) {
        venda.setStatus(APROVADA);
    }

    public void cancelar(Venda venda) {
        venda.setStatus(CANCELADA);
    }

    public boolean validar(Venda venda) throws ValorRecebidoInvalidoException {
        BigDecimal valorPago = venda.getValorPago();
        BigDecimal valorCompra = venda.getValorCompra();
        BigDecimal credito = venda.getCliente().getCredito();

        int comparacao = valorPago.compareTo(valorCompra);

        if (comparacao > 0) {
            BigDecimal diferencaEsperada = valorPago.subtract(valorCompra);
            return venda.getDiferenca() != null
                    && venda.getDiferenca().compareTo(diferencaEsperada) == 0;
        }

        if (comparacao < 0) {
            BigDecimal saldoDisponivel = credito.add(valorPago);
            return saldoDisponivel.compareTo(valorCompra) == 0;
        }

        return valorCompra.compareTo(valorPago) == 0;
    }

    public void error(Venda venda) {
        venda.setStatus(ERRO);
    }

    public Venda adicionarFallback(Exception e) {
        log.error("Falha ao conectar com ms-produto. Causa: {}", e.getMessage());
        throw new ErroAoConectarComMsException("Serviço de produtos indisponível");
    }
}
