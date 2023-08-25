package com.example.processors;

import autovalue.shaded.com.google.auto.service.AutoService;
import com.example.annotations.Axiom;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.net.URLClassLoader;


@SupportedAnnotationTypes("org.annotations.Axiom")
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
    }
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        annotations.forEach(annotation ->  {

            roundEnv.getElementsAnnotatedWith(annotation)
                    .forEach(element -> {
                        Element e = element.getEnclosingElement();
                        var c = e.getKind().getDeclaringClass().getSuperclass();
                        while (c != null) {
                            System.out.println(c.toString());
                            //get methods annotated by axiom from all classes

                            c = c.getSuperclass();
                        }
                        var t = typeUtils.getDeclaredType((TypeElement)e);
                        TypeElement typeElement = (TypeElement) e;
                        //generate a junit test file
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

    private static void generateTestFile(TypeElement typeElement) {
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

        File file = new File(folder.getPath() + "/" + typeElement.getSimpleName() + "Test.java");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("Could not create file");
        }

        try (FileOutputStream fos = new FileOutputStream(file)){
            fos.write("package org.axioms;\n".getBytes());
            fos.write("import org.junit.jupiter.api.Test;\n".getBytes());
            fos.write("import static junit.framework.Assert.assertEquals;\n".getBytes());
            fos.write("public class ".getBytes());
            fos.write((typeElement.getSimpleName() + "Test {\n").getBytes());
            fos.write("@Test\n".getBytes());
            fos.write("public void test() {\n".getBytes());
            fos.write("assertEquals(1,1);\n".getBytes());
            fos.write("}}\n".getBytes());
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
