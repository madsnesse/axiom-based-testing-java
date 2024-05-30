package axioms;


import no.uib.ii.annotations.AxiomForExistingClass;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Project specific recommendations as axioms for the java interface
 * java.lang.Object: toString is congruent to equals.
 *
 * The {@link #toString()} method provides a string representation of the
 * object. The axioms here states that equal objects should have the same string
 * representation, and that objects with equal string representation should be
 * equal. Together they state that the string representation of an object is
 * isomorphic to the object.
 *
 * This is often too strong: - For debugging it is often useful to see the raw
 * data representation of an object in string form (violating the {link
 * {@link #congruenceToString(Object, Object)} axiom). - Often the
 * {@link #toString()} method will not print the full details of the object
 * (violating the {@link #congruenceToString(Object, Object)} axiom).
 *
 * @see java.lang.Object
 * Taken from JAxT @see <url>https://www.ii.uib.no/mouldable/testing/jaxt/index.html</url>
 * @author Magne Haveraaen & Karl Trygve Kalleberg, 2007, adapted to Jaxioms by Mads Bårvåg Nesse
 * @since 2007-03-10
 * @version 2024-05-30 (adapted to Jaxions and JUnit 5)
 *
 */
public class ObjectAxiomsCongruenceToString {

    /**
     * Sees the {@link #equals(Object)} method as a congruence relation (smallest
     * equivalence relation that yields full abstraction) for the method
     * {@link #toString()}.
     *
     * This axiom is similar in spirit to the
     * {@link axioms.ObjectAxioms#hashCodeProperty2congruenceEquals(java.lang.Object, java.lang.Object)}
     * axiom which is required by the Java standard library.
     *
     */
    @AxiomForExistingClass(className = "java.lang.Object")
    public static void congruenceToString(Object a, Object b) {
        try {
            if (a.equals(b))
                assertEquals(a.toString(), b.toString(), "congruenceToString (" + a.getClass() + ")" + a.toString() + ", (" + b.getClass() + ")"
                        + b.toString());
        } catch (RuntimeException e) {
            assertTrue(true, "some run-time exception occurred");
        }
    }

    /**
     * Sees the {@link #toString()} method as defining the equivalence relation
     * {@link #equals(Object)}: When two objects have the same {@code String}
     * representation they have to be equal objects.
     *
     * For more details on the {@link #equals(Object)} method see the axioms in
     * {@link axioms.ObjectAxioms}.
     *
     */
    @AxiomForExistingClass(className = "java.lang.Object")
    public static void congruenceFromString(Object a, Object b) {
        try {
            if (a.toString().equals(b.toString()))
                assertEquals(a, b, "congruenceFromString (" + a.getClass() + ")" + a.toString() + ", (" + b.getClass() + ")"
                        + b.toString());
        } catch (RuntimeException e) {
            assertTrue(true, "some run-time exception occurred");
        }
    }

}