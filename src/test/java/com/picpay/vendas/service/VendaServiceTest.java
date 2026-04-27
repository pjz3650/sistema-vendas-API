package com.picpay.vendas.service;

import com.picpay.vendas.exception.VendaJaExistenteException;
import com.picpay.vendas.model.Produto;
import com.picpay.vendas.model.Venda;
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
    @DisplayName("Deveria adicionar o objeto ao banco e retornar o mesmo")
    void deveriaAdicionarOObjetoAoBancoERetornarOMesmo() {

        Venda obj = Venda.builder()
                .idProduto(List.of())
                .build();

        when(repository.save(obj)).thenReturn(obj);

        Venda objRetorno = service.adicionar(obj);

        assertNotNull(objRetorno);
        verify(repository, times(1)).save(obj);
    }

    @Test
    @DisplayName("Deveria retornar a soma dos preços dos produtos 1 e 2")
    void deveriaRetornarASomaDosPreçosDosProdutos1E2() {

        List<Long> idsProdutos = List.of(1L, 2L);

        Venda obj = Venda.builder()
                .idProduto(idsProdutos)
                .build();

        Produto produto = new Produto();
        produto.setPreco(10.0);

        when(client.buscarProduto(anyLong())).thenReturn(produto);
        when(repository.save(obj)).thenReturn(obj);

        Venda objRetorno = service.adicionar(obj);

        double valorVenda = objRetorno.getValorCompra();

        double valorCorreto = 10.0 + 10.0;

        assertEquals(valorCorreto, valorVenda);
        verify(repository, times(1)).save(obj);
        verify(client, times(1)).buscarProduto(1L);
        verify(client, times(1)).buscarProduto(2L);
    }

    @Test
    @DisplayName("Deveria retornar erro ao tentar adicionar uma venda duplicada")
    void deveriaRetornarErroAoTentarAdicionarUmaVendaDuplicada() {

        Venda obj = Venda.builder()
                .idProduto(List.of())
                .build();

        when(repository.save(any())).thenThrow(new DuplicateKeyException("duplicate"));

        assertThrows(VendaJaExistenteException.class, () -> service.adicionar(obj));
    }

    @Test
    @DisplayName("Deveria retornar todos os objetos da lista")
    void deveriaRetornarTodosOsObjetosDaLista() {

        Venda v1 = Venda.builder().id("id-1").build();
        Venda v2 = Venda.builder().id("id-2").build();

        List<Venda> vendas = List.of(v1, v2);

        when(repository.findAll()).thenReturn(vendas);

        List<Venda> vendasResultado = service.listar();

        verify(repository, times(1)).findAll();
        assertSame(vendas, vendasResultado);
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
    @DisplayName("Deveria retornar o objeto")
    void deveriaRetornarOObjeto() {

        Venda venda = Venda.builder().id("id-1").build();

        when(repository.findById("id-1")).thenReturn(Optional.of(venda));

        Optional<Venda> vendaResultado = service.buscar("id-1");

        assertTrue(vendaResultado.isPresent());
        verify(repository, times(1)).findById("id-1");
    }

    @Test
    @DisplayName("Deveria retornar um objeto vazio")
    void deveriaRetornarUmObjetoVazio() {

        when(repository.findById("id-2")).thenReturn(Optional.empty());

        Optional<Venda> vendaResultado = service.buscar("id-2");

        verify(repository, times(1)).findById("id-2");
        assertTrue(vendaResultado.isEmpty());
    }

    @Test
    @DisplayName("Deve alterar o objeto e retornar com valorCompra calculado")
    void deveAlterarOObjetoERetornarOMesmo() {

        Venda objUpd = Venda.builder()
                .id("id-1")
                .idProduto(List.of(1L, 2L))
                .build();

        Produto produto = new Produto();
        produto.setPreco(10.0);

        when(repository.findById("id-1")).thenReturn(Optional.of(objUpd));
        when(client.buscarProduto(anyLong())).thenReturn(produto);
        when(repository.save(any())).thenReturn(objUpd);

        Venda objRetorno = service.atualizar(objUpd);

        verify(repository, times(1)).findById("id-1");
        verify(repository, times(1)).save(objUpd);
        verify(client, times(1)).buscarProduto(1L);
        verify(client, times(1)).buscarProduto(2L);
        assertEquals(20.0, objRetorno.getValorCompra());
    }

    @Test
    @DisplayName("Deve retornar um erro ao tentar atualizar venda inexistente")
    void deveRetornarUmErroAoTentarAtualizarVendaInexistente() {

        Venda objUpd = Venda.builder()
                .id("id-99")
                .idProduto(List.of(1L))
                .build();

        when(repository.findById("id-99")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.atualizar(objUpd));

        verify(repository, times(1)).findById("id-99");
        verify(repository, never()).save(any());
    }
}
