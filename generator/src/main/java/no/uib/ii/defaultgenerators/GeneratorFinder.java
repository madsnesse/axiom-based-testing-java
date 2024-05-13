package no.uib.ii.defaultgenerators;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GeneratorFinder {

    public List<Generator<?>> getAllGeneratorsInPackage() {
        var result = new ArrayList<Generator<?>>();
        var p = GeneratorFinder.class.getPackage();
        var classes = Generator.class.getClasses();
        var cs = findAllClassessInPackage(p.getName());
        for (var c : cs) {
            if (Generator.class.isAssignableFrom(c)) {
                try {
                    result.add((Generator<?>) c.getConstructor().newInstance());
                } catch (ReflectiveOperationException e) {
                    System.out.println("could not instantiate generator " + c);
                }
            }
        }
        return result;
    }

    private List<Class<?>> findAllClassessInPackage(String p) {
        var result = new ArrayList<Class<?>>();
        var is = this.getClass().getClassLoader().getDefinedPackages();
//        var br = new BufferedReader(new InputStreamReader(is));
//        br.lines().forEach(line -> {
//            var c = getClass(p, line);
//            if (c != null) {
//                result.add(c);
//            }
//        });
        return result;

    }

    private Class<?> getClass(String packageName, String className) {
        try {
            return Class.forName(packageName + "." + className);
        } catch (ClassNotFoundException e) {
            System.err.println("could not find class " + packageName  + "." + className);
            return null;
        }
    }
}
