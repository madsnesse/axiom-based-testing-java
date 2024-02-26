package org.example;

import static no.uib.ii.StaticMethods.assertEquals;

public abstract class AssociativeAddition {

    public abstract <T extends AssociativeAddition> void add(T a);


    public abstract AssociativeAddition deepCopy();

    /**
     * addition is associative
     * @param a
     * @param b
     * @param c
     */
    //@Axiom(type = AxiomType.REQUIRED)
    public static void addIsAssociative(AssociativeAddition a, AssociativeAddition b, AssociativeAddition c) { // TODO fix this
        AssociativeAddition aPlusB = a.deepCopy();
        aPlusB.add(b);
        AssociativeAddition bPlusC = b.deepCopy(); //b + c
        bPlusC.add(c);
        AssociativeAddition aPlusBPlusC = aPlusB;
        aPlusBPlusC.add(c);
        AssociativeAddition bPlusCPlusA = bPlusC;
        bPlusCPlusA.add(a); // a + (b+c)
        assertEquals(aPlusBPlusC, bPlusCPlusA);
    }

}
