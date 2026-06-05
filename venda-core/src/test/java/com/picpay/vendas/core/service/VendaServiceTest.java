package com.picpay.vendas.core.service;

import com.picpay.vendas.core.exception.TipoPagamentoInvalidoException;
import com.picpay.vendas.core.exception.ValorRecebidoInvalidoException;
import com.picpay.vendas.core.exception.VendaJaExistenteException;
import com.picpay.vendas.core.exception.VendaNaoEncontradaException;
import com.picpay.vendas.core.model.*;
import com.picpay.vendas.core.publisher.VendaPublisher;
import com.picpay.vendas.core.repository.ProdutoClient;
import com.picpay.vendas.core.repository.VendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VendaService - Testes Unitários")
class VendaServiceTest {

    @Mock
    private VendaRepository repository;

    @Mock
    private ProdutoClient produtoClient;

    @Mock
    private Fabrica fabrica;

    @Mock
    private VendaPublisher publisher;

    @Mock
    private CalculoDesconto calculoDesconto;

    private VendaService vendaService;

    @BeforeEach
    void setUp() {
        vendaService = new VendaService(repository, produtoClient, fabrica, publisher);
    }

    @Nested
    @DisplayName("Ao adicionar uma venda")
    class AdicionarVenda {

        @Test
        @DisplayName("deve lançar exceção quando tipo de pagamento for nulo")
        void deveLancarExcecaoQuandoTipoPagamentoNulo() {
            Venda venda = umaVendaBuilder()
                    .tipoPagamento(null)
                    .build();

            assertThrows(TipoPagamentoInvalidoException.class, 
                    () -> vendaService.adicionar(venda));
        }

        @Test
        @DisplayName("deve calcular desconto corretamente e salvar venda com PIX")
        void deveCalcularDescontoCorretamenteEPoisSalvarVenda() {
            Produto produto = umProdutoComPreco("produto-1", new BigDecimal("100.00"));
            when(produtoClient.buscarProduto("produto-1")).thenReturn(produto);
            when(fabrica.devolverImplementacao(TipoPagamento.PIX)).thenReturn(calculoDesconto);
            when(calculoDesconto.calcularDesconto(any())).thenReturn(new BigDecimal("90.00"));
            when(repository.save(any(Venda.class))).thenAnswer(inv -> inv.getArgument(0));

            Venda venda = umaVendaBuilder()
                    .tipoPagamento(TipoPagamento.PIX)
                    .idProduto(List.of("produto-1"))
                    .valorPago(new BigDecimal("90.00"))
                    .build();

            Venda resultado = vendaService.adicionar(venda);

            assertThat(resultado.getStatus()).isEqualTo(Status.APROVADA);
            assertThat(resultado.getValorCompra()).isEqualByComparingTo("90.00");
            verify(publisher).publicar(resultado);
        }

        @Test
        @DisplayName("deve rejeitar venda quando valor pago for insuficiente")
        void deveRejeitarVendaQuandoValorPagoInsuficiente() {
            Produto produto = umProdutoComPreco("produto-1", new BigDecimal("100.00"));
            when(produtoClient.buscarProduto("produto-1")).thenReturn(produto);
            when(fabrica.devolverImplementacao(TipoPagamento.CARTAO_CREDITO)).thenReturn(calculoDesconto);
            when(calculoDesconto.calcularDesconto(any())).thenReturn(new BigDecimal("100.00"));

            Venda venda = umaVendaBuilder()
                    .tipoPagamento(TipoPagamento.CARTAO_CREDITO)
                    .idProduto(List.of("produto-1"))
                    .valorPago(new BigDecimal("50.00"))
                    .build();

            assertThrows(ValorRecebidoInvalidoException.class, 
                    () -> vendaService.adicionar(venda));
            
            assertThat(venda.getStatus()).isEqualTo(Status.REJEITADA);
        }

        @Test
        @DisplayName("deve lançar exceção quando venda já existir")
        void deveLancarExcecaoQuandoVendaJaExistir() {
            Produto produto = umProdutoComPreco("produto-1", new BigDecimal("100.00"));
            when(produtoClient.buscarProduto("produto-1")).thenReturn(produto);
            when(fabrica.devolverImplementacao(TipoPagamento.PIX)).thenReturn(calculoDesconto);
            when(calculoDesconto.calcularDesconto(any())).thenReturn(new BigDecimal("90.00"));
            when(repository.save(any())).thenThrow(new DuplicateKeyException("duplicate"));

            Venda venda = umaVendaBuilder()
                    .tipoPagamento(TipoPagamento.PIX)
                    .idProduto(List.of("produto-1"))
                    .valorPago(new BigDecimal("90.00"))
                    .build();

            assertThrows(VendaJaExistenteException.class, 
                    () -> vendaService.adicionar(venda));
        }

        @Test
        @DisplayName("deve validar venda quando valor pago for maior que valor da compra")
        void deveValidarVendaQuandoValorPagoMaior() {
            Produto produto = umProdutoComPreco("produto-1", new BigDecimal("100.00"));
            when(produtoClient.buscarProduto("produto-1")).thenReturn(produto);
            when(fabrica.devolverImplementacao(TipoPagamento.PIX)).thenReturn(calculoDesconto);
            when(calculoDesconto.calcularDesconto(any())).thenReturn(new BigDecimal("90.00"));
            when(repository.save(any(Venda.class))).thenAnswer(inv -> inv.getArgument(0));

            Venda venda = umaVendaBuilder()
                    .tipoPagamento(TipoPagamento.PIX)
                    .idProduto(List.of("produto-1"))
                    .valorPago(new BigDecimal("100.00"))
                    .diferenca(new BigDecimal("10.00"))
                    .build();

            Venda resultado = vendaService.adicionar(venda);

            assertThat(resultado.getStatus()).isEqualTo(Status.APROVADA);
        }

        @Test
        @DisplayName("deve usar crédito do cliente quando valor pago for menor")
        void deveUsarCreditoClienteQuandoValorPagoMenor() {
            Produto produto = umProdutoComPreco("produto-1", new BigDecimal("100.00"));
            when(produtoClient.buscarProduto("produto-1")).thenReturn(produto);
            when(fabrica.devolverImplementacao(TipoPagamento.PIX)).thenReturn(calculoDesconto);
            when(calculoDesconto.calcularDesconto(any())).thenReturn(new BigDecimal("90.00"));
            when(repository.save(any(Venda.class))).thenAnswer(inv -> inv.getArgument(0));

            Venda venda = umaVendaBuilder()
                    .tipoPagamento(TipoPagamento.PIX)
                    .idProduto(List.of("produto-1"))
                    .valorPago(new BigDecimal("40.00"))
                    .cliente(umClienteComCredito(new BigDecimal("50.00")))
                    .build();

            Venda resultado = vendaService.adicionar(venda);

            assertThat(resultado.getStatus()).isEqualTo(Status.APROVADA);
        }
    }

    @Nested
    @DisplayName("Ao listar vendas")
    class ListarVendas {

        @Test
        @DisplayName("deve retornar todas as vendas cadastradas")
        void deveRetornarTodasVendas() {
            List<Venda> vendas = List.of(
                    umaVendaBuilder().build(),
                    umaVendaBuilder().id("venda-2").build()
            );
            when(repository.findAll()).thenReturn(vendas);

            List<Venda> resultado = vendaService.listar();

            assertThat(resultado).hasSize(2);
            verify(repository).findAll();
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não houver vendas")
        void deveRetornarListaVaziaQuandoNaoHouverVendas() {
            when(repository.findAll()).thenReturn(List.of());

            List<Venda> resultado = vendaService.listar();

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("Ao buscar uma venda por ID")
    class BuscarVendaPorId {

        @Test
        @DisplayName("deve retornar venda quando existir")
        void deveRetornarVendaQuandoExistir() {
            Venda venda = umaVendaBuilder().id("venda-1").build();
            when(repository.findById("venda-1")).thenReturn(Optional.of(venda));

            Optional<Venda> resultado = vendaService.buscar("venda-1");

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getId()).isEqualTo("venda-1");
        }

        @Test
        @DisplayName("deve retornar empty quando não existir")
        void deveRetornarEmptyQuandoNaoExistir() {
            when(repository.findById("nao-existe")).thenReturn(Optional.empty());

            Optional<Venda> resultado = vendaService.buscar("nao-existe");

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("Ao deletar uma venda")
    class DeletarVenda {

        @Test
        @DisplayName("deve deletar e retornar true quando existir")
        void deveDeletarQuandoExistir() {
            when(repository.existsById("venda-1")).thenReturn(true);

            boolean resultado = vendaService.deletar("venda-1");

            assertThat(resultado).isTrue();
            verify(repository).deleteById("venda-1");
        }

        @Test
        @DisplayName("deve retornar false quando não existir")
        void deveRetornarFalseQuandoNaoExistir() {
            when(repository.existsById("nao-existe")).thenReturn(false);

            boolean resultado = vendaService.deletar("nao-existe");

            assertThat(resultado).isFalse();
            verify(repository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("Ao atualizar uma venda")
    class AtualizarVenda {

        @Test
        @DisplayName("deve atualizar venda existente corretamente")
        void deveAtualizarVendaExistente() {
            Venda existente = umaVendaBuilder()
                    .id("venda-1")
                    .status(Status.APROVADA)
                    .build();

            Produto produto = umProdutoComPreco("produto-1", new BigDecimal("100.00"));

            when(repository.findById("venda-1")).thenReturn(Optional.of(existente));
            when(produtoClient.buscarProduto("produto-1")).thenReturn(produto);
            when(fabrica.devolverImplementacao(TipoPagamento.PIX)).thenReturn(calculoDesconto);
            when(calculoDesconto.calcularDesconto(any())).thenReturn(new BigDecimal("90.00"));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Venda atualizacao = umaVendaBuilder()
                    .id("venda-1")
                    .tipoPagamento(TipoPagamento.PIX)
                    .idProduto(List.of("produto-1"))
                    .valorPago(new BigDecimal("90.00"))
                    .cliente(umClienteComCredito(new BigDecimal("0.00")))
                    .build();

            Venda resultado = vendaService.atualizar(atualizacao);

            assertThat(resultado.getStatus()).isEqualTo(Status.APROVADA);
            verify(publisher).publicar(resultado);
        }

        @Test
        @DisplayName("deve lançar exceção quando venda não existir")
        void deveLancarExcecaoQuandoNaoExistir() {
            when(repository.findById("nao-existe")).thenReturn(Optional.empty());

            Venda venda = umaVendaBuilder().id("nao-existe").build();

            assertThrows(VendaNaoEncontradaException.class, 
                    () -> vendaService.atualizar(venda));
        }

        @Test
        @DisplayName("deve lançar exceção quando tipo pagamento for nulo na atualização")
        void deveLancarExcecaoQuandoTipoPagamentoNulo() {
            Venda venda = umaVendaBuilder()
                    .id("venda-1")
                    .tipoPagamento(null)
                    .build();

            assertThrows(TipoPagamentoInvalidoException.class, 
                    () -> vendaService.atualizar(venda));
        }
    }

    @Nested
    @DisplayName("Ao validar uma venda")
    class ValidarVenda {

        @Test
        @DisplayName("deve retornar true quando valor pago igual ao valor da compra")
        void deveRetornarTrueQuandoValoresIguais() {
            Venda venda = umaVendaBuilder()
                    .valorCompra(new BigDecimal("100.00"))
                    .valorPago(new BigDecimal("100.00"))
                    .cliente(umClienteComCredito(new BigDecimal("0.00")))
                    .build();

            boolean resultado = vendaService.validar(venda);

            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("deve retornar true quando diferenca for correta")
        void deveRetornarTrueQuandoDiferencaCorreta() {
            Venda venda = umaVendaBuilder()
                    .valorCompra(new BigDecimal("90.00"))
                    .valorPago(new BigDecimal("100.00"))
                    .diferenca(new BigDecimal("10.00"))
                    .cliente(umClienteComCredito(new BigDecimal("0.00")))
                    .build();

            boolean resultado = vendaService.validar(venda);

            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("deve retornar true quando credito completar valor")
        void deveRetornarTrueQuandoCreditoCompletar() {
            Venda venda = umaVendaBuilder()
                    .valorCompra(new BigDecimal("100.00"))
                    .valorPago(new BigDecimal("50.00"))
                    .diferenca(null)
                    .cliente(umClienteComCredito(new BigDecimal("50.00")))
                    .build();

            boolean resultado = vendaService.validar(venda);

            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("deve retornar false quando valor pago menor sem crédito suficiente")
        void deveRetornarFalseQuandoValorMenorSemCredito() {
            Venda venda = umaVendaBuilder()
                    .valorCompra(new BigDecimal("100.00"))
                    .valorPago(new BigDecimal("50.00"))
                    .diferenca(null)
                    .cliente(umClienteComCredito(new BigDecimal("25.00")))
                    .build();

            boolean resultado = vendaService.validar(venda);

            assertThat(resultado).isFalse();
        }
    }

    @Nested
    @DisplayName("Ao executar ações de status")
    class AcoesStatus {

        @Test
        @DisplayName("deve aprovar venda")
        void deveAprovarVenda() {
            Venda venda = umaVendaBuilder().build();

            vendaService.aprovar(venda);

            assertThat(venda.getStatus()).isEqualTo(Status.APROVADA);
        }

        @Test
        @DisplayName("deve cancelar venda")
        void deveCancelarVenda() {
            Venda venda = umaVendaBuilder().build();

            vendaService.cancelar(venda);

            assertThat(venda.getStatus()).isEqualTo(Status.CANCELADA);
        }

        @Test
        @DisplayName("deve marcar erro na venda")
        void deveMarcarErroVenda() {
            Venda venda = umaVendaBuilder().build();

            vendaService.error(venda);

            assertThat(venda.getStatus()).isEqualTo(Status.ERRO);
        }
    }

    // ========== HELPERS ==========

    private Venda.VendaBuilder umaVendaBuilder() {
        return Venda.builder()
                .id("venda-teste")
                .idProduto(List.of("produto-1"))
                .tipoPagamento(TipoPagamento.PIX)
                .valorPago(new BigDecimal("100.00"))
                .valorCompra(new BigDecimal("90.00"))
                .cliente(umClienteComCredito(new BigDecimal("0.00")));
    }

    private Venda.Cliente umClienteComCredito(BigDecimal credito) {
        return Venda.Cliente.builder()
                .id("cliente-1")
                .nome("João")
                .sobrenome("Silva")
                .credito(credito)
                .build();
    }

    private Produto umProdutoComPreco(String id, BigDecimal preco) {
        Produto produto = new Produto();
        produto.setId(id);
        produto.setPreco(preco);
        return produto;
    }
}
