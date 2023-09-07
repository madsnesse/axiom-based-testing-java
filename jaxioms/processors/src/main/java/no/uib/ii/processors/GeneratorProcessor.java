package no.uib.ii.processors;

import autovalue.shaded.com.google.auto.service.AutoService;
import no.uib.ii.DataGenerator;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.swing.text.Position;
import java.lang.reflect.Constructor;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("no.uib.ii.Generator")
public class GeneratorProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        this.elementUtils = env.getElementUtils();
        this.typeUtils = env.getTypeUtils();
        this.processingEnv = env;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("Hello, World!");
        annotations.forEach((annotation) -> {
            System.out.println(annotation.getQualifiedName());

            roundEnv.getElementsAnnotatedWith(annotation)
                    .forEach(element -> {

                        var c = element.getEnclosingElement().getKind().getDeclaringClass().getSuperclass();

                        TypeElement typeElement = (TypeElement) element;

                        var declaredType = typeUtils.getDeclaredType(typeElement);
                        var name = typeElement.getQualifiedName().toString();
                        // Load the class using reflection
                        try {
                            Class<?> clazz = Class.forName(name);
                            Constructor<?> constructor = clazz.getDeclaredConstructor();
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                    });

        });
        return false;
    }
}
