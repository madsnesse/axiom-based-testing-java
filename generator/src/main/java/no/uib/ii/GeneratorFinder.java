package no.uib.ii;


import no.uib.ii.defaultgenerators.*;

import java.util.HashMap;
import java.util.Map;

public class GeneratorFinder {

    public static <T> Map<Class<?>, Class<Generator<T>>> findGenerators() {
        Map<Class<?>, Class<Generator<T>>> generators = new HashMap<>();

        Package[] p = Package.getPackages();
        System.out.println("packages: " + p.length);

        GeneratorFinder.class.getPackage();
        try {
            var c = Class.forName(Generator.class.getName());
            var cs = c.getPermittedSubclasses();
            System.out.println("class: " + c);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        //get file generator_list from classpath

//        try(var file = GeneratorFinder.class.getResourceAsStream("/generator_list")){
//            var content = file.readAllBytes();
//            var lines = new String(content).split("\n");
//            for (var line : lines) {
//                try{
//                    var clazz = (Class<Generator<T>>) Class.forName(line);
//                    var p = (ParameterizedType) clazz.getGenericSuperclass();
//                    var c = p.getActualTypeArguments()[0];
//                    generators.put((Class<?>) c, clazz);
//                } catch (ClassCastException e) {
//                    throw new RuntimeException(e); //TODO handle exception
//                }
//
//
//            }
//            return generators;
//        } catch (ReflectiveOperationException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return generators;


    }

    public static Map<Class<?>, Class<? extends Generator>> defaultGenerators() {
        Map<Class<?>, Class<? extends Generator>> result = new HashMap<>();
        //TODO do this differently

        result.put(String.class, StringGenerator.class);
        result.put(Integer.class, IntegerGenerator.class);
        return result;
    }
}
