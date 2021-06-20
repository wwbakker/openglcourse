package nl.wwbakker.android.app.data

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Vertices(values : FloatArray, val valuesPerVertex : Int) {
    val vertexBuffer : FloatBuffer = run {
        val bb =
            ByteBuffer.allocateDirect(values.size * 4) // (# of coordinate values * 4 bytes per float)
        bb.order(ByteOrder.nativeOrder())
        val result = bb.asFloatBuffer()
        result.put(values)
        result.position(0)
        result
    }
    val vertexCount = values.size / valuesPerVertex
    val vertexStride = valuesPerVertex * 4 // 4 bytes per float
}