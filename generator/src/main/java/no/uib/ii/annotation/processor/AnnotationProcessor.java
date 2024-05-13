package no.uib.ii.annotation.processor;

import autovalue.shaded.com.google.auto.service.AutoService;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import no.uib.ii.DataGenerator;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;

import static no.uib.ii.annotation.processor.AnnotationProcessor.DEFINED_GENERATOR;

@SupportedAnnotationTypes({DEFINED_GENERATOR})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {

    public static final String DEFINED_GENERATOR = "no.uib.ii.annotations.DefinedGenerator";
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        JavaParser parser = new JavaParser();
        System.out.println("hei");
        for (TypeElement annotation : annotations) {
            for (var element : roundEnv.getElementsAnnotatedWith(annotation)) {
                System.out.println(element);
                String name = element.toString();
                JavaFileManager.Location l = StandardLocation.CLASS_OUTPUT;
                try {
                    var fo = processingEnv.getFiler().createResource(l,"" , "generator_list", element);
                    fo.openWriter().append(name).close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return false;
    }

}
