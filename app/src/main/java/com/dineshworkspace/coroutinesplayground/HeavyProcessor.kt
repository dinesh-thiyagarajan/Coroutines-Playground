package com.dineshworkspace.coroutinesplayground

class HeavyProcessor {

    fun processDoubleValues(iterations: Int = 1) : Double{
        val data = 0.0001232 + 39089238.3434
        val data2 = 1 + data
        val data3 = 1 + data2
        val data4 = 1 + data3
        var data5 = 1 + data4

        repeat(iterations) {
            for (i in 1..1000000000) {
                data5 += i
            }
        }

        return data5
    }

}