package com.damatapedro.controle_gastos.infrastructure;

import com.damatapedro.controle_gastos.application.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(InvestimentoNotFoundException.class)
    public ResponseEntity<String> handleInvestimentoNotFound(
            InvestimentoNotFoundException exception) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }
    @ExceptionHandler(RegistroNotFoundException.class)
    public ResponseEntity<String> handleRegistroNotFound(
            RegistroNotFoundException exception) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }


    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<String> handleSaldoInsuficiente(
            SaldoInsuficienteException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(ValorResgateInsuficienteException.class)
    public ResponseEntity<String> handleValorResgateInsuficiente(
            ValorResgateInsuficienteException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(ValorInvalidoException.class)
    public ResponseEntity<String> handleValorInvalido(
            ValorInvalidoException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(TipoInvestimentoInvalidoException.class)
    public ResponseEntity<String> handleTipoInvestimentoInvalido(
            TipoInvestimentoInvalidoException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(TipoRegistroInvalidoException.class)
    public ResponseEntity<String> handleTipoRegistroInvalido(
            TipoRegistroInvalidoException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(CategoriaInvestimentoInvalidoException.class)
    public ResponseEntity<String> handleCategoriaInvestimentoInvalido(
            CategoriaInvestimentoInvalidoException exception
    ){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }
    @ExceptionHandler(CategoriaRegistroInvalidoException.class)
    public ResponseEntity<String> handleCategoriaRegistroInvalido(
            CategoriaRegistroInvalidoException exception
    ){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

}