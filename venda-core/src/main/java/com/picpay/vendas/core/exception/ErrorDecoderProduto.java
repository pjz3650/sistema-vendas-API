package com.picpay.vendas.core.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorDecoderProduto implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 404 -> {
                log.error("Produto não encontrado. Method: {}, Status: {}", methodKey, response.status());
                yield new ErroAoConectarComMsException("Produto não encontrado");
            }
            case 500 -> {
                log.error("Erro interno na API externa. Method: {}, Status: {}", methodKey, response.status());
                yield new ErroAoConectarComMsException("API indisponível no momento");
            }
            default -> {
                log.error("Erro inesperado na API externa. Method: {}, Status: {}", methodKey, response.status());
                yield new ErroAoConectarComMsException("Erro inesperado na API externa - Status: " + response.status());
            }
        };
    }
}
