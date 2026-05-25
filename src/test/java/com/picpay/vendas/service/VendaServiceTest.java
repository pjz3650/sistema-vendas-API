package com.picpay.vendas.service;

import com.picpay.vendas.exception.TipoPagamentoInvalidoException;
import com.picpay.vendas.exception.VendaJaExistenteException;
import com.picpay.vendas.exception.VendaNaoEncontradaException;
import com.picpay.vendas.model.CalculoDesconto;
import com.picpay.vendas.model.Fabrica;
import com.picpay.vendas.model.Produto;
import com.picpay.vendas.model.TipoPagamento;
import com.picpay.vendas.model.Venda;
import java.math.BigDecimal;

import com.picpay.vendas.publisher.VendaPublisher;
import com.picpay.vendas.repository.ProdutoClient;
import com.picpay.vendas.repository.VendaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock
    private VendaRepository repository;

    @Mock
    private ProdutoClient client;

    @Mock
    private Fabrica fabrica;

    @Mock
    private VendaPublisher publisher;

    @InjectMocks
    private VendaService service;

    @Test
    @DisplayName("Deveria retornar True ao chamar método deletar")
    void deveriaRetornarTrueAoChamarMetodoDeletar() {
        when(repository.existsById("id-1")).thenReturn(true);

        boolean resultado = service.deletar("id-1");

        verify(repository, times(1)).deleteById("id-1");
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deveria retornar False ao chamar método deletar")
    void deveriaRetornarFalseAoChamarMetodoDeletar() {
        when(repository.existsById("id-1")).thenReturn(false);

        boolean resultado = service.deletar("id-1");

        assertFalse(resultado);
        verify(repository, never()).deleteById("id-1");
    }

    @Test
    @DisplayName("Deveria adicionar venda com PIX e aplicar desconto de 10%")
    void deveriaAdicionarVendaComPixEAplicarDesconto() {
        Produto produto = new Produto();
        produto.setPreco(BigDecimal.valueOf(100.0));

        Venda venda = Venda.builder()
                .idProduto(List.of(1L))
                .tipoPagamento(TipoPagamento.PIX)
                .build();

        CalculoDesconto descontoPix = mock(CalculoDesconto.class);
        when(descontoPix.calcularDesconto(any())).thenReturn(BigDecimal.valueOf(90.0));
        when(fabrica.devolverImplementacao(TipoPagamento.PIX)).thenReturn(descontoPix);
        when(client.buscarProduto(1L)).thenReturn(produto);
        when(repository.save(venda)).thenReturn(venda);

        Venda resultado = service.adicionar(venda);

        assertEquals(0, BigDecimal.valueOf(90.0).compareTo(resultado.getValorCompra()));
        verify(repository, times(1)).save(venda);
    }

    @Test
    @DisplayName("Deveria adicionar venda com CARTAO_DEBITO e aplicar desconto de 5%")
    void deveriaAdicionarVendaComCartaoDebitoEAplicarDesconto() {
        Produto produto = new Produto();
        produto.setPreco(BigDecimal.valueOf(100.0));

        Venda venda = Venda.builder()
                .idProduto(List.of(1L))
                .tipoPagamento(TipoPagamento.CARTAO_DEBITO)
                .build();

        CalculoDesconto descontoDebito = mock(CalculoDesconto.class);
        when(descontoDebito.calcularDesconto(any())).thenReturn(BigDecimal.valueOf(95.0));
        when(fabrica.devolverImplementacao(TipoPagamento.CARTAO_DEBITO)).thenReturn(descontoDebito);
        when(client.buscarProduto(1L)).thenReturn(produto);
        when(repository.save(venda)).thenReturn(venda);

        Venda resultado = service.adicionar(venda);

        assertEquals(0, BigDecimal.valueOf(95.0).compareTo(resultado.getValorCompra()));
        verify(repository, times(1)).save(venda);
    }

    @Test
    @DisplayName("Deveria adicionar venda com CARTAO_CREDITO sem desconto")
    void deveriaAdicionarVendaComCartaoCreditoSemDesconto() {
        Produto produto = new Produto();
        produto.setPreco(BigDecimal.valueOf(100.0));

        Venda venda = Venda.builder()
                .idProduto(List.of(1L))
                .tipoPagamento(TipoPagamento.CARTAO_CREDITO)
                .build();

        CalculoDesconto descontoCredito = mock(CalculoDesconto.class);
        when(descontoCredito.calcularDesconto(any())).thenReturn(BigDecimal.valueOf(100.0));
        when(fabrica.devolverImplementacao(TipoPagamento.CARTAO_CREDITO)).thenReturn(descontoCredito);
        when(client.buscarProduto(1L)).thenReturn(produto);
        when(repository.save(venda)).thenReturn(venda);

        Venda resultado = service.adicionar(venda);

        assertEquals(0, BigDecimal.valueOf(100.0).compareTo(resultado.getValorCompra()));
        verify(repository, times(1)).save(venda);
    }

    @Test
    @DisplayName("Deveria retornar a soma dos preços dos produtos 1 e 2 com PIX")
    void deveriaRetornarASomaDosPrecosDosProudutosCom2ItensPix() {
        Produto produto = new Produto();
        produto.setPreco(BigDecimal.valueOf(10.0));

        Venda venda = Venda.builder()
                .idProduto(List.of(1L, 2L))
                .tipoPagamento(TipoPagamento.PIX)
                .build();

        CalculoDesconto descontoPix = mock(CalculoDesconto.class);
        when(descontoPix.calcularDesconto(any())).thenReturn(BigDecimal.valueOf(18.0));
        when(fabrica.devolverImplementacao(TipoPagamento.PIX)).thenReturn(descontoPix);
        when(client.buscarProduto(anyLong())).thenReturn(produto);
        when(repository.save(venda)).thenReturn(venda);

        Venda resultado = service.adicionar(venda);

        assertEquals(0, BigDecimal.valueOf(18.0).compareTo(resultado.getValorCompra()));
        verify(client, times(1)).buscarProduto(1L);
        verify(client, times(1)).buscarProduto(2L);
    }

    @Test
    @DisplayName("Deveria lançar TipoPagamentoInvalidoException quando tipoPagamento for null")
    void deveriaLancarExcecaoQuandoTipoPagamentoForNull() {
        Venda venda = Venda.builder()
                .idProduto(List.of(1L))
                .tipoPagamento(null)
                .build();

        assertThrows(TipoPagamentoInvalidoException.class, () -> service.adicionar(venda));

        verify(repository, never()).save(any());
        verify(client, never()).buscarProduto(anyLong());
    }

    @Test
    @DisplayName("Deveria lançar VendaJaExistenteException ao tentar adicionar venda duplicada")
    void deveriaLancarExcecaoAoAdicionarVendaDuplicada() {
        Venda venda = Venda.builder()
                .idProduto(List.of())
                .tipoPagamento(TipoPagamento.PIX)
                .build();

        CalculoDesconto descontoPix = mock(CalculoDesconto.class);
        when(descontoPix.calcularDesconto(any())).thenReturn(BigDecimal.ZERO);
        when(fabrica.devolverImplementacao(TipoPagamento.PIX)).thenReturn(descontoPix);
        when(repository.save(any())).thenThrow(new DuplicateKeyException("duplicate"));

        assertThrows(VendaJaExistenteException.class, () -> service.adicionar(venda));
    }

    @Test
    @DisplayName("Deveria retornar todos os objetos da lista")
    void deveriaRetornarTodosOsObjetosDaLista() {
        Venda v1 = Venda.builder().id("id-1").build();
        Venda v2 = Venda.builder().id("id-2").build();
        List<Venda> vendas = List.of(v1, v2);

        when(repository.findAll()).thenReturn(vendas);

        List<Venda> resultado = service.listar();

        verify(repository, times(1)).findAll();
        assertSame(vendas, resultado);
    }

    @Test
    @DisplayName("Deveria retornar uma lista vazia")
    void deveriaRetornarUmaListaVazia() {
        List<Venda> vendas = List.of();

        when(repository.findAll()).thenReturn(vendas);

        List<Venda> resultado = service.listar();

        verify(repository, times(1)).findAll();
        assertSame(vendas, resultado);
    }

    @Test
    @DisplayName("Deveria retornar o objeto ao buscar por id existente")
    void deveriaRetornarOObjeto() {
        Venda venda = Venda.builder().id("id-1").build();

        when(repository.findById("id-1")).thenReturn(Optional.of(venda));

        Optional<Venda> resultado = service.buscar("id-1");

        assertTrue(resultado.isPresent());
        verify(repository, times(1)).findById("id-1");
    }

    @Test
    @DisplayName("Deveria retornar Optional vazio ao buscar id inexistente")
    void deveriaRetornarOptionalVazioAoBuscarIdInexistente() {
        when(repository.findById("id-99")).thenReturn(Optional.empty());

        Optional<Venda> resultado = service.buscar("id-99");

        assertTrue(resultado.isEmpty());
        verify(repository, times(1)).findById("id-99");
    }

    @Test
    @DisplayName("Deve atualizar venda com PIX e recalcular valorCompra com desconto")
    void deveAtualizarVendaComPixERecalcularValor() {
        Venda vendaExistente = Venda.builder()
                .id("id-1")
                .idProduto(List.of(1L, 2L))
                .tipoPagamento(TipoPagamento.PIX)
                .build();

        Produto produto = new Produto();
        produto.setPreco(BigDecimal.valueOf(10.0));

        CalculoDesconto descontoPix = mock(CalculoDesconto.class);
        when(descontoPix.calcularDesconto(any())).thenReturn(BigDecimal.valueOf(18.0));
        when(fabrica.devolverImplementacao(TipoPagamento.PIX)).thenReturn(descontoPix);
        when(repository.findById("id-1")).thenReturn(Optional.of(vendaExistente));
        when(client.buscarProduto(anyLong())).thenReturn(produto);
        when(repository.save(any())).thenReturn(vendaExistente);

        Venda resultado = service.atualizar(vendaExistente);

        assertEquals(0, BigDecimal.valueOf(18.0).compareTo(resultado.getValorCompra()));
        verify(repository, times(1)).findById("id-1");
        verify(repository, times(1)).save(vendaExistente);
    }

    @Test
    @DisplayName("Deve lançar VendaNaoEncontradaException ao tentar atualizar venda inexistente")
    void deveLancarExcecaoAoAtualizarVendaInexistente() {
        Venda venda = Venda.builder()
                .id("id-99")
                .idProduto(List.of(1L))
                .tipoPagamento(TipoPagamento.PIX)
                .build();

        when(repository.findById("id-99")).thenReturn(Optional.empty());

        assertThrows(VendaNaoEncontradaException.class, () -> service.atualizar(venda));

        verify(repository, times(1)).findById("id-99");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar TipoPagamentoInvalidoException ao atualizar venda com tipoPagamento null")
    void deveLancarExcecaoAoAtualizarVendaComTipoPagamentoNull() {
        Venda vendaExistente = Venda.builder()
                .id("id-1")
                .idProduto(List.of(1L))
                .tipoPagamento(null)
                .build();

        assertThrows(TipoPagamentoInvalidoException.class, () -> service.atualizar(vendaExistente));

        verify(repository, never()).findById(any());
        verify(repository, never()).save(any());
    }
}
