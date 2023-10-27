package org.example;


import no.uib.ii.AxiomForExistingClass;

public class ObjectAxioms {

    @AxiomForExistingClass(className = "java.lang.Object")
    public static boolean equalsIsReflexive(Object o) {
        return o.equals(o);
    }

}
