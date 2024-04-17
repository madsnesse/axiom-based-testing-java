package no.uib.ii

class QualifiedClassName {
    private var packageName: String
    private var className: String
    constructor(qualifiedClassName: String) {
        this.packageName = qualifiedClassName.substringBeforeLast(".")
        this.className = qualifiedClassName.substringAfterLast(".")
    }
    constructor(packageName: String, className: String) {
        this.packageName = packageName
        this.className = className
    }

    fun getPackageName(): String {
        return packageName
    }

    fun getClassName(): String {
        return className
    }

    fun equals(qualifiedClassName: QualifiedClassName): Boolean {
        return this.packageName == qualifiedClassName.packageName && this.className == qualifiedClassName.className
    }

    fun equalsString(qualifiedClassName: String): Boolean {
        return this.packageName == qualifiedClassName.substringBeforeLast(".") && this.className == qualifiedClassName.substringAfterLast(".")
    }

}
