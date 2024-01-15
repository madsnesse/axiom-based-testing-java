package no.uib.ii.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import no.uib.ii.UnexpectedParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class CommonParserMethods {

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

    /*
    * load the content of a file as a compilationunit, the file should be a java file containing at least one class
    * */
    public static CompilationUnit parseFromTemplate(Reader reader) {
        return null;
    }

}
