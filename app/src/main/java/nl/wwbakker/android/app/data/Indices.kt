package nl.wwbakker.android.app.data

import java.nio.IntBuffer

class Indices(values: IntArray) {
    val indexBuffer = run {
        val result = IntBuffer.allocate(values.size)
        result.put(values)
        result.position(0)
        result
    }
    val length = values.size
}