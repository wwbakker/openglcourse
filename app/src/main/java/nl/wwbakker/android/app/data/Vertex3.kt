package nl.wwbakker.android.app.data

import kotlin.math.sqrt

data class Vertex3(val x : Float, val y : Float, val z : Float) {
    val floatArray = arrayOf(x, y, z).toFloatArray()

    operator fun unaryMinus() : Vertex3 =
        Vertex3(-x, -y, -z)

    operator fun plus(other : Vertex3) : Vertex3 =
        Vertex3(x + other.x, y + other.y, z + other.z)

    operator fun minus(other : Vertex3) : Vertex3 =
        plus(-other)

    fun normalize() : Vertex3 {
        val magnitude =
            sqrt((x * x) + (y * y) + (z * z))
        return Vertex3(x / magnitude, y / magnitude, z / magnitude)
    }

    fun dot(other: Vertex3) : Float =
        (x * other.x) + (y * other.y) + (z * other.z)

    fun cross(other: Vertex3) : Vertex3 =
        Vertex3(
            x = (y * other.z) - (z * other.y),
            y = (z * other.x) - (x * other.z),
            z = (x * other.y) - (y * other.z)
        )
}

fun List<Vertex3>.toVertices() : Vertices {
    return Vertices(flatMap { listOf(it.x, it.y, it.z) }.toFloatArray(), 3)
}