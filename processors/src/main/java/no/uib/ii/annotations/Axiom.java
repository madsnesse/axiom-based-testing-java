package no.uib.ii.annotations;

import java.lang.annotation.*;


@Inherited //TODO skriv om denne
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Axiom {

    AxiomType type() default AxiomType.REQUIRED;
    int numberOfTestCases() default 100;

}
