package com.picpay.vendas.service;


import com.picpay.vendas.model.Produto;
import com.picpay.vendas.model.Venda;
import com.picpay.vendas.repository.ProdutoClient;
import com.picpay.vendas.repository.VendaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class VendaServiceRetryTest {

    @Autowired
    private VendaService service;

    @MockitoBean
    private ProdutoClient client;

    @MockitoBean
    private VendaRepository repository;

    @Test
    @DisplayName("Deveria tentar 5 vezes e cair no fallback")
    void deveriaTestar5VezesECairNoFallback() {

        Venda obj = Venda.builder()
                .id(1L)
                .idProduto(List.of(1L))
                .build();

        when(client.buscarProduto(anyLong()))
                .thenThrow(new RuntimeException("API indisponível"));

        assertThrows(RuntimeException.class, () -> service.adicionar(obj));

        verify(client, times(5)).buscarProduto(1L);
    }

    @Test
    @DisplayName("Deveria ter sucesso na segunda tentativa")
    void deveriaSuccederNaSegundaTentativa() {

        Venda obj = Venda.builder()
                .id(1L)
                .idProduto(List.of(1L))
                .build();

        Produto produto = new Produto();
        produto.setPreco(10.0);

        when(client.buscarProduto(anyLong()))
                .thenThrow(new RuntimeException("falhou"))
                .thenReturn(produto);

        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Venda resultado = service.adicionar(obj);

        assertNotNull(resultado);
        assertEquals(10.0, resultado.getValorCompra());
        verify(client, times(2)).buscarProduto(1L);
    }
}

