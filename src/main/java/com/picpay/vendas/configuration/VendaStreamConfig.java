package com.picpay.vendas.configuration;

import com.picpay.vendas.model.NotaFiscal;
import com.picpay.vendas.model.Venda;
import com.picpay.vendas.model.VendaEvent;
import com.picpay.vendas.service.NotaFiscalPublisher;
import com.picpay.vendas.service.NotaFiscalService;
import com.picpay.vendas.service.VendaPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
public class VendaStreamConfig {

    private final NotaFiscalService notaFiscalService;
    private final NotaFiscalPublisher notaFiscalPublisher;

    public VendaStreamConfig(NotaFiscalService notaFiscalService, NotaFiscalPublisher notaFiscalPublisher) {
        this.notaFiscalService = notaFiscalService;
        this.notaFiscalPublisher = notaFiscalPublisher;
    }

    @Bean
    public Consumer<Message<VendaEvent>> vendaCadastradaEventConsumer() {
        return event -> {
            NotaFiscal notaFiscal = notaFiscalService.gerarNota(event.getPayload());
            notaFiscalPublisher.publicar(notaFiscal);
        };
    }



}
