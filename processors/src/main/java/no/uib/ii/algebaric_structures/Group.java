package no.uib.ii.algebaric_structures;

import no.uib.ii.annotations.Axiom;

import static no.uib.ii.StaticMethods.assertEquals;

public interface Group<T> {


    T binaryOperation(T a);

    T inverse();

    T identity();

    //@Axiom
    static <T extends Group<T>> void associativeBinaryOperation(T a, T b, T c) {
        //TODO fix with deepcopy
        T aCp = a;
        assertEquals(a.binaryOperation(b).binaryOperation(c), a.binaryOperation(b.binaryOperation(c)));
    }
}