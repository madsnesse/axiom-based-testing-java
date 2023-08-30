package no.uib.ii.processors;

import autovalue.shaded.com.google.auto.service.AutoService;
import no.uib.ii.DataGenerator;
import no.uib.ii.Axiom;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.net.URLClassLoader;

@SupportedAnnotationTypes("no.uib.ii.Axiom")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor

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

                        DataGenerator dataGenerator = null;

                        try {
                            typeUtils.getDeclaredType((TypeElement)e);
                            var c = Class.forName("com.example.experiments." + typeElement.getSimpleName() + "Generator");
                            dataGenerator = (DataGenerator) Class.forName("com.example.experiments." + typeElement.getSimpleName() + "Generator.java").getConstructor().newInstance();

                        } catch (Exception exception) {
                            System.out.println("Could not create generator");
                        }

                        generateTestFile(typeElement);
                        Axiom annotation1 = element.getAnnotation(Axiom.class);
                        System.out.println(annotation1.numberOfTestCases());
                        ElementKind elementKind = element.getKind();
                        if (elementKind.equals(ElementKind.METHOD)) {
                            System.out.println("hei" + element.getSimpleName());
                        }
                    });
        });
        return true;
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

    private static void generateTestFile(TypeElement typeElement) {
        //Use processingEnv.getFiler() to create a file ?

        ClassLoader classLoader = URLClassLoader.getSystemClassLoader();
        System.out.println(typeElement.getQualifiedName());
        // Fetch the resource in the form of a URL
        URL resource = classLoader.getResource(typeElement.getSimpleName() + ".java");

        if (resource != null) {
            // Extract the path from the URL
            String classPath = resource.getPath();

            System.out.println("The path of the class is: " + classPath);
        } else {
            System.out.println("Oh, the path remains hidden, lost in obscurity.");
        }
        String currentPath = System.getProperty("user.dir");
        File folder = new File(currentPath + "/processors/src/test/java/org/axioms");
        if (!folder.exists()) {
            folder.mkdirs();
        }


    }

    private void createFileFromTemplate(String className,
                                        String folderPath,
                                        String packageName,
                                        String methodName,
                                        String methodBody) {
        File file = new File(folderPath + "/" + className + ".java");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("Could not create file");
        }

    }

}
