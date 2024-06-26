package no.uib.ii.annotations;

/*
 * indicates that the annotated method is an axiom for the class with the given name
 *  */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AxiomForExistingClass {

    String className();

}
