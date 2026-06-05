package com.picpay.vendas.consumer.configuration;

import com.picpay.vendas.core.model.Fabrica;
import com.picpay.vendas.core.publisher.NotaFiscalPublisher;
import com.picpay.vendas.core.publisher.VendaPublisher;
import com.picpay.vendas.core.repository.ProdutoClient;
import com.picpay.vendas.core.repository.VendaRepository;
import com.picpay.vendas.core.service.NotaFiscalService;
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
    public NotaFiscalPublisher notaFiscalPublisher(StreamBridge streamBridge) {
        return new NotaFiscalPublisher(streamBridge);
    }

    @Bean
    public VendaService vendaService(VendaRepository vendaRepository,
                                      ProdutoClient produtoClient,
                                      Fabrica fabrica,
                                      VendaPublisher publisher) {
        return new VendaService(vendaRepository, produtoClient, fabrica, publisher);
    }

    @Bean
    public NotaFiscalService notaFiscalService(VendaService vendaService, ProdutoClient produtoClient) {
        return new NotaFiscalService(vendaService, produtoClient);
    }
}
