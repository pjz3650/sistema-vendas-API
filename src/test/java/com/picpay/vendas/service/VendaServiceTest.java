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

        Venda obj = mock(Venda.class);

        when(repository.existsById(obj.getId())).thenReturn(true);

        boolean resultado = service.deletar(obj.getId());

        verify(repository, times(1)).deleteById(obj.getId());
        assertTrue(resultado);

    }

    @Test
    @DisplayName("Deveria retornar False ao chamar método deletar")
    void deveriaRetornarFalseAoChamarMetodoDeletar() {

        Venda obj = Venda.builder()
                .id(1L)
                .build();

        when(repository.existsById(1L)).thenReturn(false);

        boolean resultado = service.deletar(obj.getId());

        assertFalse(resultado);
        verify(repository, never()).deleteById(1L);

    }

    @Test
    @DisplayName("Deveria adicionar o objeto ao banco e retornar o mesmo")
    void deveriaAdicionarOObjetoAoBancoERetornarOMesmo() {

        Venda obj = mock(Venda.class);

        when(repository.save(obj)).thenReturn(obj);

        Venda objRetorno = service.adicionar(obj);

        assertNotNull(objRetorno);
        verify(repository, times(1)).save(obj);
    }

    @Test
    @DisplayName("Deveria retornar a soma dos preços dos produdos 1 e 2")
    void deveriaRetornarASomaDosPreçosDosProdudos1E2() {

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
    @DisplayName("Deveria retornar erro ao tentar adicionar uma venda")
    void deveriaRetornarErroAoTentarAdicionarUmaVenda() {

        Venda obj = mock(Venda.class);

        when(service.adicionar(obj)).thenThrow(new VendaJaExistenteException("Venda já existente"));

        assertThrows(VendaJaExistenteException.class, () -> service.adicionar(obj));

    }

    @Test
    @DisplayName("Deveria retornar todos os objetos da lista")
    void deveriaRetornarTodosOsObjetosDaLista() {

        Venda v1 = Venda.builder()
                .id(1L)
                .build();

        Venda v2 = Venda.builder()
                .id(2L)
                .build();

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

        Venda venda = Venda.builder()
                .id(1L)
                .build();


        when(repository.findById(anyLong())).thenReturn(Optional.of(venda));

        Optional<Venda> vendaResultado = service.buscar(venda.getId());

        boolean validacao = vendaResultado.isPresent();

        verify(repository, times(1)).findById(venda.getId());
        assertTrue(validacao);

    }

    @Test
    @DisplayName("Deveria retornar um objeto vazio")
    void deveriaRetornarUmObjetoVazio() {

        when(repository.findById(2L)).thenReturn(Optional.empty());

        Optional<Venda> vendaResultado = service.buscar(2L);

        verify(repository, times(1)).findById(2L);
        assertTrue(vendaResultado.isEmpty());

    }

    @Test
    @DisplayName("Deve alterar o objeto e retornar com valorCompra calculado")
    void deveAlterarOObjetoERetornarOMesmo() {

        Venda objUpd = Venda.builder()
                .id(1L)
                .idProduto(List.of(1L, 2L))
                .build();

        Produto produto = new Produto();
        produto.setPreco(10.0);

        when(repository.findById(anyLong())).thenReturn(Optional.of(objUpd));
        when(client.buscarProduto(anyLong())).thenReturn(produto);
        when(repository.save(any())).thenReturn(objUpd);

        Venda objRetorno = service.atualizar(objUpd);

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(objUpd);
        verify(client, times(1)).buscarProduto(1L);
        verify(client, times(1)).buscarProduto(2L);
        assertEquals(20.0, objRetorno.getValorCompra());
    }

    @Test
    @DisplayName("Deve retornar um erro")
    void deveRetornarUmErro() {

        Venda objUpd = Venda.builder()
                .id(2L)
                .idProduto(List.of(1L))
                .build();

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.atualizar(objUpd));

        verify(repository, times(1)).findById(2L);
        verify(repository, never()).save(any());
    }
}

