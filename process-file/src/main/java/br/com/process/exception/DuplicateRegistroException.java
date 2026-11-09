package br.com.process.exception;

public class DuplicateRegistroException extends RuntimeException {

    public DuplicateRegistroException(String message) {
        super(message);
    }
}
