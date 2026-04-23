package com.picpay.vendas.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;

public class ErrorDecoderProduto implements ErrorDecoder {

    private final Logger logger = LoggerFactory.getLogger(ErrorDecoderProduto.class);
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 404 -> {
                logger.error("Produto não encontrado. Method: {}, Status: {}", methodKey, response.status());
                yield new ErroAoConectarComMs("Produto não encontrado");
            }
            case 500 -> {
                logger.error("Erro interno na API externa. Method: {}, Status: {}", methodKey, response.status());
                yield new ErroAoConectarComMs("API indisponível no momento");
            }
            default -> {
                logger.error("Erro inesperado na API externa. Method: {}, Status: {}", methodKey, response.status());
                yield new ErroAoConectarComMs("Erro inesperado na API externa - Status: " + response.status());
            }
        };

    }

}
