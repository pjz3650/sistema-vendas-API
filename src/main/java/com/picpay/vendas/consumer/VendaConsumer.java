package com.picpay.vendas.consumer;

import com.picpay.vendas.model.NotaFiscal;
import com.picpay.vendas.model.VendaEvent;
import com.picpay.vendas.publisher.NotaFiscalPublisher;
import com.picpay.vendas.service.NotaFiscalService;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class VendaConsumer {

    private final NotaFiscalService notaFiscalService;
    private final NotaFiscalPublisher notaFiscalPublisher;

    public VendaConsumer(NotaFiscalService notaFiscalService, NotaFiscalPublisher notaFiscalPublisher) {
        this.notaFiscalService = notaFiscalService;
        this.notaFiscalPublisher = notaFiscalPublisher;
    }

    @Bean
    public Consumer<Message<VendaEvent>> vendaCadastradaEventConsumer() {
        return this::processar;
    }

    public void processar(Message<VendaEvent> event) {
        NotaFiscal notaFiscal = notaFiscalService.gerarNota(event.getPayload());
        notaFiscalPublisher.publicar(notaFiscal);
    }



}
