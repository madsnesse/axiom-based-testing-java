package no.uib.ii.processors

import com.github.javaparser.JavaParser
import com.github.javaparser.ParseException
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.*
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import com.github.javaparser.ast.type.ClassOrInterfaceType
import no.uib.ii.ASTTraverser
import no.uib.ii.AxiomDefinition
import no.uib.ii.DataGenerator
import no.uib.ii.FileUtils
import no.uib.ii.parser.CommonParserMethods
import java.io.File
import java.util.*
import javax.annotation.processing.Filer
import javax.annotation.processing.FilerException

class TestClassGenerator (private val dataGenerator: DataGenerator) {

        private val parser = JavaParser();
        fun generateTestClassesForAxioms(axiomDeclarations: Map<String, List<AxiomDefinition>>, filer: Filer?) {

            val generators: Map<String, List<String>> = getGeneratorsForAxioms(filer!!, axiomDeclarations);

            for (axiomDeclaration in axiomDeclarations) {
                val imports = resolveImports(axiomDeclaration.key, axiomDeclaration.value, generators[axiomDeclaration.key].orEmpty()).toMutableList()
                if (dataGenerator.availableGenerators.containsKey(axiomDeclaration.key.substringAfterLast("."))) {
                    val generatorName = dataGenerator.availableGenerators[axiomDeclaration.key.substringAfterLast(".")]!!
                    imports += StaticJavaParser.parseImport("import $generatorName ;");
                }
                if (generators.containsKey(axiomDeclaration.key) || dataGenerator.availableGenerators.containsKey(axiomDeclaration.key.substringAfterLast("."))) {
                    generateTestClass(
                        filer,
                        axiomDeclaration.key,
                        axiomDeclaration.value,
                        imports
                    )
                }
            }


        }

        private fun getGeneratorsForAxioms(
            filer: Filer,
            axiomDeclarations: Map<String, List<AxiomDefinition>>
        ): Map<String, List<String>> {
            var result = HashMap<String, List<String>>()
            for ((className, axiomDefinitions) in axiomDeclarations) {
                var generators = ArrayList<String>()
                for (axiomDeclaration in axiomDefinitions) {
                    if (axiomDeclaration.isGeneric()) continue;
                    val requiredClasses = getRequiredClasses(axiomDeclaration)
                    if (axiomDeclaration.isGeneric()) continue;
                    requiredClasses.forEach { requiredClass ->
                        val clazz = getClassFromClassName(requiredClass);
                        if (clazz.isPresent) {
                            if (dataGenerator.hasGeneratorForClass(clazz.get())) {
//                                predefinedGenerators.add(DataGenerator.getGeneratorForClass(clazz.get()));
                                val generator = dataGenerator.getGeneratorForClass(clazz.get()).javaClass
                                generators.add(generator.name)
                            }
                            //generators.add(DataGenerator.generateGeneratorForClass(clazz.get()))
                        } else if (dataGenerator.availableGenerators.containsKey(requiredClass.substringAfterLast("."))) {
                            //no need to generate
                        }
                        else { //Check if it is a type
                            val fileName = requiredClass.split(".").last() + ".java"
                            val packageName = requiredClass.split(".").dropLast(1).joinToString(".")
                            val c = FileUtils.getSourceFile(filer, packageName, fileName)
                            val cd = ASTTraverser().loadClassFromSource(c).second
                            //TODO check if exists
                            val f: File =
                                File("target/generated-sources/annotations/no/uib/ii/jaxioms/generators/${cd.nameAsString}Generator.java");
                            if (f.exists()) {
                                f.delete();
                            }
                            //TODO create in test folder instead
                            var gen = getDataGenerator(cd, filer);
                            gen.ifPresent { gen ->
                                var g = gen.getType(0);
                                if (!generators.any { s -> s.equals(g.fullyQualifiedName.get()) }) {
                                    generators.add(gen.getType(0).fullyQualifiedName.get())
                                    try {
                                        filer.createSourceFile("no.uib.ii.jaxioms.generators.${cd.nameAsString}Generator")
                                            .openWriter().use { writer ->
                                                writer.write(gen.toString())
                                            }
                                    } catch (e: FilerException) {
                                        //TODO
                                    }
                                }
                            }
                        }
                    }
                }
                if (generators.isNotEmpty()) result[className] = generators
            }

            return result;
        }

        private fun getClassFromClassName(requiredClass: String): Optional<Class<*>> {
            return try {
                val c = Class.forName(requiredClass);
                Optional.of(c as Class<*>);
            } catch (e: Exception) {
                println("$requiredClass is not a class")
                Optional.empty();
            }
        }

        private fun getRequiredClasses(method: AxiomDefinition): List<String> {
            var result = HashSet<String>()
            method.getMethod().parameters.forEach { parameter ->
                if (!isTypeParameter(parameter)) {
                    result.add(resolveName(parameter))
                } else {
                    method.setGeneric(true)
                }
            }
            return result.toList();

        }

        private fun isTypeParameter(parameter: Parameter?): Boolean {

            var b = parameter?.parentNode?.ifPresent { m ->
                var parentClass = m.parentNode.ifPresent { c ->
                    c as ClassOrInterfaceDeclaration
                    var tp = parameter.type as ClassOrInterfaceType
                    if (c.typeParameters.any { it.name.equals(tp.name) }) {
                        true
                    }
                    false
                }
            }
            return false
        }

        private fun resolveName(param: Parameter): String {
//            val m = param.parentNode.get() as MethodDeclaration
//            val c = m.parentNode.get() as ClassOrInterfaceDeclaration
//            val p = c.parentNode.get() as CompilationUnit
            var name = param.type.asString()
            var packagename = ""
            param.parentNode.ifPresent { m ->
                (m as MethodDeclaration).parentNode.ifPresent { c ->
                    (c as ClassOrInterfaceDeclaration).parentNode.ifPresent { p ->
                        (p as CompilationUnit).packageDeclaration.ifPresent {
                            packagename = it.nameAsString + "."
                        }
                    }
                }
            }
            if (packagename != "" && !name.contains(packagename)) {
                return packagename + name
            }
            return name
        }

        private fun getDebugMethod(): BodyDeclaration<*>? {
            var d = "   @BeforeAll\n" +
                    "    public static void t() {\n" +
                    "        System.out.println(\"fei8has\");\n" +
                    "    }"

            return CommonParserMethods.parseOrException(parser.parseMethodDeclaration(d), "error")
        }

        private fun getStreamMethod(className: String, numberOfArguments: Int = 1, name: String): BodyDeclaration<*>? {
            var generateStatement = (1..numberOfArguments).joinToString(",") { "Named.of(\"Argument $it:\",clazzGenerator.generate())" }
            var s = "public static Stream<Arguments> $name(){\n" +
                    "        Generator<Clazz> clazzGenerator = new ClazzGenerator();\n" +
                    "        List<Arguments> clazzStream = new ArrayList(); \n" +
                    "        for (int i = 0; i < NUMBER_OF_CASES; i++) {\n" +
                    "            clazzStream.add(Arguments.of($generateStatement));\n" + //TODO dont have fixed number
                    "        }\n" +
                    "        System.out.println(clazzStream); \n" +
                    "        return clazzStream.stream();\n" +
                    "    }"
            s = s.replace("Clazz", className)
            s = s.replace("NUMBER_OF_CASES", "100"); //TODO replace with value for 100
            return CommonParserMethods.parseOrException(parser.parseMethodDeclaration(s), "could not parse")
        }

        private fun generateTestClass(
            filer: Filer,
            className: String,
            axiomDeclarations: List<AxiomDefinition>,
            imports: List<ImportDeclaration>
        ) {

            var cu: CompilationUnit = CompilationUnit("annotations.no.uib.ii.jaxioms");

            val methods: List<MethodDeclaration> =
                axiomDeclarations.map { axiomDeclaration -> axiomDeclaration.getMethod() }

            imports.forEach(fun(import: ImportDeclaration) {
                cu.addImport(import)
            })
            val className = className.split(".").last();

            var classDeclaration = cu.addClass("${className}GeneratedTest");
            methods.forEach(fun(method: MethodDeclaration) {
                val args = (1..method.parameters.size).joinToString(",") { "{${(it - 1)}}" }
                classDeclaration.addMember(
                    MethodDeclaration().setBody(method.body.orElseThrow())
                        .setType(method.type)
                        .setName(method.name)
                        .setParameters(method.parameters)
                        .setModifiers(method.modifiers)
                        .setStatic(false) //TODO skriv om hvorfor static ikke fungerte, tester ble ikke oppdaget
                        .setThrownExceptions(method.thrownExceptions)
                        .addAnnotation("ParameterizedTest")
                        .addSingleMemberAnnotation("DisplayName", parser.parseExpression<VariableDeclarationExpr>("value=\"${method.name} < $args >\"").result.get())
                        .addSingleMemberAnnotation("MethodSource", "\"factory${method.name}\"")
                )
                classDeclaration.addMember(
                    getStreamMethod(className, method.parameters.size, "factory${method.name}")
                )
            })

            //classDeclaration.addMember(getDebugMethod())

//                var b = MethodDeclaration().setBody(StaticJavaParser.parseBlock("""
//                    |System.out.println("Hello world");
//                """.trimMargin()))
//                try{
//                    classDeclaration.addMember(
//                        b.setName("printSomething")
//                            .setType("void")
//                    )
//                }catch (e: Exception) {
//                    println(e)
//                }
//                println(cu.toString())

            //TODO check if exists
            val f: File =
                File("target/generated-sources/annotations/no/uib/ii/jaxioms/GeneratedTestClass${className}.java");
            if (f.exists()) {
                f.delete();
            }
            //TODO create in test folder instead

            filer.createSourceFile("no.uib.ii.jaxioms.${className}GeneratedTest").openWriter().use { writer ->
                writer.write(cu.toString())
            }

        }

        private fun resolveImports(
            className: String,
            axiomDeclarations: List<AxiomDefinition>,
            generators: List<String>
        ): List<ImportDeclaration> {

            val list = mutableListOf<ImportDeclaration>();
            list += (ImportDeclaration("org.junit.jupiter.api.Assertions.assertEquals", true, false));
            list += (ImportDeclaration("org.junit.jupiter.api.Assertions.assertTrue", true, false));
            list += (ImportDeclaration("org.junit.jupiter.params.ParameterizedTest", false, false));
            list += (ImportDeclaration("org.junit.jupiter.api.BeforeAll", false, false));
            list += (ImportDeclaration("org.junit.jupiter.params.provider.Arguments", false, false));
            list += (ImportDeclaration("org.junit.jupiter.api.Named", false, false));
            list += (ImportDeclaration("org.junit.jupiter.params.provider.MethodSource", false, false));
            list += (ImportDeclaration("org.junit.jupiter.api.DisplayName", false, false));
            list += (ImportDeclaration("java.util.ArrayList", false, false));
            list += (ImportDeclaration("java.util.List", false, false));
            list += (ImportDeclaration("java.util.stream.Stream", false, false));
            list += (ImportDeclaration("no.uib.ii.defaultgenerators.Generator", false, false));
            //list += (ImportDeclaration("org.junit.jupiter.api.extension.ExtendWith", false, false));
            list += (ImportDeclaration(className, false, false));
            axiomDeclarations.forEach { axDef ->
                axDef.getMethod().parameters.forEach { parameter ->

//                    var import = Resolver.resolveType(parameter.type)
//                    list += ImportDeclaration(import , false, false);
                    //TODO resolve import for custom types
                    //TODO use parameter.type.resolve() maybe

                }

            }
            generators.forEach { generator ->
                list += ImportDeclaration(generator, false, false);
            }

            return list;
        }

        private fun getDataGenerator(clazz: ClassOrInterfaceDeclaration, filer: Filer): Optional<CompilationUnit> {
            val s = dataGenerator.generateGeneratorForClass(clazz, filer)
            val parseResult = parser.parse(s)
            if (!parseResult.isSuccessful) {
                throw ParseException("Could not parse generated data generator")
            }

            return parseResult.result;
        }
}
