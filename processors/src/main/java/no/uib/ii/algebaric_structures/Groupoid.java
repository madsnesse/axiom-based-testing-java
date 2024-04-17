package no.uib.ii.algebaric_structures;

import static no.uib.ii.StaticMethods.assertEquals;

public interface Groupoid<T> extends Monoid<T> {

    T inverse(T a);

    //@Axiom
    default void inverseAxiom(T a) {
        assertEquals(identity(), binaryOperation(inverse(a), a));
        assertEquals(identity(), binaryOperation(a, inverse(a)));
    }


}
