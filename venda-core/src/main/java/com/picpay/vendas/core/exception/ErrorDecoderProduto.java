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
                log.warn("Produto não encontrado. Method: {}, Status: {}", methodKey, response.status());
                yield new ProdutoNaoEncontradoException("Produto não encontrado");
            }
            case 500, 502, 503, 504 -> {
                log.error("Erro na API externa. Method: {}, Status: {}", methodKey, response.status());
                yield new ErroAoConectarComMsException("API indisponível no momento");
            }
            case 400 -> {
                log.warn("Há dados faltando ou os dados não forma passados corretamente. Method: {}, Status: {}", methodKey, response.status());
                yield new DadosInconsistentesException("Dados inconsistentes");
            }
            case 422 -> {
                log.warn("Alguns dados informados são inválidos. Method: {}, Status: {}", methodKey, response.status());
                yield new ValidacaoFalhouException("Dados inválidos");
            }
            default -> {
                log.error("Erro inesperado na API externa. Method: {}, Status: {}", methodKey, response.status());
                yield new ErroIntegracaoException("Erro inesperado na API externa - Status: " + response.status());
            }
        };
    }
}
