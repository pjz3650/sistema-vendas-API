package com.picpay.vendas.consumer.consumer;

import com.picpay.vendas.core.model.NotaFiscal;
import com.picpay.vendas.core.model.VendaEvent;
import com.picpay.vendas.core.publisher.NotaFiscalPublisher;
import com.picpay.vendas.core.service.NotaFiscalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
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
        log.info("Recebendo evento de venda: {}", event.getPayload().getId());
        try {
        NotaFiscal notaFiscal = notaFiscalService.gerarNota(event.getPayload());
        notaFiscalPublisher.publicar(notaFiscal);
        
        log.info("Nota fiscal gerada: {}", notaFiscal.getNumero());
    } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
