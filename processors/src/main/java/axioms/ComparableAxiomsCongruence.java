package axioms;

import no.uib.ii.annotations.AxiomForExistingClass;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Project specific recommendations as axioms for the java interface
 * java.lang.Comparable related to
 * <a url="http://java.sun.com/j2se/1.5.0/docs/api/">Java 5.0 API</a>
 *
 * @see java.lang.Comparable
 * @see java.lang.Object
 * @author Magne Haveraaen & Karl Trygve Kalleberg, 2007, adapted to Jaxioms by Mads Bårvåg Nesse
 * @version 2024-05-30 (adapted to Jaxions and JUnit 5) *
 */

public class ComparableAxiomsCongruence {
    /**
     * Sees the equals method as a congruence relation (smallest equivalence
     * relation that yields full abstraction). This should generate congruence
     * axioms for every declared interface and class.
     * <p>
     * This axiom is less restrictive than the strong recommendation that the
     * natural ordering is consistent with equals.
     */
    @AxiomForExistingClass(className = "java.lang.Comparable")
    public static <T extends Comparable<T>> void congruenceCompareTo(T a, T b) {
        try {
            if (a.equals(b))
                assertEquals(
                        a.compareTo(b), 0, "Compares (" + a.getClass() + ")" + a.toString() + " (" + b.getClass() + ")" + b.toString());
        } catch (RuntimeException e) {
            assertTrue(true, "some run-time exception occurred");
        }
    }

}