package no.uib.ii.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Group {

    /**
     * @return the name of the identity function of the type
     */
    String identity();

    /**
     * @return the name of the binary operation of the type
     */
    String binaryOperation();

    /**
     * @return the name of the inverse function
     */
    String inverse();


}
