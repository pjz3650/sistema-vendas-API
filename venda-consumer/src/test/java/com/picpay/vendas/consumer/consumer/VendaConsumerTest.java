package com.picpay.vendas.consumer.consumer;

import com.picpay.vendas.core.model.NotaFiscal;
import com.picpay.vendas.core.model.VendaEvent;
import com.picpay.vendas.core.publisher.NotaFiscalPublisher;
import com.picpay.vendas.core.service.NotaFiscalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VendaConsumer - Testes Unitários")
class VendaConsumerTest {

    @Mock
    private NotaFiscalService notaFiscalService;

    @Mock
    private NotaFiscalPublisher notaFiscalPublisher;

    private VendaConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new VendaConsumer(notaFiscalService, notaFiscalPublisher);
    }

    @Nested
    @DisplayName("Ao processar evento de venda")
    class ProcessarEventoVenda {

        @Test
        @DisplayName("deve gerar nota fiscal e publicar")
        void deveGerarNotaFiscalEPublicar() {
            VendaEvent event = umVendaEvent();
            Message<VendaEvent> message = MessageBuilder.withPayload(event).build();
            NotaFiscal notaFiscal = umaNotaFiscal();

            when(notaFiscalService.gerarNota(event)).thenReturn(notaFiscal);

            consumer.processar(message);

            verify(notaFiscalService).gerarNota(event);
            verify(notaFiscalPublisher).publicar(notaFiscal);
        }

        @Test
        @DisplayName("deve processar evento corretamente")
        void deveProcessarEventoCorretamente() {
            VendaEvent event = umVendaEvent();
            Message<VendaEvent> message = MessageBuilder.withPayload(event).build();
            NotaFiscal notaFiscal = umaNotaFiscal();

            when(notaFiscalService.gerarNota(any())).thenReturn(notaFiscal);

            consumer.processar(message);

            verify(notaFiscalService).gerarNota(event);
            verify(notaFiscalPublisher).publicar(notaFiscal);
        }

        @Test
        @DisplayName("deve criar consumer bean corretamente")
        void deveCriarConsumerBeanCorretamente() {
            var consumerFn = consumer.vendaCadastradaEventConsumer();

            assertThat(consumerFn).isNotNull();
        }

        @Test
        @DisplayName("não deve publicar nota quando serviço lançar exceção")
        void naoDevePublicarQuandoServicoLancarExcecao() {
            VendaEvent event = umVendaEvent();
            Message<VendaEvent> message = MessageBuilder.withPayload(event).build();

            when(notaFiscalService.gerarNota(any())).thenThrow(new RuntimeException("Erro"));

            try {
                consumer.processar(message);
            } catch (RuntimeException e) {
                // esperado
            }

            verify(notaFiscalPublisher, never()).publicar(any());
        }
    }

    // ========== HELPERS ==========

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

    private NotaFiscal umaNotaFiscal() {
        return NotaFiscal.builder()
                .numero("venda-1")
                .valor(new BigDecimal("90.00"))
                .nomeCliente("João")
                .build();
    }
}
