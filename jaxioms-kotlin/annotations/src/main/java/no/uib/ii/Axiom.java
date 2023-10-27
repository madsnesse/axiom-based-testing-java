package no.uib.ii;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Axiom {

    AxiomType type() default AxiomType.REQUIRED;
    int numberOfTestCases() default 100;

}
