package no.uib.ii;

import java.util.function.Supplier;

public class UnexpectedParseException extends RuntimeException {
    public UnexpectedParseException(String reason) {
        super(reason);
    }
}
