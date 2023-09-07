package com.example.experiments;
import org.junit.jupiter.api.Test;
import static junit.framework.Assert.assertEquals;
import org.example.Position;
import org.example.PositionGenerator;
import static org.example.Position.addIsAssociative;
import no.uib.ii.DataGenerator;


public class PositionTest {

    private static DataGenerator<Position> generator = new PositionGenerator();

    @Test
    public void generate () {
        addIsAssociative(generator.generate(),generator.generate(),generator.generate());
    }
}
