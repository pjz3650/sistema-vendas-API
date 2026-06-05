package com.picpay.vendas.api.controller;

import com.picpay.vendas.core.model.Status;
import com.picpay.vendas.core.model.TipoPagamento;
import com.picpay.vendas.core.model.Venda;
import com.picpay.vendas.core.service.VendaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VendaController - Testes Unitários")
class VendaControllerTest {

    @Mock
    private VendaService service;

    private VendaController controller;

    @BeforeEach
    void setUp() {
        controller = new VendaController(service);
    }

    @Nested
    @DisplayName("Ao listar vendas")
    class ListarVendas {

        @Test
        @DisplayName("deve retornar todas as vendas")
        void deveRetornarTodasVendas() {
            List<Venda> vendas = List.of(umaVenda(), umaVenda());
            when(service.listar()).thenReturn(vendas);

            List<Venda> resultado = controller.listarVendas();

            assertThat(resultado).hasSize(2);
            verify(service).listar();
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não houver vendas")
        void deveRetornarListaVazia() {
            when(service.listar()).thenReturn(List.of());

            List<Venda> resultado = controller.listarVendas();

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("Ao procurar venda por ID")
    class ProcurarVendaPorId {

        @Test
        @DisplayName("deve retornar venda quando existir")
        void deveRetornarVendaQuandoExistir() {
            Venda venda = umaVenda();
            when(service.buscar("venda-1")).thenReturn(Optional.of(venda));

            ResponseEntity<Venda> resultado = controller.procurarVendaPorId("venda-1");

            assertThat(resultado.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(resultado.getBody()).isNotNull();
            assertThat(resultado.getBody().getId()).isEqualTo("venda-1");
        }

        @Test
        @DisplayName("deve retornar 404 quando não existir")
        void deveRetornar404QuandoNaoExistir() {
            when(service.buscar("nao-existe")).thenReturn(Optional.empty());

            ResponseEntity<Venda> resultado = controller.procurarVendaPorId("nao-existe");

            assertThat(resultado.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(resultado.getBody()).isNull();
        }
    }

    @Nested
    @DisplayName("Ao adicionar venda")
    class AdicionarVenda {

        @Test
        @DisplayName("deve criar venda e retornar 201")
        void deveCriarVendaComSucesso() {
            Venda venda = umaVenda();
            when(service.adicionar(any())).thenReturn(venda);

            ResponseEntity<String> resultado = controller.adicionarVenda(venda);

            assertThat(resultado.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(resultado.getBody()).isEqualTo("Venda cadastrada com sucesso!");
            verify(service).adicionar(venda);
        }

        @Test
        @DisplayName("deve chamar service para processar venda")
        void deveChamarServiceParaProcessar() {
            Venda venda = umaVenda();

            controller.adicionarVenda(venda);

            verify(service).adicionar(venda);
        }
    }

    @Nested
    @DisplayName("Ao atualizar venda")
    class AtualizarVenda {

        @Test
        @DisplayName("deve atualizar venda e retornar 200")
        void deveAtualizarVendaComSucesso() {
            Venda venda = umaVenda();
            when(service.atualizar(any())).thenReturn(venda);

            ResponseEntity<Object> resultado = controller.atualizarVenda(venda);

            assertThat(resultado.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(resultado.getBody()).isNotNull();
            verify(service).atualizar(venda);
        }

        @Test
        @DisplayName("deve chamar service para atualizar")
        void deveChamarServiceParaAtualizar() {
            Venda venda = umaVenda();

            controller.atualizarVenda(venda);

            verify(service).atualizar(venda);
        }
    }

    @Nested
    @DisplayName("Ao deletar venda")
    class DeletarVenda {

        @Test
        @DisplayName("deve deletar venda existente e retornar 200")
        void deveDeletarVendaExistente() {
            when(service.deletar("venda-1")).thenReturn(true);

            ResponseEntity<String> resultado = controller.deletarVenda("venda-1");

            assertThat(resultado.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(resultado.getBody()).isEqualTo("Venda deletada!");
            verify(service).deletar("venda-1");
        }

        @Test
        @DisplayName("deve retornar 404 quando venda não existir")
        void deveRetornar404QuandoNaoExistir() {
            when(service.deletar("nao-existe")).thenReturn(false);

            ResponseEntity<String> resultado = controller.deletarVenda("nao-existe");

            assertThat(resultado.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            verify(service).deletar("nao-existe");
        }
    }

    // ========== HELPERS ==========

    private Venda umaVenda() {
        return Venda.builder()
                .id("venda-1")
                .idProduto(List.of("produto-1"))
                .tipoPagamento(TipoPagamento.PIX)
                .valorPago(new BigDecimal("90.00"))
                .valorCompra(new BigDecimal("90.00"))
                .status(Status.APROVADA)
                .cliente(Venda.Cliente.builder()
                        .id("cliente-1")
                        .nome("João")
                        .sobrenome("Silva")
                        .credito(BigDecimal.ZERO)
                        .build())
                .build();
    }
}
