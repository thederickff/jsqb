package io.github.str4ng3r.exceptions;

public class InvalidDDLGenerationException extends Exception {
    InvalidDDLGenerationException(String error) {
        super(error);
    }
}
