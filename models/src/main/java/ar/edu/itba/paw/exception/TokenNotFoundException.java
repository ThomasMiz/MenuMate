package ar.edu.itba.paw.exception;

import ar.edu.itba.paw.exception.base.ResourceNotFoundException;

public class TokenNotFoundException extends ResourceNotFoundException {
    public TokenNotFoundException() {
        super("exception.TokenNotFoundException");
    }
}
