package com.picpay.vendas.core.publisher;

import com.picpay.vendas.core.model.Status;
import com.picpay.vendas.core.model.TipoPagamento;
import com.picpay.vendas.core.model.Venda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VendaPublisher - Testes Unitários")
class VendaPublisherTest {

    @Mock
    private StreamBridge streamBridge;

    private VendaPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new VendaPublisher(streamBridge);
    }

    @Nested
    @DisplayName("Ao publicar venda")
    class PublicarVenda {

        @Test
        @DisplayName("deve publicar evento no binding correto")
        void devePublicarEventoNoBindingCorreto() {
            Venda venda = umaVenda();

            publisher.publicar(venda);

            verify(streamBridge).send("vendaRealizada-out-0", venda);
        }

        @Test
        @DisplayName("deve chamar streamBridge uma vez")
        void deveChamarStreamBridgeUmaVez() {
            Venda venda = umaVenda();

            publisher.publicar(venda);

            verify(streamBridge, times(1)).send(anyString(), any());
        }

        @Test
        @DisplayName("deve publicar qualquer venda recebida")
        void devePublicarQualquerVenda() {
            Venda venda1 = umaVenda();
            Venda venda2 = Venda.builder()
                    .id("venda-2")
                    .idProduto(List.of("produto-2"))
                    .tipoPagamento(TipoPagamento.CARTAO_CREDITO)
                    .valorPago(new BigDecimal("200.00"))
                    .status(Status.APROVADA)
                    .cliente(Venda.Cliente.builder()
                            .id("cliente-2")
                            .nome("Maria")
                            .sobrenome("Santos")
                            .credito(BigDecimal.ZERO)
                            .build())
                    .build();

            publisher.publicar(venda1);
            publisher.publicar(venda2);

            verify(streamBridge, times(2)).send(anyString(), any());
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
