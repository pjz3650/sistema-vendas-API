package com.picpay.vendas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.picpay.vendas.exception.ErroAoConectarComMsException;
import com.picpay.vendas.exception.TipoPagamentoInvalidoException;
import com.picpay.vendas.exception.VendaJaExistenteException;
import com.picpay.vendas.exception.VendaNaoEncontradaException;
import com.picpay.vendas.model.TipoPagamento;
import com.picpay.vendas.model.Venda;
import com.picpay.vendas.service.VendaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;


@WebMvcTest(VendaController.class)
class VendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VendaService service;

    private Venda vendaValida() {
        return Venda.builder()
                .id("id-1")
                .tipoPagamento(TipoPagamento.PIX)
                .idProduto(List.of(1L))
                .cliente(Venda.Cliente.builder()
                        .id("cli-1")
                        .nome("João")
                        .sobrenome("Silva")
                        .credito(BigDecimal.valueOf(500))
                        .build())
                .build();
    }


    @Test
    @DisplayName("Deveria retornar todos os objetos da lista")
    void deveriaRetornarTodosOsObjetosDaLista() throws Exception {
        Venda v1 = Venda.builder().id("id-1").build();
        Venda v2 = Venda.builder().id("id-2").build();

        when(service.listar()).thenReturn(List.of(v1, v2));

        mockMvc.perform(get("/v2/test-api/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("id-1"))
                .andExpect(jsonPath("$[1].id").value("id-2"));
    }

    @Test
    @DisplayName("Deveria retornar uma lista vazia")
    void deveriaRetornarUmaListaVazia() throws Exception {
        when(service.listar()).thenReturn(List.of());

        mockMvc.perform(get("/v2/test-api/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Deveria retornar objeto ao buscar por id existente")
    void deveriaRetornarObjetoAoBuscarPorIdExistente() throws Exception {
        Venda venda = Venda.builder().id("id-1").build();

        when(service.buscar("id-1")).thenReturn(Optional.of(venda));

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
    @DisplayName("Deveria retornar 201 ao inserir venda com sucesso")
    void deveriaRetornar201AoInserirVendaComSucesso() throws Exception {
        Venda venda = vendaValida();

        when(service.adicionar(any())).thenReturn(venda);

        mockMvc.perform(post("/v2/test-api/adicionar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venda)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deveria retornar 409 ao inserir venda já registrada")
    void deveriaRetornar409AoInserirVendaJaRegistrada() throws Exception {
        Venda venda = vendaValida();

        when(service.adicionar(any()))
                .thenThrow(new VendaJaExistenteException("Essa venda já foi registrada..."));

        mockMvc.perform(post("/v2/test-api/adicionar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venda)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Essa venda já foi registrada..."));
    }

    @Test
    @DisplayName("Deveria retornar 503 quando serviço de produtos indisponível")
    void deveriaRetornar503QuandoServicoProdutosIndisponivel() throws Exception {
        Venda venda = vendaValida();

        when(service.adicionar(any()))
                .thenThrow(new ErroAoConectarComMsException("Serviço de produtos indisponível"));

        mockMvc.perform(post("/v2/test-api/adicionar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venda)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("Serviço de produtos indisponível"));
    }

    @Test
    @DisplayName("Deveria retornar 422 ao inserir venda com tipo de pagamento inválido")
    void deveriaRetornar422AoInserirVendaComTipoPagamentoInvalido() throws Exception {
        Venda venda = vendaValida();

        when(service.adicionar(any()))
                .thenThrow(new TipoPagamentoInvalidoException("Tipo de pagamento é obrigatório"));

        mockMvc.perform(post("/v2/test-api/adicionar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venda)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("Tipo de pagamento é obrigatório"));
    }

    @Test
    @DisplayName("Deveria retornar 200 ao deletar venda existente")
    void deveriaRetornar200AoDeletarVendaExistente() throws Exception {
        when(service.deletar("id-1")).thenReturn(true);

        mockMvc.perform(delete("/v2/test-api/deletar/id-1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Venda deletada!"));
    }

    @Test
    @DisplayName("Deveria retornar 404 ao tentar deletar venda inexistente")
    void deveriaRetornar404AoDeletarVendaInexistente() throws Exception {
        when(service.deletar("id-99")).thenReturn(false);

        mockMvc.perform(delete("/v2/test-api/deletar/id-99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deveria retornar 200 ao atualizar venda com sucesso")
    void deveriaRetornar200AoAtualizarVendaComSucesso() throws Exception {
        Venda venda = vendaValida();

        when(service.atualizar(any())).thenReturn(venda);

        mockMvc.perform(put("/v2/test-api/atualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venda)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("id-1"));
    }

    @Test
    @DisplayName("Deveria retornar 404 ao tentar atualizar venda inexistente")
    void deveriaRetornar404AoAtualizarVendaInexistente() throws Exception {
        Venda venda = vendaValida();

        when(service.atualizar(any()))
                .thenThrow(new VendaNaoEncontradaException("Venda não encontrada"));

        mockMvc.perform(put("/v2/test-api/atualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venda)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Venda não encontrada"));
    }

    @Test
    @DisplayName("Deveria retornar 422 ao atualizar venda com tipo de pagamento inválido")
    void deveriaRetornar422AoAtualizarVendaComTipoPagamentoInvalido() throws Exception {
        Venda venda = vendaValida();

        when(service.atualizar(any()))
                .thenThrow(new TipoPagamentoInvalidoException("Tipo de pagamento é obrigatório"));

        mockMvc.perform(put("/v2/test-api/atualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venda)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("Tipo de pagamento é obrigatório"));
    }

    @Test
    @DisplayName("Deveria retornar 500 ao ocorrer erro interno inesperado")
    void deveriaRetornar500AoOcorrerErroInternoInesperado() throws Exception {
        Venda venda = vendaValida();

        doThrow(new RuntimeException("erro inesperado")).when(service).atualizar(any());

        mockMvc.perform(put("/v2/test-api/atualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venda)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Deveria retornar 400 com mensagem descritiva ao enviar enum de pagamento inválido no JSON")
    void deveriaRetornar400AoEnviarEnumInvalidoNoJson() throws Exception {
        String jsonComEnumInvalido = """
                {
                    "idProduto": [1],
                    "tipoPagamento": "BOLETO"
                }
                """;

        mockMvc.perform(post("/v2/test-api/adicionar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonComEnumInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Valores aceitos")));
    }

    @Test
    @DisplayName("Deveria retornar 400 com mensagem genérica ao enviar JSON malformado")
    void deveriaRetornar400AoEnviarJsonMalformado() throws Exception {
        String jsonMalformado = "{ isso nao e json valido }";

        mockMvc.perform(post("/v2/test-api/adicionar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMalformado))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Requisição inválida"));
    }
}
