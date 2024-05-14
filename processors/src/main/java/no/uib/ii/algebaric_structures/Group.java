package no.uib.ii.algebaric_structures;

import no.uib.ii.annotations.Axiom;
import no.uib.ii.annotations.InheritAxioms;

import static no.uib.ii.StaticMethods.assertEquals;

public interface Group<T> {


    T binaryOperation(T a);

    T inverse();

    T identity();

    @Axiom
    static <T extends Group<T>> void associativeBinaryOperation(T a, T b, T c) {
        T ab = a.binaryOperation(b);
        T bc = b.binaryOperation(c);
        assertEquals(ab.binaryOperation(c), bc.binaryOperation(a));
    }
}