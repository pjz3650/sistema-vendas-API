package com.picpay.vendas.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.picpay.vendas.model.TipoPagamento;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerVenda {

    @ExceptionHandler(ErroAoConectarComMsException.class)
    ResponseEntity<String> erroAoConectarMicrosservico(ErroAoConectarComMsException e) {
        log.warn("Falha ao conectar com microsserviço: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(e.getMessage());
    }

    @ExceptionHandler(VendaJaExistenteException.class)
    ResponseEntity<String> conflitoAoInserirHandler(VendaJaExistenteException e) {
        log.warn("Falha adicionar venda: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }

    @ExceptionHandler(VendaNaoEncontradaException.class)
    ResponseEntity<String> vendaNaoEncontradaHandler(VendaNaoEncontradaException e) {
        log.warn("Venda não encontrada: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(TipoPagamentoInvalidoException.class)
    ResponseEntity<String> tipoPagamentoInvalidoHandler(TipoPagamentoInvalidoException e) {
        log.warn("Tipo de pagamento inválido: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<String> runtimeExceptionHandler(RuntimeException e) {
        log.error("Erro interno não tratado: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<String> httpMessageNotReadableHandler(HttpMessageNotReadableException e) {
        Throwable cause = e.getCause();
        if (cause instanceof InvalidFormatException ife
                && ife.getTargetType() != null
                && ife.getTargetType().isEnum()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Tipo de pagamento inválido. Valores aceitos: " +
                            Arrays.toString(TipoPagamento.values()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requisição inválida");

    }

}
