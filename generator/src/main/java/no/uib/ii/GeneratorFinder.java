package no.uib.ii;


import no.uib.ii.defaultgenerators.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

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
        var generators = new no.uib.ii.defaultgenerators.GeneratorFinder().getAllGeneratorsInPackage();

//        var gf = new no.uib.ii.defaultgenerators.GeneratorFinder();
//        var g = GeneratorFinder.class.getClassLoader().getDefinedPackage(gf.getClass().getPackageName());
//        Enumeration<URL> is = null;
//        try {
//            is = GeneratorFinder.class.getClassLoader().getResources(g.getName().replace(".", "/"));
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        List<URL> urls = new ArrayList<>();
//        is.asIterator().forEachRemaining(urls::add);
//        urls.forEach(url -> {
//            var file = new File(url.getFile());
//            var f = file.listFiles();
//            int length = f.length;
//        });
        //var lines = br.lines().collect(Collectors.toList());
        result.put(String.class, StringGenerator.class);
        result.put(Integer.class, IntegerGenerator.class);
        return result;
    }
}
