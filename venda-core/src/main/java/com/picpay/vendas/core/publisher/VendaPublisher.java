package com.picpay.vendas.core.publisher;

import com.picpay.vendas.core.model.Venda;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;

@Slf4j
public class VendaPublisher {

    private final StreamBridge streamBridge;

    public VendaPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publicar(Venda event) {
        boolean enviado = streamBridge.send("vendaRealizada-out-0", event);
        if (enviado) {
            log.debug("Evento vendaRealizada publicado. ID: {}", event.getId());
        }
        log.error("Falha ao publicar evento vendaRealizada. ID: {}", event.getId());
    }
}
