package com.picpay.vendas.api.configuration;

import com.picpay.vendas.core.model.Fabrica;
import com.picpay.vendas.core.publisher.VendaPublisher;
import com.picpay.vendas.core.repository.ProdutoClient;
import com.picpay.vendas.core.repository.VendaRepository;
import com.picpay.vendas.core.service.VendaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceConfig (venda-api) - Testes Unitários")
class ServiceConfigTest {

    @Mock
    private StreamBridge streamBridge;

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private ProdutoClient produtoClient;

    @Mock
    private Fabrica fabrica;

    private final ServiceConfig serviceConfig = new ServiceConfig();

    @Nested
    @DisplayName("Ao criar beans")
    class CriarBeans {

        @Test
        @DisplayName("deve criar VendaPublisher com StreamBridge")
        void deveCriarVendaPublisher() {
            VendaPublisher publisher = serviceConfig.vendaPublisher(streamBridge);

            assertThat(publisher).isNotNull();
        }

        @Test
        @DisplayName("deve criar VendaService com dependências")
        void deveCriarVendaService() {
            VendaPublisher publisher = new VendaPublisher(streamBridge);

            VendaService vendaService = serviceConfig.vendaService(
                    vendaRepository, produtoClient, fabrica, publisher
            );

            assertThat(vendaService).isNotNull();
        }
    }
}
