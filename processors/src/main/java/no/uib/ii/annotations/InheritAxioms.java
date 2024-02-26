package no.uib.ii.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * indicate that the class should inherit axioms from its parents
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface InheritAxioms {
}
