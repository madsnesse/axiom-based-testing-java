package no.uib.ii.processors

import com.github.javaparser.ast.expr.MemberValuePair
import com.github.javaparser.ast.expr.StringLiteralExpr
import no.uib.ii.AxiomDefinition
import no.uib.ii.FileUtils
import javax.annotation.processing.Filer
import javax.lang.model.element.*


class AlgebraicStructureProcessing {
    companion object {
        fun process(annotation: TypeElement, elementsAnnotatedWith: Set<Element>?, filer: Filer): Map<String, List<AxiomDefinition>> {
            var result = HashMap<String, List<AxiomDefinition>>()

            elementsAnnotatedWith?.forEach { element: Element ->
                println(element)
                var classDeclaration = FileUtils.getCompilationUnitForTypeElement(element as TypeElement, filer)

                var annotationExpr = classDeclaration.getAnnotationByName(annotation.simpleName.toString()).get()
                annotationExpr = annotationExpr.asNormalAnnotationExpr()
                var p = annotationExpr.pairs;
                for (item : MemberValuePair in p) {
                    var n = item.name
                    var v = item.value as StringLiteralExpr
                    println("$n $v")
                    //verify that value is a function in the class annotated
                    if (classDeclaration.getMethodsByName(v.asString()).size == 1) {
                        var m = classDeclaration.getMethodsByName(v.asString()).get(0);
                        print(m)
                    } else {
                        println("Error, no method of name $v")
                    }
                }

            }

            return result
        }
    }

}
