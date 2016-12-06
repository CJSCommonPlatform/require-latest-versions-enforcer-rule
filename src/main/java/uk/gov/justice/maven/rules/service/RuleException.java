package uk.gov.justice.maven.rules.service;

import java.util.List;

public class RuleException extends RuntimeException {

    private static final long serialVersionUID = 2458122929157987953L;

    private List<Error> error;

    public RuleException(String errorMessage, List<Error> error) {
        super(errorMessage);
        this.error = error;
    }

    public List<Error> getError() {
        return error;
    }
}
