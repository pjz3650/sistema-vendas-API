package com.picpay.vendas.publisher;

import com.picpay.vendas.model.NotaFiscal;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class NotaFiscalPublisher {

    private final StreamBridge streamBridge;

    public NotaFiscalPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publicar(NotaFiscal notaFiscal) {
        streamBridge.send("notaFiscalGerada-out-0", notaFiscal);
    }
}
