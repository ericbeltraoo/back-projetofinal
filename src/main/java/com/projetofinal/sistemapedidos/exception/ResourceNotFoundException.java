// ========== ResourceNotFoundException.java ==========
package com.projetofinal.sistemapedidos.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}