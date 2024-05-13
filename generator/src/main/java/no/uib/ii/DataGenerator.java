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
import no.uib.ii.defaultgenerators.Generator;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.Filer;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DataGenerator {

    private static final String CLASS_NAME_IN_TEMPLATE = "CLASS_NAME";
    private static final Random r = new Random();
    private Map<Class<?>, Class<? extends Generator>> availableGenerators = GeneratorFinder.defaultGenerators();
    public String generateGeneratorForClass(ClassOrInterfaceDeclaration clazz, Filer filer) {
        if (clazz.getConstructors().isEmpty()) {
            return generateGeneratorEmptyConstructor(clazz, filer);
        }
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
            CompilationUnit cu = loadGeneratorTemplateReplaceClassName(filer, CLASS_NAME_IN_TEMPLATE, clazz.getName().asString());

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

            cu.addImport( clazz.getFullyQualifiedName().get());

            return cu.toString();
        }
        return null;
    }

    private String generateGeneratorEmptyConstructor(ClassOrInterfaceDeclaration clazz, Filer filer) {

        CompilationUnit cu = loadGeneratorTemplateReplaceClassName(filer, CLASS_NAME_IN_TEMPLATE, clazz.getName().asString());
        ClassOrInterfaceDeclaration generatorClass = null;
        for (Node n : cu.getChildNodes()) {
            if (n instanceof ClassOrInterfaceDeclaration c) {
                generatorClass = c;
            }
        }
        generatorClass.setName(clazz.getName() + "Generator");

        BlockStmt generateStatement = new BlockStmt();
        generateStatement.addStatement("return new " + clazz.getName() + "();");
        var generateMethod = generatorClass.getMethodsByName("generate");
        generateMethod.get(0).setBody(generateStatement);
        var generatorConstructor = generatorClass.getConstructors().get(0);
        generatorConstructor.setName(clazz.getName()+"Generator");
        cu.addImport(clazz.getFullyQualifiedName().get());

        generatorClass.getExtendedTypes().forEach(t ->{
            System.out.println((t));
            }
        );

        return cu.toString();
    }

    private static CompilationUnit loadGeneratorTemplateReplaceClassName(Filer filer, String oldName, String newName) {
        var d = DataGenerator.class.getClassLoader().getResourceAsStream("Generator_Template");
        try {
            var s = new String(d.readAllBytes());

            return StaticJavaParser.parse(s.replace(oldName, newName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public Generator<?> getGeneratorForClass(Class<?> aClass) throws ClassNotFoundException {
        System.out.println(aClass);


        switch (aClass.getName()) {
            case ("int"), ("java.lang.Integer"):
                return instantiateGenerator(availableGenerators.get(Integer.class));
            case ("java.lang.String"), ("java.lang.StringBuffer"):
                return instantiateGenerator(availableGenerators.get(String.class));
        }

            return instantiateGenerator(availableGenerators.get(Class.forName(aClass.toString())));
        }

    private Generator<?> getGeneratorForClass(Type aClass) {
        System.out.println(aClass);
        switch (aClass.asString()) {
            case ("int"):
                return instantiateGenerator(availableGenerators.get(Integer.class));
            //TODO fill in
            case ("String"):
                return instantiateGenerator(availableGenerators.get(String.class));
        }
        try {
            return instantiateGenerator(availableGenerators.get(Class.forName(aClass.asString())));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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

    public boolean hasGeneratorForClass(@NotNull Class<?> get) {
        return availableGenerators.containsKey(get);
    }
}
