package no.uib.ii.processors

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import no.uib.ii.AxiomDefinition
import no.uib.ii.DataGenerator
import no.uib.ii.FileUtils
import no.uib.ii.exceptions.UnexpectedError
import javax.annotation.processing.Filer
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Types

class GeneratorProcessing (private val dataGenerator: DataGenerator) {

    fun processGenerator(
        elementsAnnotatedWith: Set<Element>?,
        filer: Filer,
        typeUtils: Types?,
        axiomDeclarations: MutableMap<String, MutableList<AxiomDefinition>>
    ) {
        elementsAnnotatedWith?.forEach(
            fun(element: Element) {
                //get the type of the element
                var extendsGenerator = false;
                val classDeclaration = FileUtils.getClassOrInterfaceForTypeElement(element as TypeElement, filer)
                for (type in classDeclaration.extendedTypes) {
                    if (type.name.toString() == "Generator") {
                        val typeArguments = type.typeArguments.orElseThrow {
                            UnexpectedError("Type arguments for Generator not found")
                        }
                        if (typeArguments.size != 1) {
                            throw UnexpectedError("Generator must have exactly one type argument")
                        }
                        val typeArgument = typeArguments[0]
                        //val id = cu.imports.find { id -> id.nameAsString.endsWith(typeArgument.asString()) }
                        dataGenerator.addGenerator(typeArgument.asString(), classDeclaration.fullyQualifiedName.orElseThrow {
                            UnexpectedError(
                                "Fully qualified name not found"
                            )
                        })
                        extendsGenerator = true;
                    }
                }
                if (!extendsGenerator) {
                    throw UnexpectedError("Class ${classDeclaration.nameAsString} must extend Generator<T>")
                }

                //check that supertype is Generator<T> and set
            }
        )
    }
}
