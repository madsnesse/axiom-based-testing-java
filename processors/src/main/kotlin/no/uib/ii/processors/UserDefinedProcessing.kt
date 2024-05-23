package no.uib.ii.processors

import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import no.uib.ii.AxiomDefinition
import no.uib.ii.FileUtils
import no.uib.ii.QualifiedClassName
import no.uib.ii.annotations.AxiomForExistingClass
import javax.annotation.processing.Filer
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.Types

class UserDefinedProcessing {


    companion object {
        fun processAxiom(
            elementsAnnotatedWith: Set<Element>?,
            filer: Filer,
            typeUtils: Types,
            axiomDeclarations: MutableMap<String, MutableList<AxiomDefinition>>
        ): MutableMap<String, MutableList<AxiomDefinition>> {

            elementsAnnotatedWith?.forEach (
                fun (element: Element) {
                    val typeElement = element.enclosingElement as TypeElement
                    val axiomMethod = processAxiomMethod(element, typeElement, filer, typeUtils)

                    val existingAxiomsForClass = axiomDeclarations.getOrDefault(
                        typeElement.qualifiedName.toString(),
                        ArrayList()
                    );
                    existingAxiomsForClass.add(axiomMethod)
                    axiomDeclarations[typeElement.qualifiedName.toString()] = existingAxiomsForClass
                }
            )
            return axiomDeclarations;
        }

        private fun processAxiomMethod(
            element: Element,
            typeElement: TypeElement,
            filer: Filer,
            typeUtils: Types
        ): AxiomDefinition {

            val methodName = (element as ExecutableElement).simpleName.toString();
            val parameters = element.parameters;

            //TODO check if it is a interface, if so find the classes in the classpath that implement said interface

            val cu = FileUtils.getClassOrInterfaceForTypeElement(typeElement, filer);

            if (cu.isInterface or cu.isAbstract) { //TODO remove
                //find all children in classpath
                var l = FileUtils.getAllSourceFilesInClassPath(filer, typeUtils);
            }

            var methodDeclaration: MethodDeclaration? =
                getMethodDeclarationForAxiom(cu.methods, methodName, parameters);

            return AxiomDefinition(methodDeclaration!!,
                qualifiedClassName = QualifiedClassName(cu.fullyQualifiedName.orElseThrow()),
                generic = methodDeclaration.isGeneric)
        }

        private fun getMethodDeclarationForAxiom(
            methods: List<MethodDeclaration>,
            methodName: String,
            parameters: MutableList<out VariableElement>
        ): MethodDeclaration? {
            var result: MethodDeclaration? = null
            methods.forEach(fun(method: MethodDeclaration) {
                if (method.nameAsString == methodName) {
                    if (allParametersAreEqual(method.parameters, parameters)) {
                        result = method;
                    }
                }
            })
            return result
        }


        private fun allParametersAreEqual(
            parameters: NodeList<Parameter>?,
            parameters1: List<VariableElement>
        ): Boolean {
            if (parameters == null) {
                return false;
            }
            if (parameters.size != parameters1.size) {
                return false;
            }
            for (i in 0 until parameters.size) {
                var p = parameters1[i].asType().toString().substringAfterLast(".")
                val p1 = parameters[i].type.toString()
                if (p1 != p) {
                    return false;
                }
            }
            return true;

        }

        fun applyAxiomsFromParent(
            elementsAnnotatedWith: Set<Element>?,
            filer: Filer,
            typeUtils: Types?,
            axiomDeclarations: MutableMap<String, MutableList<AxiomDefinition>>
        ): MutableMap<String, MutableList<AxiomDefinition>> {

            elementsAnnotatedWith?.forEach(
                fun(element: Element) {
                    var typeElement = element as TypeElement
                    var axioms = axiomDeclarations.getOrDefault(
                        typeElement.qualifiedName.toString(),
                        ArrayList()
                    )
                    typeElement.interfaces.forEach {
                        var e = typeUtils?.asElement(it) as TypeElement;
                        val axs = axiomDeclarations[e.qualifiedName?.toString()]
                        if (axs != null) {
                            axioms.addAll(axs)
                        }

                    }

                    var e = typeUtils?.asElement(typeElement.superclass) as TypeElement
                    axiomDeclarations[e.qualifiedName.toString()]?.forEach { axiom ->
                        axioms.add(axiom.copy())
                    }

                    axioms = convertGenericAxioms(axioms, typeElement, typeUtils)
                    axioms = convertParentAxioms(axioms.toMutableList(), typeElement, filer).toMutableList()
                    axiomDeclarations[typeElement.qualifiedName.toString()] = axioms

                }
            )
            return axiomDeclarations

        }


        fun processAxiomForExistingClass(
            elementsAnnotatedWith: Set<Element>?,
            filer: Filer,
            typeUtils: Types,
            axiomDeclarations: MutableMap<String, MutableList<AxiomDefinition>>
        ) {
            elementsAnnotatedWith?.forEach { element ->
                var typeElement = element.enclosingElement as TypeElement
                val annotation = element.getAnnotation(AxiomForExistingClass::class.java)
                val axiomMethod = processAxiomMethod(element, typeElement, filer, typeUtils)
                axiomMethod.setGeneric(true)
                axiomMethod.setQualifiedClassName(QualifiedClassName(annotation.className))
                val existingAxiomsForClass = axiomDeclarations.getOrDefault(
                    annotation.className,
                    ArrayList()
                );
                existingAxiomsForClass.add(axiomMethod)
                axiomDeclarations[annotation.className] = existingAxiomsForClass
            }
        }


    }

}
