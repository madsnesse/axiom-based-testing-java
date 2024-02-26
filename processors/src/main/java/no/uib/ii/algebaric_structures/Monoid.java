package no.uib.ii.algebaric_structures;

import no.uib.ii.annotations.Axiom;

import static no.uib.ii.StaticMethods.assertEquals;

public interface Monoid <T> {

    T binaryOperation(T a, T b);

    T identity();

    @Axiom
    default void associativeBinaryAxiom(T a, T b, T c) {
        //check if associative
        var left = binaryOperation(a,binaryOperation(b,c));
        var right = binaryOperation(binaryOperation(a,b), c);
        assertEquals(left, right);
    }

    @Axiom
    default void neutralAxiom(T a) {
        assertEquals(a, binaryOperation(identity(), a));
        assertEquals(a, binaryOperation(a, identity()));
    }
}
