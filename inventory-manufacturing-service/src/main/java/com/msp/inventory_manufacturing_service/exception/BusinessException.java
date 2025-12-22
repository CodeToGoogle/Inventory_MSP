package com.msp.inventory_manufacturing_service.exception;



import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    private final String code;
    private final HttpStatus status;
    public BusinessException(String code, String msg, HttpStatus status) { super(msg); this.code=code; this.status=status; }
    public String getCode(){return code;}
    public HttpStatus getStatus(){ return status;}
}

