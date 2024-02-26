package no.uib.ii.processors

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import no.uib.ii.AxiomDefinition
import no.uib.ii.FileUtils
import no.uib.ii.annotations.AxiomForExistingClass
import javax.annotation.processing.Filer
import javax.lang.model.element.*
import javax.lang.model.util.Types

class UserDefinedProcessing {


    companion object {
        fun processAxiom(elementsAnnotatedWith: Set<Element>?,
                         filer: Filer,
                         typeUtils: Types,
                         axiomDeclarations: MutableMap<String, MutableList<AxiomDefinition>>): MutableMap<String, MutableList<AxiomDefinition>> {

            elementsAnnotatedWith?.forEach {
                var typeElement = it.enclosingElement as TypeElement
                val axiomMethod = processAxiomMethod(it, typeElement, filer, typeUtils)

                val existingAxiomsForClass = axiomDeclarations.getOrDefault(
                    typeElement.qualifiedName.toString(),
                    ArrayList()
                );
                existingAxiomsForClass.add(axiomMethod)
                axiomDeclarations[typeElement.qualifiedName.toString()] = existingAxiomsForClass
            }
            return axiomDeclarations;
        }

        private fun processAxiomMethod(
            element: Element,
            typeElement : TypeElement,
            filer: Filer,
            typeUtils: Types
        ) : AxiomDefinition {

            val methodName = (element as ExecutableElement).simpleName.toString();
            val parameters = element.parameters;

            //TODO check if it is a interface, if so find the classes in the classpath that implement said interface

            val cu = FileUtils.getCompilationUnitForTypeElement(typeElement, filer);

            if (cu.isInterface or cu.isAbstract) {
                //find all children in classpath
                var l = FileUtils.getAllSourceFilesInClassPath(filer, typeUtils);
            }

            var methodDeclaration: MethodDeclaration? =
                getMethodDeclarationForAxiom(cu.methods, methodName, parameters);

            return AxiomDefinition(methodDeclaration!!)
        }

        private fun getMethodDeclarationForAxiom(
            methods: List<MethodDeclaration>,
            methodName: String,
            parameters: MutableList<out VariableElement>
        ): MethodDeclaration? {
            var result: MethodDeclaration? = null
            methods.forEach(fun(method: MethodDeclaration) {
                if (method.nameAsString == methodName) {
                    var allContains = true
                    if (allParametersAreEqual(method.parameters, parameters)) {
                        result = method;
                    }
                }
            })
            return result
        }


        private fun allParametersAreEqual(parameters: NodeList<Parameter>?, parameters1: List<VariableElement>): Boolean {
            if (parameters == null) {
                return false;
            }
            if (parameters.size != parameters1.size) {
                return false;
            }
            for (i in 0 until parameters.size) {
                var p = parameters1[i].asType().toString().split(".").last(); //TODO: maybe not do this so hacky
                val p1 = parameters[i].type.toString()
                if (p1 != p) {
                    return false;
                }
            }
            return true;

        }

        fun applyAxiomsFromParent(
            elementsAnnotatedWith: Set<Element>?,
            typeUtils: Types?,
            axiomDeclarations: MutableMap<String, MutableList<AxiomDefinition>>
        ): MutableMap<String, MutableList<AxiomDefinition>> {

            elementsAnnotatedWith?.forEach(
                fun(element: Element) {

                    var typeElement = element as TypeElement
                    var axioms = axiomDeclarations.getOrDefault(typeElement.qualifiedName.toString(),ArrayList<AxiomDefinition>())
                    typeElement.interfaces.forEach {
                        var e = typeUtils?.asElement(it) as TypeElement;
                        val axs = axiomDeclarations[e.qualifiedName?.toString()]
                        if (axs != null) {
                            axioms.addAll(axs)
                        }

                    }

                    var e = typeUtils?.asElement(typeElement.superclass) as TypeElement
                    val axs = axiomDeclarations[e.qualifiedName.toString()]
                    if (axs != null) {
                        axioms.addAll(axs)
                    }

                    axioms = convertGenericAxioms(axioms, typeElement, typeUtils)
                    axiomDeclarations[typeElement.qualifiedName.toString()] = axioms

                }
            )
            return axiomDeclarations

        }

        private fun convertGenericAxioms(
            axioms: MutableList<AxiomDefinition>,
            typeElement: TypeElement,
            typeUtils: Types?
        ): MutableList<AxiomDefinition> {

            axioms.forEach { axiomDefinition ->
                if (axiomDefinition.isGeneric()) {
                    var m = axiomDefinition.getMethod()
                    var body = m.body.orElseThrow()
                    m.parameters.forEach {
                        val interfaceType = it.type
                        val t = StaticJavaParser.parseType(typeElement.qualifiedName.toString())
                        body.statements.forEach {
                            if (it.isExpressionStmt) {
                                var e = it.asExpressionStmt()
                                if (e.expression.isVariableDeclarationExpr) {
                                    var v = e.expression.asVariableDeclarationExpr()
                                    v.variables.forEach { v ->
                                        if (v.type.equals(interfaceType)) {
                                            v.type = t
                                        }
                                    }
                                }

                            }
                        }
                        it.type = t
                    }

                    m.typeParameters = NodeList()
                    axiomDefinition.setGeneric(false)
                }
            }
            return axioms
        }

        fun processAxiomForExistingClass(
            elementsAnnotatedWith: Set<Element>?,
            filer: Filer,
            typeUtils: Types,
            axiomDeclarations: MutableMap<String, MutableList<AxiomDefinition>>
        ) {
            elementsAnnotatedWith?.forEach {
                val annotation = it.getAnnotation(AxiomForExistingClass::class.java)
                var typeElement = it.enclosingElement as TypeElement
                val axiomMethod = processAxiomMethod(it, typeElement, filer, typeUtils)
                axiomMethod.setGeneric(true)
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
