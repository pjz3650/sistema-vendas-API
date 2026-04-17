package com.picpay.vendas.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j

public class ConflitoVendaException extends RuntimeException {
    public ConflitoVendaException(String message) {
        super(message);
    }

}
