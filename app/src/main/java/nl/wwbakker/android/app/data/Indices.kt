package nl.wwbakker.android.app.data

import java.nio.IntBuffer

class Indices(values: IntArray) {
    val indexBuffer: IntBuffer = run {
        val result = IntBuffer.allocate(values.size)
        result.put(values)
        result.position(0)
        result
    }
    val length = values.size

    operator fun plus(other : Indices) : Indices {
        return Indices(indexBuffer.array() + other.indexBuffer.array())
    }

    @Deprecated("Debug function")
    fun debugSubArray(limit : Int = 0, skip : Int = 0) : Indices {
        return Indices(indexBuffer.array().drop(skip).take(limit).toIntArray())
    }
}