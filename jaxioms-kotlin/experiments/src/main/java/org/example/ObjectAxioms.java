package org.example;


import no.uib.ii.AxiomForExistingClass;

public class ObjectAxioms {

    @AxiomForExistingClass(className = "org.example.Position")
    public static boolean equalsIsReflexive(Object o) {
        return o.equals(o);
    }

}
