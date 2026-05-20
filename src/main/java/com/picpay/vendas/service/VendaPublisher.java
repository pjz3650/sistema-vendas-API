package com.picpay.vendas.service;

import com.picpay.vendas.model.Venda;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class VendaPublisher {

    private final StreamBridge streamBridge;

    public VendaPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }


    public void publicar(Venda event) {
        streamBridge.send("vendaRealizada-out-0", event);
    }

}