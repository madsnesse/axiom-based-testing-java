package axioms;


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
        if (x.equals(y) && y.equals(z)) {
            assertEquals(x, z);
        }
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
        //TODO implement equalsCongruence
    }

    @AxiomForExistingClass(className = "java.lang.Object")
    public static void equalsAndHashCodePresent(Object o) {
        assertEquals(o.equals(o), o.hashCode() == o.hashCode());
    }

    @AxiomForExistingClass(className = "java.lang.Object")
    public static void equalsHashCodeCongruence(Object a, Object b) {
        if (a.equals(b)) {
            assertEquals(a.hashCode(), b.hashCode());
        }
    }

}
