package no.uib.ii.processors;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class TestFileTemplate {

    private Map<String, String> fields;
    public TestFileTemplate(String path) {
        path = System.getProperty("user.dir") + path;
        StringBuilder content = new StringBuilder();
        try {
            FileReader reader = new FileReader(path);
            while (reader.ready()) {
                content.append(reader.read());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(content.toString());
    }
}
