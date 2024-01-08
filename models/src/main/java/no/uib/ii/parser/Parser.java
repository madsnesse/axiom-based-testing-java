package no.uib.ii.parser;

import com.github.javaparser.ParseResult;
import no.uib.ii.UnexpectedParseException;

public class Parser {
    public static <T> T parseOrException(ParseResult<T> result, String exceptionMessage) {
        if (result.isSuccessful()) {
            return result.getResult().orElseThrow(() ->
                    new UnexpectedParseException("returned a result, but optional was empty"));
        }
        else {
            String errorMessageFromParser = "";
            for (var v : result.getProblems()) {
                errorMessageFromParser+= v.getVerboseMessage();
            }
            throw new UnexpectedParseException(exceptionMessage + errorMessageFromParser);
        }
    }
}
