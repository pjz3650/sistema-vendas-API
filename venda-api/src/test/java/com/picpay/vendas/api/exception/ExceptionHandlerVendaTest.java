package com.picpay.vendas.api.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.picpay.vendas.core.exception.*;
import com.picpay.vendas.core.model.TipoPagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExceptionHandlerVenda - Testes Unitários")
class ExceptionHandlerVendaTest {

    private ExceptionHandlerVenda exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new ExceptionHandlerVenda();
    }

    @Nested
    @DisplayName("Ao tratar ErroAoConectarComMsException")
    class ErroAoConectarComMs {

        @Test
        @DisplayName("deve retornar status 503 SERVICE_UNAVAILABLE")
        void deveRetornar503() {
            ErroAoConectarComMsException exception = new ErroAoConectarComMsException("Serviço indisponível");

            ResponseEntity<String> resposta = exceptionHandler.erroAoConectarMicrosservico(exception);

            assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
            assertThat(resposta.getBody()).isEqualTo("Serviço indisponível");
        }
    }

    @Nested
    @DisplayName("Ao tratar VendaJaExistenteException")
    class VendaJaExistente {

        @Test
        @DisplayName("deve retornar status 409 CONFLICT")
        void deveRetornar409() {
            VendaJaExistenteException exception = new VendaJaExistenteException("Venda já existe");

            ResponseEntity<String> resposta = exceptionHandler.conflitoAoInserirHandler(exception);

            assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(resposta.getBody()).isEqualTo("Venda já existe");
        }
    }

    @Nested
    @DisplayName("Ao tratar VendaNaoEncontradaException")
    class VendaNaoEncontrada {

        @Test
        @DisplayName("deve retornar status 404 NOT_FOUND")
        void deveRetornar404() {
            VendaNaoEncontradaException exception = new VendaNaoEncontradaException("Venda não encontrada");

            ResponseEntity<String> resposta = exceptionHandler.vendaNaoEncontradaHandler(exception);

            assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(resposta.getBody()).isEqualTo("Venda não encontrada");
        }
    }

    @Nested
    @DisplayName("Ao tratar TipoPagamentoInvalidoException")
    class TipoPagamentoInvalido {

        @Test
        @DisplayName("deve retornar status 422 UNPROCESSABLE_ENTITY")
        void deveRetornar422() {
            TipoPagamentoInvalidoException exception = new TipoPagamentoInvalidoException("Tipo inválido");

            ResponseEntity<String> resposta = exceptionHandler.tipoPagamentoInvalidoHandler(exception);

            assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            assertThat(resposta.getBody()).isEqualTo("Tipo inválido");
        }
    }

    @Nested
    @DisplayName("Ao tratar RuntimeException")
    class RuntimeExceptionHandler {

        @Test
        @DisplayName("deve retornar status 500 INTERNAL_SERVER_ERROR")
        void deveRetornar500() {
            RuntimeException exception = new RuntimeException("Erro inesperado");

            ResponseEntity<String> resposta = exceptionHandler.runtimeExceptionHandler(exception);

            assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(resposta.getBody()).isEqualTo("Erro inesperado");
        }
    }

    @Nested
    @DisplayName("Ao tratar HttpMessageNotReadableException")
    class HttpMessageNotReadable {

        @Test
        @DisplayName("deve retornar 400 com mensagem de enum inválido quando for InvalidFormatException")
        void deveRetornar400ParaEnumInvalido() {
            InvalidFormatException invalidFormatException = mock(InvalidFormatException.class);
            when(invalidFormatException.getTargetType()).thenReturn((Class) TipoPagamento.class);

            HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
            when(exception.getCause()).thenReturn(invalidFormatException);

            ResponseEntity<String> resposta = exceptionHandler.httpMessageNotReadableHandler(exception);

            assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(resposta.getBody()).contains("Tipo de pagamento inválido");
            assertThat(resposta.getBody()).contains(TipoPagamento.values()[0].name());
        }

        @Test
        @DisplayName("deve retornar 400 com mensagem genérica para outros casos")
        void deveRetornar400MensagemGenerica() {
            HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
            when(exception.getCause()).thenReturn(new RuntimeException("outro erro"));

            ResponseEntity<String> resposta = exceptionHandler.httpMessageNotReadableHandler(exception);

            assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(resposta.getBody()).isEqualTo("Requisição inválida");
        }
    }

    @Nested
    @DisplayName("Ao tratar MethodArgumentNotValidException")
    class MethodArgumentNotValidHandler {

        @Test
        @DisplayName("deve retornar 400 com lista de erros")
        void deveRetornar400ComListaErros() throws Exception {
            BindingResult bindingResult = mock(BindingResult.class);
            org.springframework.validation.FieldError fieldError1 = 
                new org.springframework.validation.FieldError("venda", "idProduto", "não deve estar em branco");
            org.springframework.validation.FieldError fieldError2 = 
                new org.springframework.validation.FieldError("venda", "valorPago", "não deve ser nulo");
            
            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            when(exception.getBindingResult()).thenReturn(bindingResult);

            ResponseEntity<Map<String, Object>> resposta = exceptionHandler.methodArgumentNotValidHandler(exception);

            assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(resposta.getBody()).isNotNull();
            assertThat(resposta.getBody().get("status")).isEqualTo(400);
            
            @SuppressWarnings("unchecked")
            List<String> erros = (List<String>) resposta.getBody().get("erros");
            assertThat(erros).hasSize(2);
        }

        @Test
        @DisplayName("deve retornar erros formatados corretamente")
        void deveRetornarErrosFormatados() throws Exception {
            BindingResult bindingResult = mock(BindingResult.class);
            org.springframework.validation.FieldError fieldError = 
                new org.springframework.validation.FieldError("venda", "tipoPagamento", "Informe o tipo de pagamento");
            
            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            when(exception.getBindingResult()).thenReturn(bindingResult);

            ResponseEntity<Map<String, Object>> resposta = exceptionHandler.methodArgumentNotValidHandler(exception);

            assertThat(resposta.getBody()).isNotNull();
            
            @SuppressWarnings("unchecked")
            List<String> erros = (List<String>) resposta.getBody().get("erros");
            assertThat(erros.get(0)).contains("tipoPagamento");
        }
    }

    // DTO auxiliar para testes de validação
    private static class VendaTestDto {
        private List<String> idProduto;
        private java.math.BigDecimal valorPago;
        private String tipoPagamento;

        public List<String> getIdProduto() { return idProduto; }
        public void setIdProduto(List<String> idProduto) { this.idProduto = idProduto; }
        public java.math.BigDecimal getValorPago() { return valorPago; }
        public void setValorPago(java.math.BigDecimal valorPago) { this.valorPago = valorPago; }
        public String getTipoPagamento() { return tipoPagamento; }
        public void setTipoPagamento(String tipoPagamento) { this.tipoPagamento = tipoPagamento; }
    }
}
