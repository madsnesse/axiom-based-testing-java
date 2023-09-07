package no.uib.ii.processors;

import autovalue.shaded.com.google.auto.service.AutoService;
import no.uib.ii.DataGenerator;
import no.uib.ii.Axiom;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@SupportedAnnotationTypes({"no.uib.ii.Axiom", "no.uib.ii.Generator"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class AxiomProcessor extends AbstractProcessor

{
    private Elements elementUtils;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.elementUtils = processingEnv.getElementUtils();
        this.typeUtils = processingEnv.getTypeUtils();
        this.processingEnv = processingEnv;
    }
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("Hello, World!");
        annotations.forEach(annotation ->  {

            roundEnv.getElementsAnnotatedWith(annotation)
                    .forEach(element -> {

                        Element e = element.getEnclosingElement();

                        List<String> list = getAllAxiomsFromAncestors(e);

                        var t = typeUtils.getDeclaredType((TypeElement)e);
                        TypeElement typeElement = (TypeElement) e;
                        //generate a junit test file

                        //generateTestFile(typeElement);
                        Axiom annotation1 = element.getAnnotation(Axiom.class);
                        var m = element.getModifiers();
                        System.out.println(annotation1.numberOfTestCases());
                        ElementKind elementKind = element.getKind();
                        if (elementKind.equals(ElementKind.METHOD)) {
                            ExecutableElement executableElement = (ExecutableElement) element;
                            var method = extractMethodSignature(typeElement, executableElement);
                            System.out.println("hei" + executableElement.toString());
                            //element.getEnclosedElements().forEach(System.out::println);
                            var parameters = ((ExecutableElement) element).getParameters();
                            var imports = getImportsForType(typeElement);
                            imports += "import static " + typeElement.getQualifiedName() + "." + executableElement.getSimpleName() + ";\n";
                            imports += "import no.uib.ii.DataGenerator;\n";
                            createFileFromTemplate(typeElement.getSimpleName().toString(),
                                    "src/main/java/com/example/experiments",
                                    "com.example.experiments",
                                    "generate",
                                    imports,
                                    method);
                        }
                    });
        });
        return true;
    }

    private String getImportsForType(TypeElement typeElement) {
        return "import " + typeElement.getQualifiedName() + ";\n" +
                "import " + typeElement.getQualifiedName() + "Generator;\n";

    }

    /**
     * Get all methods annotated by axiom from all ancestors of the element
     * TODO Change return type and populate the list with the methods
     */
    private List<String> getAllAxiomsFromAncestors(Element element) {
        List<String> result = new ArrayList<>();
        var c = element.getKind().getDeclaringClass().getSuperclass();
        while (c != null) {
            System.out.println(c.toString());
            //get methods annotated by axiom from all classes
            result.add(c.toString());
            c = c.getSuperclass();
        }
        return result;
    }

    private void createFileFromTemplate(String className,
                                        String folderPath,
                                        String packageName,
                                        String methodName,
                                        String imports,
                                        String methodBody) {

        ClassLoader classLoader = getClass().getClassLoader();
        var resourceStream = classLoader.getResourceAsStream("TestFileTemplate");
        //read from resource file
        String fileContent = "";
        FileOutputStream outstream = null;
        File f = new File(folderPath + "/" + className + "Test.java");

        try {
            if (!f.exists()) {
                var dir = f.getParentFile();
                if (!dir.exists())
                    dir.mkdirs();
                f.createNewFile();
            }
            outstream = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PrintWriter writer = new PrintWriter(outstream);

        try {
            Scanner sc = new Scanner(resourceStream);
            while (sc.hasNextLine()) {
                var line = sc.nextLine();
                if (line.contains("${CLASS_NAME}")) {
                    line = line.replace("${CLASS_NAME}", className);
                }
                if (line.contains("${PACKAGE_NAME}")) {
                    line = line.replace("${PACKAGE_NAME}", packageName);
                }
                if (line.contains("${GENERATOR_CLASS_NAME}")) {
                    line = line.replace("${GENERATOR_CLASS_NAME}", className + "Generator");
                }
                if (line.contains("${TEST_METHOD_NAME}")) {
                    line = line.replace("${TEST_METHOD_NAME}", methodName);
                }
                if (line.contains("${TEST_METHOD_BODY}")) {
                    line = line.replace("${TEST_METHOD_BODY}", methodBody);
                }
                if (line.contains("${IMPORTS}")) {
                    line = line.replace("${IMPORTS}", imports);
                }
                writer.println(line);
            }
        }
        catch (Exception e) {
            System.out.println("Could not read from resource file");
        }

        writer.close();
        System.out.println("File " + f.getPath() + " created successfully");

    }

    public String extractMethodSignature(TypeElement type, ExecutableElement executableElement) {

            var params = executableElement.getParameters();
            //check if we have generator for given type
            String method = "";
            System.out.println(executableElement);


            method += executableElement.getSimpleName().toString() + "(";

            //TODO use string join
            for (int i = 0; i < executableElement.getParameters().size()-1; i++) {
                method += "generator.generate(),";
//                method += executableElement.getParameters().get(i).asType().toString() + ", ";
            }
            method += "generator.generate())";//executableElement.getParameters().get(executableElement.getParameters().size()-1).asType().toString() + ")";
            System.out.println(method);
//            String returnType = typeUtils.asElement(method.getReturnType()).toString();
//            String methodName = method.getSimpleName().toString();
//            List<? extends Element> parameters = method.getParameters();
//
//            StringBuilder signature = new StringBuilder();
//            signature.append(returnType).append(" ").append(methodName).append("(");
//
//            for (int i = 0; i < parameters.size(); i++) {
//                Element parameter = parameters.get(i);
//                String parameterType = typeUtils.asElement(parameter.asType()).toString();
//                signature.append(parameterType);
//                if (i < parameters.size() - 1) {
//                    signature.append(", ");
//                }
//            }
//
//            signature.append(")");
//
//            // Print or process the method signature as needed
//            System.out.println("Method Signature: " + signature.toString());
        return method;
    }
}
