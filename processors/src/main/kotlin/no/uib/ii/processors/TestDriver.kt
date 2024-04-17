package no.uib.ii.processors

class TestDriver {
    companion object {
        fun runTest(n: Int, test: () -> Unit) {
            for (i in 1..n) {
                test()
            }
        }

    }

}