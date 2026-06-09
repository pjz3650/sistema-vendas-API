package com.picpay.vendas.core.publisher;

import com.picpay.vendas.core.model.NotaFiscal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;

@Slf4j
public class NotaFiscalPublisher {

    private final StreamBridge streamBridge;

    public NotaFiscalPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publicar(NotaFiscal notaFiscal) {
        boolean enviado = streamBridge.send("notaFiscalGerada-out-0", notaFiscal);
        if (enviado) {
            log.debug("Evento notaFiscalGerada publicado. Número: {}", notaFiscal.getNumero());
        } else {
            log.error("Falha ao publicar evento notaFiscalGerada. Número: {}", notaFiscal.getNumero());
        }
    }
}
