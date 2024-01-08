package no.uib.ii;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;
import no.uib.ii.annotation.DefinedGenerator;
import no.uib.ii.defaultgenerators.Generator;
import no.uib.ii.parser.Parser;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DataGenerator {

    private static Random r = new Random();
    private static Map<Class<?>, Class<? extends Generator>> availableGenerators = GeneratorFinder.defaultGenerators();

    public static <T> T generateForClass(Class<T> clazz) {

        for (Constructor<?> constructor: clazz.getConstructors()) {
            Class<?>[] parameters = constructor.getParameterTypes();
            Object[] args = new Object[parameters.length];


            for (int i = 0; i < parameters.length; i++) {
                var p = parameters[i];
                switch (p.getName()) {
                    case "java.lang.String":

                        //TODO hvordan finne rett generator? kanskje analyse av classpath?
                        //StringGenerator generator = new StringGenerator(r);
                        //args[i] = generator.generate();
                        break;
                    case "int":
                        args[i] = r.nextInt();
                        break;

                    }
                }
                try {
                    Object t = constructor.newInstance(args);
                    return clazz.cast(t);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        return null;
    }
    @SuppressWarnings("unchecked")
//    public static <T> Generator<T> getGeneratorForClass(Class<T> clazz) {
//        if (availableGenerators.containsKey(clazz)) {
//            var constructor = availableGenerators.get(clazz).getConstructors()[0];
//            try {
//                var n = constructor.newInstance(r); //TODO require that generator has a constructor with a random as argument
//                if (n instanceof Generator) {
//                    //check if generic part is of type T
//                    var p = ((ParameterizedType) n.getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName();
//                    if (p.equals(clazz.getName())) {
//                        return (Generator<T>) n;
//                    }
//                }
//            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        //TODO handle primitives, swap for Integer, Long etc.
//        else {
//            for (Constructor<?> constructor: clazz.getConstructors()) {
//                Class<?>[] parameters = constructor.getParameterTypes();
//                Object[] args = new Object[parameters.length];
//
//                Generator[] generators = new Generator[parameters.length];
//                for (int i = 0; i < parameters.length; i++) {
//                    var p = parameters[i];
//                    //TODO handle not going too deep
//                    var generator = getGeneratorForClass(p);
//                    if (generator == null) {
//                        throw new RuntimeException("No generator found for " + p.getName());
//                    }
//                    generators[i] = generator;
//                }
//                JavaParser parser = new JavaParser();
//                CompilationUnit cu = new CompilationUnit();
//                ClassOrInterfaceDeclaration generatorClass = new ClassOrInterfaceDeclaration();
//                generatorClass.addExtendedType(Generator.class);
//                generatorClass.setName(clazz.getSimpleName() + "Generator");
//                generatorClass.addAnnotation(DefinedGenerator.class);
//                ConstructorDeclaration constructorDeclaration = generatorClass.addConstructor(Modifier.Keyword.PUBLIC);
//
//                constructorDeclaration.setName(generatorClass.getName());
//
//                String blockStatement = "return new " + clazz.getName() + "(";
//
//                for (Generator g : generators) {
//                    var varName = g.getClass().getSimpleName().toLowerCase();
//                    constructorDeclaration.addParameter(g.getClass(), varName);
//                    blockStatement += varName+".generate(),";
//                }
//
//
//                cu.addType(generatorClass);
//
//                System.out.println(cu.toString());
//                if (!new File("target/generated-sources/").exists()) {
//                    new File("target/generated-sources/").mkdirs();
//                }
//                try (final FileOutputStream fos = new FileOutputStream("target/generated-sources/" + generatorClass.getName() + ".java");){
//
//                    fos.write(cu.toString().getBytes());
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//                //TODO create a generator file instead
//                return new Generator<T>(r) {
//                    @Override
//                    public T generate() {
//                        return null;
//                    }
//                };
//        }}
//
//        return null;
//    }

    public static String generateGeneratorForClass(Class<?> clazz) {
        for (Constructor<?> constructor: clazz.getConstructors()) {
            Class<?>[] parameters = constructor.getParameterTypes();
            Object[] args = new Object[parameters.length];
            List<Generator> generators = new ArrayList<>();
            for (Class<?> p : parameters) {
                Generator<?> generator = null;
                try {
                    generator = getGeneratorForClass(p);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if (generator == null) {
                    throw new RuntimeException("No generator found for " + p.getName());
                }
                generators.add(generator);
            }
            JavaParser parser = new JavaParser();
            CompilationUnit cu = new CompilationUnit();
            ClassOrInterfaceDeclaration generatorClass = new ClassOrInterfaceDeclaration();
            generatorClass.addExtendedType(Generator.class);
            generatorClass.setName(clazz.getName() + "Generator");
            generatorClass.addAnnotation(DefinedGenerator.class);
            ConstructorDeclaration constructorDeclaration = generatorClass.addConstructor(Modifier.Keyword.PUBLIC);

            constructorDeclaration.setName(generatorClass.getName());

            String blockStatement = String.format("return new %s(%s);", clazz.getName(), String.join(",", generators.stream().map(g -> g.getClass().getSimpleName().toLowerCase() + ".generate()").toArray(String[]::new)));






            cu.addType(generatorClass);

            return cu.toString();
        }
        return null;
    }

    public static String generateGeneratorForClass(ClassOrInterfaceDeclaration clazz) {

        for (ConstructorDeclaration constructor: clazz.getConstructors()) {
            Iterator<Parameter> parameters = constructor.getParameters().iterator();

            List<Generator> generators = new ArrayList<>();
            while (parameters.hasNext()) {
                var p = parameters.next();
                var generator = getGeneratorForClass(p.getType());
                generators.add(generator);
            }

            ClassOrInterfaceDeclaration declaration = new GeneratorBuilder()
                    .addImports(ImportResolver.resolveImports(clazz))
                    .setName(clazz.getName() + "Generator")

                    .build();
            JavaParser parser = new JavaParser();
            CompilationUnit cu = new CompilationUnit();
            ClassOrInterfaceDeclaration generatorClass = new ClassOrInterfaceDeclaration();
            generatorClass.addExtendedType(Generator.class);
            generatorClass.setName(clazz.getName() + "Generator");
            generatorClass.addAnnotation(DefinedGenerator.class);
            ConstructorDeclaration constructorDeclaration = generatorClass.addConstructor(Modifier.Keyword.PUBLIC);

            constructorDeclaration.setName(generatorClass.getName());

            String blockStatement = "return new " + clazz.getName() + "(";

            for (Generator g : generators) {
                var varName = g.getClass().getSimpleName().toLowerCase();
                constructorDeclaration.addParameter(g.getClass(), varName);
                blockStatement += varName+".generate(),";
            }
            constructorDeclaration.setBody(Parser.parseOrException(parser.parseBlock(blockStatement), "Could not parse block statement"));

            cu.addType(generatorClass);

            return cu.toString();
        }
        return null;
    }

    public static Generator<?> getGeneratorForClass(Class<?> aClass) throws ClassNotFoundException {
        System.out.println(aClass);


        switch (aClass.getName()) {
            case ("int"), ("java.lang.Integer"):
                return instantiateGenerator(availableGenerators.get(Integer.class));
            case ("java.lang.String"), ("java.lang.StringBuffer"):
                return instantiateGenerator(availableGenerators.get(String.class));
        }

            return instantiateGenerator(availableGenerators.get(Class.forName(aClass.toString())));
        }

    private static Generator<?> getGeneratorForClass(Type aClass) {
        System.out.println(aClass);
        if (aClass.isPrimitiveType()) {
            switch (aClass.asString()) {
                case ("int"):
                    return instantiateGenerator(availableGenerators.get(Integer.class));
                    //TODO fill in
            }
        } else {
            try {
                return instantiateGenerator(availableGenerators.get(Class.forName(aClass.asString())));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private static Generator<?> instantiateGenerator(Class<? extends Generator> generatorClass) {
        for (Constructor<?> constr : generatorClass.getConstructors()){
            if (constr.getParameterCount() == 1 && constr.getParameterTypes()[0] == Random.class) {
                try {
                    return (Generator<?>) constr.newInstance(r);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String generatorFile = generateGeneratorForClass(new JavaParser().parse("class A { public A(int i) {} }").getResult().get().getClassByName("A").get().asClassOrInterfaceDeclaration());
        System.out.println(generatorFile);
    }

    public static boolean hasGeneratorForClass(@NotNull Class<?> get) {
        return availableGenerators.containsKey(get);
    }
}
