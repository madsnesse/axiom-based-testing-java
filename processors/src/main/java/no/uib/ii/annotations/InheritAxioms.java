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
    /**
     * The class that should inherit axioms, used if you want to specify that a class that is not your own should inherit axioms
     * @return
     */
    String forClass() default ""; //TODO: implement this, kanskje ha det som en property for AxiomForClass, eller en helt ny annotation
    // må kanskje være for en klasse, ikke for en source file
    // Kanskje vi ikke trenger en egen, men kan heller inherite alle som har axiomer som skal gjelde for barn
}
