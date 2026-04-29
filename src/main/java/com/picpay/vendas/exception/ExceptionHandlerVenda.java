package com.picpay.vendas.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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





}
