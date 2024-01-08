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
