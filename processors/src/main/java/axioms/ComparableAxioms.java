package axioms;

import no.uib.ii.annotations.AxiomForExistingClass;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * Required axioms for the java interface {@link Comparable} as taken from
 * <a url=
 * "https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/lang/Comparable.html">Java
 * 14 API</a>.
 * <p>
 * The interface imposes a total ordering on the objects of each class that
 * implements it. This ordering is referred to as the class's <em>natural
 * ordering</em>, and the class's {@link Comparable#compareTo(Object)} method is
 * referred to as its <em>natural comparison method</em>.
 * <p>
 * The axioms below capture the requirements on implementations of the
 * {@link Comparable} interface. See related java files for additional
 * recommendations.
 *
 * @see java.lang.Comparable
 * @see axioms.ComparableAxiomsStronglyRecommended
 * Taken from JAxT @see <url>https://www.ii.uib.no/mouldable/testing/jaxt/index.html</url>
 *
 * @author Magne Haveraaen & Karl Trygve Kalleberg, adapted to Jaxioms by Mads Bårvåg Nesse
 * @since 2007-03-26
 * @version 2024-05-30 (adapted to Jaxions and JUnit 5)
 */
public class ComparableAxioms {

    /**
     * compareTo property 1a: duality.
     *
     * <p>
     * The implementor must ensure
     * {@code sgn(x.compareTo(y)) == -sgn(y.compareTo(x))} for all {@code x} and
     * {@code y}. (This implies that {@code x.compareTo(y)} must throw an exception
     * iff {@code y.compareTo(x)} throws an exception.)
     */
    @AxiomForExistingClass(className = "java.lang.Comparable")
    public static <T extends Comparable<T>> void compareToProperty1aDuality(T x, T y) {
        try {
            assertEquals(Integer.signum(x.compareTo(y)), -Integer.signum(y.compareTo(x)));
        } catch (RuntimeException re) {
            assertTrue(true, "compareTo is allowed to throw.");
        }
    }

    /**
     * compareTo property 1b: comparison with null.
     * <p>
     * This implies that {@code x.compareTo(y)} must throw an exception iff
     * {@code y.compareTo(x)} throws an exception.
     * <p>
     * {@code e.compareTo(null)} should throw a {@link NullPointerException}.
     */
    @AxiomForExistingClass(className = "java.lang.Comparable")
    public static <T extends Comparable<T>> void compareToProperty1bNull(T e) {
        try {
            e.compareTo(null);
            fail(e + ".compareTo(null) should throw a NullPointerException");
        } catch (NullPointerException npe) {
            assertTrue(true,"Throws NullPointerException as required");
        }
    }

    /**
     * compareTo property 1c: strong symmetry (both alternatives fail or both
     * succeed).
     * <p>
     * This implies that {@code x.compareTo(y)} must throw an exception iff
     * {@code y.compareTo(x)} throws an exception.
     */
    @AxiomForExistingClass(className = "java.lang.Comparable")
    public static <T extends Comparable<T>> void compareToProperty1cStrongSymmetry(T x, T y) {
        try {
            x.compareTo(y);
            y.compareTo(x);
            assertTrue(true,"neither call fails");
        } catch (RuntimeException e) {
            // at least one of the calls throws an exception
            try {
                x.compareTo(y);
                fail(y + ".compareTo(" + x + ") throws an exception while the converse does not");
            } catch (RuntimeException e1) {
                try {
                    y.compareTo(x);
                    fail(x + ".compareTo(" + y + ") throws an exception while the converse does not");
                } catch (RuntimeException e2) {
                    assertTrue(true, "OK! Both calls fail symmetrically");
                }
            }
        }
    }

    /**
     * compareTo property 2 and 3: transitivity for all comparison operations. This
     * axiom merges the effect of the two axioms required by the documentation.
     *
     * <p>
     * The implementor must also ensure that the relation is transitive:
     * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
     * {@code x.compareTo(z) > 0}.
     *
     * <p>
     * Finally, the implementor must ensure that {@code x.compareTo(y)==0} implies
     * that {@code sgn(x.compareTo(z)) == sgn(y.compareTo(z))}, for all {@code z}.
     *
     */
    @AxiomForExistingClass(className = "java.lang.Comparable")
    public static <T extends Comparable<T>> void compareToProperty2primeTransitive(T x, T y, T z) {
        try {
            if (x.compareTo(y) >= 0 && y.compareTo(z) >= 0)
                assertTrue(x.compareTo(z) >= 0);
        } catch (RuntimeException re) {
            assertTrue(true,"compareTo is allowed to throw.");
        }
    }

}