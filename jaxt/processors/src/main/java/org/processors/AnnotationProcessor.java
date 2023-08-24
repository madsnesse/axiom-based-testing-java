package org.processors;

import autovalue.shaded.com.google.auto.service.AutoService;
import org.annotations.Axiom;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Set;

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
                        TypeElement typeElement = (TypeElement) e;

                        Axiom annotation1 = element.getAnnotation(Axiom.class);
                        ElementKind elementKind = element.getKind();
                        if (elementKind.equals(ElementKind.METHOD)) {
                            System.out.println("hei" + element.getSimpleName());
                        }
                    });
        });
        return true;
    }
}
