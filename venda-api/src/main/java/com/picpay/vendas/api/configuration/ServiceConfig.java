package com.picpay.vendas.api.configuration;

import com.picpay.vendas.core.model.Fabrica;
import com.picpay.vendas.core.publisher.VendaPublisher;
import com.picpay.vendas.core.repository.ProdutoClient;
import com.picpay.vendas.core.repository.VendaRepository;
import com.picpay.vendas.core.service.VendaService;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public VendaPublisher vendaPublisher(StreamBridge streamBridge) {
        return new VendaPublisher(streamBridge);
    }

    @Bean
    public VendaService vendaService(VendaRepository vendaRepository,
                                      ProdutoClient produtoClient,
                                      Fabrica fabrica,
                                      VendaPublisher publisher) {
        return new VendaService(vendaRepository, produtoClient, fabrica, publisher);
    }
}
