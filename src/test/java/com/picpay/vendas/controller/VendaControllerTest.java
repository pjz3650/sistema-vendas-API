package com.picpay.vendas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.picpay.vendas.exception.VendaJaExistenteException;
import com.picpay.vendas.model.Venda;
import com.picpay.vendas.exception.ErroAoConectarComMs;
import com.picpay.vendas.service.VendaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
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


@WebMvcTest({VendaController.class, OpenApiController.class})
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

        Venda v1 = Venda.builder().id("id-1").build();
        Venda v2 = Venda.builder().id("id-2").build();

        List<Venda> vendas = List.of(v1, v2);

        when(service.listar()).thenReturn(vendas);

        mockMvc.perform(get("/v2/test-api/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("id-1"))
                .andExpect(jsonPath("$[1].id").value("id-2"));
    }

    @Test
    @DisplayName("Deveria retornar uma lista vazia")
    public void deveriaRetornarUmaListaVazia() throws Exception {

        when(service.listar()).thenReturn(List.of());

        mockMvc.perform(get("/v2/test-api/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Deveria retornar mensagem de venda deletada")
    void deveriaRetornarMensagemDeVendaDeletada() throws Exception {

        when(service.deletar("id-1")).thenReturn(true);

        mockMvc.perform(delete("/v2/test-api/deletar/id-1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Venda deletada!"));
    }

    @Test
    @DisplayName("Deveria retornar erro ao deletar")
    void deveriaRetornarErroAoDeletar() throws Exception {

        when(service.deletar("id-1")).thenReturn(false);

        mockMvc.perform(delete("/v2/test-api/deletar/id-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deveria retornar mensagem de sucesso ao inserir venda")
    void deveriaRetornarMenssagemDeSucessoAoInserirVenda() throws Exception {

        Venda obj = Venda.builder().id("id-1").build();

        when(service.adicionar(any())).thenReturn(obj);

        mockMvc.perform(post("/v2/test-api/adicionar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(obj)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deveria retornar mensagem de venda já registrada ao inserir venda")
    void deveriaRetornarMensagemDeVendaJaRegistradaAoInserirVenda() throws Exception {

        Venda obj = Venda.builder().id("id-1").build();

        when(service.adicionar(any())).thenThrow(new VendaJaExistenteException("Essa venda já foi registrada..."));

        mockMvc.perform(post("/v2/test-api/adicionar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(obj)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Essa venda já foi registrada..."));
    }

    @Test
    @DisplayName("Deveria retornar mensagem de erro ao inserir venda")
    void deveriaRetornarMensagemDeErroAoInserirVenda() throws Exception {

        Venda obj = Venda.builder().id("id-1").build();

        when(service.adicionar(any())).thenThrow(new ErroAoConectarComMs("Serviço de produtos indisponível"));

        mockMvc.perform(post("/v2/test-api/adicionar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(obj)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("Serviço de produtos indisponível"));
    }

    @Test
    @DisplayName("Deveria retornar objeto")
    void deveriaRetornarObjeto() throws Exception {

        Venda obj = Venda.builder().id("id-1").build();

        when(service.buscar("id-1")).thenReturn(Optional.of(obj));

        mockMvc.perform(get("/v2/test-api/procurar/id-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("id-1"));
    }

    @Test
    @DisplayName("Deveria retornar 404 quando venda não encontrada")
    void deveriaRetornar404QuandoVendaNaoEncontrada() throws Exception {

        when(service.buscar("id-99")).thenReturn(Optional.empty());

        mockMvc.perform(get("/v2/test-api/procurar/id-99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deveria alterar objeto cadastrado e retornar objeto atualizado")
    void deveriaAlterarObjetoCadastradoERetornarObjetoAtualizado() throws Exception {

        Venda obj = Venda.builder().id("id-1").build();

        when(service.atualizar(any())).thenReturn(obj);

        mockMvc.perform(put("/v2/test-api/atualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(obj)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("id-1"));
    }

    @Test
    @DisplayName("Deveria retornar 500 ao tentar atualizar objeto com erro interno")
    void deveriaRetornar500AoTentarAtualizarObjetoComErroInterno() throws Exception {

        Venda obj = Venda.builder().id("id-1").build();

        doThrow(new RuntimeException("Venda não encontrada")).when(service).atualizar(any());

        mockMvc.perform(put("/v2/test-api/atualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(obj)))
                .andExpect(status().isInternalServerError());
    }
}
