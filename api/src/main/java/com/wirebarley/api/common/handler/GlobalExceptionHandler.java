package com.wirebarley.api.common.handler;

import com.wirebarley.api.account.domain.exception.InsufficientBalanceException;
import com.wirebarley.api.account.domain.exception.InvalidDepositAmountException;
import com.wirebarley.api.account.domain.exception.InvalidWithdrawAmountException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiErrorResponse> toResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new ApiErrorResponse(message, status.value()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(
                new ApiErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value())
        );
    }

    @ExceptionHandler(InvalidDepositAmountException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidDeposit(InvalidDepositAmountException ex) {
        return toResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidWithdrawAmountException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidWithdraw(InvalidWithdrawAmountException ex) {
        return toResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientBalance(InsufficientBalanceException ex) {
        return toResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex) {
        // TODO slack, email, sms 등으로 알림을 보내는 로직 추가
        return toResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
