package uk.gov.justice.maven.rules.service;

import uk.gov.justice.maven.rules.service.Error;

import java.util.List;

public class RuleException extends RuntimeException{
    private List<Error> error;

    public RuleException(String errorMessage, List<Error> error) {
        super(errorMessage);
        this.error = error;
    }

    public List<Error> getError() {
        return error;
    }
}
