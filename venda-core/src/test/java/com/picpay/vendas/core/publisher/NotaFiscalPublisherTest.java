package com.picpay.vendas.core.publisher;

import com.picpay.vendas.core.model.NotaFiscal;
import com.picpay.vendas.core.model.Produto;
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
@DisplayName("NotaFiscalPublisher - Testes Unitários")
class NotaFiscalPublisherTest {

    @Mock
    private StreamBridge streamBridge;

    private NotaFiscalPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new NotaFiscalPublisher(streamBridge);
    }

    @Nested
    @DisplayName("Ao publicar nota fiscal")
    class PublicarNotaFiscal {

        @Test
        @DisplayName("deve publicar nota no binding correto")
        void devePublicarNoBindingCorreto() {
            NotaFiscal nota = umaNotaFiscal();

            publisher.publicar(nota);

            verify(streamBridge).send("notaFiscalGerada-out-0", nota);
        }

        @Test
        @DisplayName("deve chamar streamBridge uma vez")
        void deveChamarStreamBridgeUmaVez() {
            NotaFiscal nota = umaNotaFiscal();

            publisher.publicar(nota);

            verify(streamBridge, times(1)).send(anyString(), any());
        }

        @Test
        @DisplayName("deve publicar qualquer nota fiscal recebida")
        void devePublicarQualquerNota() {
            NotaFiscal nota1 = umaNotaFiscal();
            NotaFiscal nota2 = NotaFiscal.builder()
                    .numero("nota-2")
                    .valor(new BigDecimal("150.00"))
                    .nomeCliente("Maria")
                    .produtos(List.of())
                    .build();

            publisher.publicar(nota1);
            publisher.publicar(nota2);

            verify(streamBridge, times(2)).send(anyString(), any());
        }
    }

    // ========== HELPERS ==========

    private NotaFiscal umaNotaFiscal() {
        Produto produto = new Produto();
        produto.setId("produto-1");
        produto.setNome("Produto Teste");
        produto.setPreco(new BigDecimal("90.00"));

        return NotaFiscal.builder()
                .numero("venda-1")
                .valor(new BigDecimal("90.00"))
                .nomeCliente("João")
                .produtos(List.of(produto))
                .build();
    }
}
