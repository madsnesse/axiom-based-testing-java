package org.example;


import no.uib.ii.annotations.AxiomForExistingClass;

import static no.uib.ii.StaticMethods.assertEquals;

public class ObjectAxioms {

    @AxiomForExistingClass(className = "java.lang.Object")
    public static void equalsIsReflexive(Object o) {
        assertEquals(o,o);
    }

    @AxiomForExistingClass(className = "java.lang.Object")
    public static void equalsIsSymmetric(Object x, Object y) {
        assertEquals(x.equals(y), y.equals(x));
    }

    @AxiomForExistingClass(className = "java.lang.Object")
    public static void equalsIsTransitive(Object x, Object y, Object z) {
        assertEquals(x.equals(y) == y.equals(z), x.equals(z));
    }

    @AxiomForExistingClass(className = "java.lang.Object")
    public static void equalsIsConsistent(Object x, Object y) {
        assertEquals(x.equals(y), x.equals(y));
    }

    @AxiomForExistingClass(className = "java.lang.Object")
    public static void equalsNullIsFalse(Object x) {
        assertEquals(x.equals(null), false);
    }

    @AxiomForExistingClass(className = "java.lang.Object")
    public static void equalsCongruence(Object o) {
        var m = o.getClass().getMethods(); //the public methods of a class
        // legg inn metoder som skal sjekkes i axiom prosseseringen
    }

}
