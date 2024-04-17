package no.uib.ii.processors

fun readFileFromResources(fileName: String): String {
    val resource = TestFileReader::class.java.classLoader.getResource(fileName)!!.readText()
    return resource
}

class TestFileReader {

}
