package nl.wwbakker.android.app.data

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Vertices(val values : FloatArray, val valuesPerVertex : Int) {
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

    fun normalize(x : Boolean, y : Boolean, z : Boolean) : Vertices {
        assert(valuesPerVertex == 3) {"Normalize only supported for x,y,z"}
        val vIndices = values.indices step 3
        val xs = normalize(vIndices.map { values[it] }, x)
        val ys = normalize(vIndices.map { values[it + 1] }, y)
        val zs = normalize(vIndices.map { values[it + 2] }, z)
        return Vertices(xs.indices.map { i -> listOf(xs[i], ys[i], zs[i]) }.flatten().toFloatArray(), 3)
    }
    private fun normalize(coordinates : List<Float>, enabled : Boolean) : List<Float> {
        if (!enabled)
            return coordinates
        else {
            val max = coordinates.maxOrNull()!!
            val min = coordinates.minOrNull()!!
            return coordinates.map { 0.5f - (it - min) / (max - min) }
        }
    }

    fun rectangleIndices(offset : Int = 0) : Indices {
        assert(vertexCount % 2 == 0) {"An extra vertex.. what to do with it?"}
        val indices:  List<Int> =
            (0 until vertexCount - 2 step 2)
                .map { i -> listOf(i, i + 1, i + 2, i + 2, i + 3, i + 1) }
                .flatten()
                .map{ it + offset}

        return Indices(indices.toIntArray())
    }

    fun connectTwo2dPlanesIndices() : Indices {
        assert(vertexCount % 2 == 0) {"An extra vertex.. what to do with it?"}
        val otherSideStartIndex = vertexCount / 2
        val indices:  List<Int> =
            (0 until otherSideStartIndex - 2 step 2)
                .map { i -> listOf(
                    i, otherSideStartIndex + i + 2, otherSideStartIndex + i,
                    i, i + 2, otherSideStartIndex + i + 2,
                    i + 1, otherSideStartIndex + i + 1, otherSideStartIndex + i + 3,
                    otherSideStartIndex + i + 3, i + 3, i + 1,
                    ) }
                .flatten()

        val bottomIndices =
            listOf(
                0, otherSideStartIndex + 1, 1, 0,
                otherSideStartIndex, otherSideStartIndex + 1,
            )
        val topIndices =
            bottomIndices.map { it + otherSideStartIndex - 2 }.reversed()
        return Indices((bottomIndices + indices + topIndices).toIntArray())
    }

    fun connect2LinesClosed() : Indices {
        assert(vertexCount % 2 == 0) {"An extra vertex.. what to do with it?"}
        val otherSideStartIndex = vertexCount / 2
        val indices:  List<Int> =
            (0 until otherSideStartIndex - 1)
                .flatMap { i -> listOf(
                    // rectangle 1
                    i,
                    i + otherSideStartIndex,
                    i + 1,

                    // rectangle 2
                    i + 1,
                    i + otherSideStartIndex,
                    i + otherSideStartIndex + 1,
                    ) }

        val finalSquareIndices = listOf(
            otherSideStartIndex - 1, vertexCount - 1, 0,
            0, vertexCount - 1, otherSideStartIndex)

        return Indices((indices + finalSquareIndices ).toIntArray())
    }

    operator fun plus(other : Vertices) : Vertices {
        assert(this.valuesPerVertex == other.valuesPerVertex)
        return Vertices(this.values + other.values, valuesPerVertex)
    }

    fun redGreenBlueColors() : Vertices {
        return Vertices(
        (0 until vertexCount / 3).flatMap { i ->
            listOf(
                1f,0f,0f,1f,
                0f,1f,0f,1f,
                0f,0f,1f,1f,
            )
        }.toFloatArray(), valuesPerVertex = 4)
    }

    fun lineIndices() : Indices =
        Indices((0 until vertexCount)
            .flatMap { listOf(it, it) }
            .drop(1)
            .dropLast(1)
            .toIntArray())

    fun pointIndices() : Indices =
        Indices((0 until vertexCount).toList().toIntArray())

    fun printByIndex(indices: Indices) {
        indices.indexBuffer.array().forEach { index ->
            println("$index: " + (0 until valuesPerVertex)
                .joinToString(", ") { v -> getValueSafe((index * valuesPerVertex) + v) })
        }
    }

    private fun getValueSafe(i : Int) : String {
        return if (i < values.size) values[i].toString() else "OOB!"
    }
}