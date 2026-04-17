package com.picpay.vendas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.picpay.vendas.exception.VendaJaExistenteException;
import com.picpay.vendas.model.Venda;
import com.picpay.vendas.exception.ConflitoVendaException;
import com.picpay.vendas.service.VendaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(VendaController.class)
class VendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VendaService service;

    @Test
    @DisplayName("Deveria retornar todos os objetos da lista")
    public void deveriaRetornarTodosOsObjetosDaLista() throws Exception {

        Venda v1 = Venda.builder()
                .id(1L)
                .build();

        Venda v2 = Venda.builder()
                .id(2L)
                .build();

        List<Venda> vendas = List.of(v1, v2);

        when(service.listar()).thenReturn(vendas);

        mockMvc.perform(get("/v2/test-api/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

    }

    @Test
    @DisplayName("Deveria retornar uma lista vazia")
    public void deveriaRetornarUmaListaVazia() throws Exception {

        List<Venda> vendas = List.of();

        when(service.listar()).thenReturn(vendas);

        mockMvc.perform(get("/v2/test-api/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

    }

    @Test
    @DisplayName("Deveria retornar mensagem de venda deletada")
    void deveriaRetornarMensagemDeVendaDeletada() throws Exception {

        Venda obj = mock(Venda.class);

        when(service.deletar(1L)).thenReturn(true);

        mockMvc.perform(delete("/v2/test-api/deletar/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Produto deletado!!"));
    }

    @Test
    @DisplayName("Deveria retornar erro ao deletar")
    void deveriaRetornarErroAoDeletar() throws Exception {

        Venda obj = mock(Venda.class);

        when(service.deletar(1L)).thenReturn(false);

        mockMvc.perform(delete("/v2/test-api/deletar/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deveria retronar mensagem de sucesso ao inserir venda")
    void deveriaRetornarMenssagemDeSucessoAoInserirVenda() throws Exception {

        Venda obj = Venda.builder().id(1L).build();

        when(service.adicionar(obj)).thenReturn(obj);

        mockMvc.perform(post("/v2/test-api/adicionar")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(obj)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deveria retornar mensagem de venda já registrada inserir venda")
    void deveriaRetornarMensagemDeVendaJaRegistradaAoInserirVenda() throws Exception {

        Venda obj = Venda.builder().id(1L).build();

        when(service.adicionar(any())).thenThrow(new VendaJaExistenteException("Essa venda já foi registrada..."));

        mockMvc.perform(post("/v2/test-api/adicionar")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(obj)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Essa venda já foi registrada..."));
    }

    @Test
    @DisplayName("Deveria retornar mensagem de erro ao inserir venda")
    void deveriaRetornarMensagemDeErroAoInserirVenda() throws Exception {

        Venda obj = Venda.builder()
                .id(1L)
                .build();

        when(service.adicionar(any())).thenThrow(new ConflitoVendaException("Serviço de produtos indisponível"));

        mockMvc.perform(post("/v2/test-api/adicionar")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(obj)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("Serviço de produtos indisponível"));
    }

    @Test
    @DisplayName("Deveria retornar objeto")
    void deveriaRetornarObjeto() throws Exception {

        Venda obj = Venda.builder()
                .id(1L)
                .build();

        when(service.buscar(obj.getId())).thenReturn(Optional.of(obj));

        mockMvc.perform(get("/v2/test-api/procurar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

    }

    @Test
    @DisplayName("Deveria retornar um objeto vazio")
    void deveriaRetornarUmObjetoVazio() throws Exception {

        when(service.buscar(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/v2/test-api/procurar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("Deveria alterar objeto cadastrado e retornar objeto atualizado")
    void deveriaAlterarObjetoCadastradoERetornarObjetoAtualizado() throws Exception {

        Venda obj = Venda.builder()
                .id(1L)
                .build();

        when(service.atualizar(obj)).thenReturn(obj);

        mockMvc.perform(put("/v2/test-api/atualizar")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(obj)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

    }

    @Test
    @DisplayName("Deveria retornar erro ao tentar atualizar objeto")
    void deveriaRetornarErroAoTentarAtualizarObjeto() throws Exception {

        Venda obj = mock(Venda.class);

        doThrow(new RuntimeException("Venda não encontrada")).when(service).atualizar(any());

        mockMvc.perform(put("/v2/test-api/atualizar")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(obj)))
                .andExpect(status().isNotFound());

    }

}