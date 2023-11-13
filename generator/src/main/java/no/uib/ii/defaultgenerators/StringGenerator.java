package no.uib.ii.defaultgenerators;

import no.uib.ii.annotation.DefinedGenerator;

import java.nio.charset.StandardCharsets;
import java.util.Random;

@DefinedGenerator
public class StringGenerator extends Generator<String> {

    public StringGenerator(Random r) {
        super(r);
    }

    @Override
    public String generate(){
        int length = random.nextInt(1, 15);

        byte[] randomBytes = new byte[length];

        random.nextBytes(randomBytes);
        Character c;
        return new String(randomBytes, 0, length, StandardCharsets.UTF_8);
    }

}
