package com.picpay.vendas.model;

import com.picpay.vendas.exception.TipoPagamentoInvalidoException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Fabrica {

    private final List<CalculoDesconto> descontos;

    public Fabrica(List<CalculoDesconto> descontos) {
        this.descontos = descontos;
    }

    public CalculoDesconto devolverImplementacao(TipoPagamento tipoPagamento) {
        return descontos.stream()
                .filter(desconto -> desconto.getTipoPagamento() == tipoPagamento)
                .findFirst()
                .orElseThrow(() -> new TipoPagamentoInvalidoException("Tipo de pagamento sem implementação: " + tipoPagamento));
    }

}
