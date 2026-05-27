package com.picpay.vendas.core.publisher;

import com.picpay.vendas.core.model.NotaFiscal;
import org.springframework.cloud.stream.function.StreamBridge;

public class NotaFiscalPublisher {

    private final StreamBridge streamBridge;

    public NotaFiscalPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publicar(NotaFiscal notaFiscal) {
        streamBridge.send("notaFiscalGerada-out-0", notaFiscal);
    }
}
