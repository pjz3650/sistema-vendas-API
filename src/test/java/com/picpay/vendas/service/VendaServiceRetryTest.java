package com.picpay.vendas.service;


import com.picpay.vendas.exception.ErroAoConectarComMsException;
import com.picpay.vendas.model.Produto;
import com.picpay.vendas.model.TipoPagamento;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {
        "app.client-id=test-client",
        "integrations.produto.api-version=v1"
})
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
        Venda venda = Venda.builder()
                .id("id-1")
                .idProduto(List.of(1L))
                .tipoPagamento(TipoPagamento.PIX)
                .build();

        when(client.buscarProduto(anyLong()))
                .thenThrow(new ErroAoConectarComMsException("API indisponível"));

        assertThrows(ErroAoConectarComMsException.class, () -> service.adicionar(venda));

        verify(client, times(5)).buscarProduto(1L);
    }

    @Test
    @DisplayName("Deveria ter sucesso na segunda tentativa")
    void deveriaTerSucessoNaSegundaTentativa() {
        Venda venda = Venda.builder()
                .id("id-1")
                .idProduto(List.of(1L))
                .tipoPagamento(TipoPagamento.PIX)
                .build();

        Produto produto = new Produto();
        produto.setPreco(100.0);

        when(client.buscarProduto(anyLong()))
                .thenThrow(new ErroAoConectarComMsException("falhou"))
                .thenReturn(produto);

        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Venda resultado = service.adicionar(venda);

        assertNotNull(resultado);
        assertEquals(90.0, resultado.getValorCompra());
        verify(client, times(2)).buscarProduto(1L);
    }
}
