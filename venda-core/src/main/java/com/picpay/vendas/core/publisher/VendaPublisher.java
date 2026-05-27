package com.picpay.vendas.core.publisher;

import com.picpay.vendas.core.model.Venda;
import org.springframework.cloud.stream.function.StreamBridge;

public class VendaPublisher {

    private final StreamBridge streamBridge;

    public VendaPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publicar(Venda event) {
        streamBridge.send("vendaRealizada-out-0", event);
    }
}
