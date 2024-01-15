package no.uib.ii;

import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.Type;
import no.uib.ii.annotation.DefinedGenerator;
import no.uib.ii.defaultgenerators.Generator;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.Filer;
import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static no.uib.ii.parser.CommonParserMethods.parseOrException;

public class DataGenerator {

    private static final Random r = new Random();
    private static Map<Class<?>, Class<? extends Generator>> availableGenerators = GeneratorFinder.defaultGenerators();
    public String generateGeneratorForClass(ClassOrInterfaceDeclaration clazz, Filer filer) {

        for (ConstructorDeclaration constructor: clazz.getConstructors()) {
            Iterator<Parameter> parameters = constructor.getParameters().iterator();

            List<Generator> generators = new ArrayList<>();
            while (parameters.hasNext()) {
                var p = parameters.next();
                var generator = getGeneratorForClass(p.getType());
                generators.add(generator);
            }

            JavaParser parser = new JavaParser();
//            ClassOrInterfaceDeclaration declaration = new GeneratorBuilder()
//                    .addImports(Parser.parseOrException(parser.parseImport("import")))
//                    .setName(clazz.getName() + "Generator")
//
//                    .build();
            CompilationUnit cu = loadGeneratorTemplate(filer);

            ClassOrInterfaceDeclaration generatorClass = null;
            for (Node n : cu.getChildNodes()) {
                if (n instanceof ClassOrInterfaceDeclaration c) {
                    generatorClass = c;
                }
            }

            generatorClass.setName(clazz.getName() + "Generator");
            ConstructorDeclaration defaultConstructor = generatorClass.getDefaultConstructor().get();
            var defaultConstructorStatement = defaultConstructor.getBody();
            ConstructorDeclaration constructorDeclaration = generatorClass.addConstructor(Modifier.Keyword.PUBLIC);

            constructorDeclaration.setName(generatorClass.getName());

            BlockStmt generateStatement = new BlockStmt();
            String generateStatements = "return new " + clazz.getName() + "(";

            BlockStmt constructorStatement = new BlockStmt();
            constructorStatement.addStatement("this();");
            for (int i = 0; i < generators.size(); i++) {
                Generator g = generators.get(i);
                var varName = "var" + i;
                generatorClass.addField(g.getClass(), "var" + i, Modifier.Keyword.PRIVATE);
                constructorDeclaration.addParameter(g.getClass(), varName);
                defaultConstructorStatement.addStatement("this." + varName + " = new " + g.getClass().getSimpleName() + "(this.random);");
                constructorStatement.addStatement( "this." + varName + " = " + varName + "; \n");
                generateStatements += varName+".generate()" + (i < generators.size()-1?",":");");
            }
            defaultConstructor.setBody(defaultConstructorStatement);
            constructorDeclaration.setBody(constructorStatement);
            generateStatement.addStatement(generateStatements);
            var method = generatorClass.getMethodsByName("generate");
            method.get(0).setBody(generateStatement);
            return cu.toString();
        }
        return null;
    }

    private static CompilationUnit loadGeneratorTemplate(Filer filer) {
        var d = DataGenerator.class.getClassLoader().getResourceAsStream("Generator_Template");
        return StaticJavaParser.parse(d);
//        FileUtils.Companion.getSourceFile(filer, "", "Generator_Template");
//        URL path = DataGenerator.class.getClassLoader().getResource("Generator_Template");
//        if (path == null) {
//            throw new UnexpectedParseException("Path to Generator_Template could not be established");
//        }
//        URI pathAsUri = null;
//        try {
//            pathAsUri = path.toURI();
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//        try (BufferedReader br = Files.newBufferedReader(Path.of(pathAsUri));) {
//            return StaticJavaParser.parse(br);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
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
//        DataGenerator dataGenerator = new DataGenerator();
//        String generatorFile = dataGenerator.generateGeneratorForClass(new JavaParser().parse("class A { public A(int i) {} }").getResult().get().getClassByName("A").get().asClassOrInterfaceDeclaration());
//        System.out.println(generatorFile);
    }

    public static boolean hasGeneratorForClass(@NotNull Class<?> get) {
        return availableGenerators.containsKey(get);
    }
}
