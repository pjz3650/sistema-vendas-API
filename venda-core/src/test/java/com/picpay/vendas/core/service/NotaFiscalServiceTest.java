package com.picpay.vendas.core.service;

import com.picpay.vendas.core.exception.VendaNaoEncontradaException;
import com.picpay.vendas.core.model.*;
import com.picpay.vendas.core.repository.ProdutoClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotaFiscalService - Testes Unitários")
class NotaFiscalServiceTest {

    @Mock
    private VendaService vendaService;

    @Mock
    private ProdutoClient produtoClient;

    private NotaFiscalService notaFiscalService;

    @BeforeEach
    void setUp() {
        notaFiscalService = new NotaFiscalService(vendaService, produtoClient);
    }

    @Nested
    @DisplayName("Ao gerar nota fiscal")
    class GerarNotaFiscal {

        @Test
        @DisplayName("deve gerar nota fiscal corretamente para venda aprovada")
        void deveGerarNotaFiscalCorretamente() {
            Venda venda = umaVendaAprovada();
            VendaEvent event = VendaEvent.fromVenda(venda);

            when(vendaService.buscar("venda-1")).thenReturn(Optional.of(venda));
            when(produtoClient.buscarProduto("produto-1")).thenReturn(umProduto());

            NotaFiscal nota = notaFiscalService.gerarNota(event);

            assertThat(nota.getNumero()).isEqualTo("venda-1");
            assertThat(nota.getValor()).isEqualByComparingTo("90.00");
            assertThat(nota.getNomeCliente()).isEqualTo("João");
            assertThat(nota.getProdutos()).hasSize(1);
        }

        @Test
        @DisplayName("deve lançar exceção quando venda não for encontrada")
        void deveLancarExcecaoQuandoVendaNaoEncontrada() {
            VendaEvent event = umVendaEvent();

            when(vendaService.buscar("venda-1")).thenReturn(Optional.empty());

            assertThrows(VendaNaoEncontradaException.class,
                    () -> notaFiscalService.gerarNota(event));
        }

        @Test
        @DisplayName("deve lançar exceção quando venda não estiver aprovada")
        void deveLancarExcecaoQuandoVendaNaoAprovada() {
            Venda venda = umaVendaBuilder().status(Status.CRIADA).build();
            VendaEvent event = VendaEvent.fromVenda(venda);

            when(vendaService.buscar("venda-1")).thenReturn(Optional.of(venda));

            assertThrows(IllegalStateException.class,
                    () -> notaFiscalService.gerarNota(event));
        }

        @Test
        @DisplayName("deve lançar exceção quando venda estiver rejeitada")
        void deveLancarExcecaoQuandoVendaRejeitada() {
            Venda venda = umaVendaBuilder().status(Status.REJEITADA).build();
            VendaEvent event = VendaEvent.fromVenda(venda);

            when(vendaService.buscar("venda-1")).thenReturn(Optional.of(venda));

            assertThrows(IllegalStateException.class,
                    () -> notaFiscalService.gerarNota(event));
        }

        @Test
        @DisplayName("deve buscar todos os produtos da venda")
        void deveBuscarTodosProdutos() {
            Venda venda = umaVendaAprovada();
            venda.setIdProduto(List.of("produto-1", "produto-2", "produto-3"));
            VendaEvent event = VendaEvent.fromVenda(venda);

            when(vendaService.buscar("venda-1")).thenReturn(Optional.of(venda));
            when(produtoClient.buscarProduto(anyString())).thenReturn(umProduto());

            NotaFiscal nota = notaFiscalService.gerarNota(event);

            assertThat(nota.getProdutos()).hasSize(3);
            verify(produtoClient, times(3)).buscarProduto(anyString());
        }

        @Test
        @DisplayName("deve gerar nota com múltiplos produtos corretamente")
        void deveGerarNotaComMultiplosProdutos() {
            Produto produto1 = umProdutoComPreco("produto-1", new BigDecimal("100.00"));
            Produto produto2 = umProdutoComPreco("produto-2", new BigDecimal("50.00"));

            Venda venda = umaVendaBuilder()
                    .id("venda-1")
                    .status(Status.APROVADA)
                    .idProduto(List.of("produto-1", "produto-2"))
                    .build();

            VendaEvent event = VendaEvent.fromVenda(venda);

            when(vendaService.buscar("venda-1")).thenReturn(Optional.of(venda));
            when(produtoClient.buscarProduto("produto-1")).thenReturn(produto1);
            when(produtoClient.buscarProduto("produto-2")).thenReturn(produto2);

            NotaFiscal nota = notaFiscalService.gerarNota(event);

            assertThat(nota.getProdutos()).hasSize(2);
            assertThat(nota.getProdutos().get(0).getPreco()).isEqualByComparingTo("100.00");
            assertThat(nota.getProdutos().get(1).getPreco()).isEqualByComparingTo("50.00");
        }
    }

    // ========== HELPERS ==========

    private Venda.VendaBuilder umaVendaBuilder() {
        return Venda.builder()
                .id("venda-1")
                .idProduto(List.of("produto-1"))
                .tipoPagamento(TipoPagamento.PIX)
                .valorPago(new BigDecimal("90.00"))
                .valorCompra(new BigDecimal("90.00"))
                .cliente(Venda.Cliente.builder()
                        .id("cliente-1")
                        .nome("João")
                        .sobrenome("Silva")
                        .credito(BigDecimal.ZERO)
                        .build());
    }

    private Venda umaVendaAprovada() {
        return umaVendaBuilder()
                .status(Status.APROVADA)
                .build();
    }

    private VendaEvent umVendaEvent() {
        return VendaEvent.builder()
                .id("venda-1")
                .idProduto(List.of("produto-1"))
                .valorCompra(new BigDecimal("90.00"))
                .cliente(VendaEvent.ClienteEvent.builder()
                        .id("cliente-1")
                        .nome("João")
                        .sobrenome("Silva")
                        .credito(BigDecimal.ZERO)
                        .build())
                .build();
    }

    private Produto umProduto() {
        Produto produto = new Produto();
        produto.setId("produto-1");
        produto.setNome("Produto Teste");
        produto.setPreco(new BigDecimal("100.00"));
        return produto;
    }

    private Produto umProdutoComPreco(String id, BigDecimal preco) {
        Produto produto = new Produto();
        produto.setId(id);
        produto.setNome("Produto " + id);
        produto.setPreco(preco);
        return produto;
    }
}
