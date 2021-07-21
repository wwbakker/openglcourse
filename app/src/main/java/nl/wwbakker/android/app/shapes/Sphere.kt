package nl.wwbakker.android.app.shapes

import android.opengl.GLES32
import nl.wwbakker.android.app.data.Indices
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Vertices
import nl.wwbakker.android.app.shaders.VertexAndMultiColorShaders
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

object Sphere : Shape {

    private val shaders = VertexAndMultiColorShaders

    fun spherePositions(latitudeResolution : Int, longitudeResolution : Int, radius : Float) =
        Vertices(values =
        (0 until latitudeResolution).flatMap { latitude ->
            val theta = latitude * PI / (latitudeResolution - 1)
            val sinTheta = sin(theta)
            val cosTheta = cos(theta)
            (0 until longitudeResolution).map { longitude ->
                val phi = longitude * 2 * PI / longitudeResolution
                val sinPhi = sin(phi)
                val cosPhi = cos(phi)
                val x = cosPhi * sinTheta
                val y = cosTheta
                val z = sinPhi * sinTheta
                listOf((radius * x).toFloat(), (radius * y).toFloat(), (radius * z).toFloat())
            }
        }.flatten().toFloatArray(), valuesPerVertex = 3)

    fun sphereIndices(latitudeResolution : Int, longitudeResolution : Int) =
        Indices(values =
            (0 until latitudeResolution).flatMap { latitude ->
                (0 until longitudeResolution - 1).map { longitude ->
                    val p0 = ((longitude * latitudeResolution) + latitude) % (longitudeResolution * latitudeResolution)
                    val p1 = p0 + latitudeResolution
                    listOf(p0, p0 + 1, p1, p1, p0 + 1, (p1 + 1) % (latitudeResolution * longitudeResolution))
                }
            }.flatten().toIntArray()
        )

    fun sphereColors(latitudeResolution : Int, longitudeResolution : Int) =
        Vertices(values =
        (0 until latitudeResolution).flatMap { latitude ->
            val colorBase = -0.5f
            val colorInc = 1f / (longitudeResolution)
            (0 until longitudeResolution).map { longitude ->
                listOf(1f, abs(colorBase + (longitude * colorInc)), 1f, 1f)
            }
        }.flatten().toFloatArray(), valuesPerVertex = 4)


    override fun draw(projectionMatrix: Matrix, worldMatrix: Matrix) {
        val latitudeResolution = 8
        val longitudeResolution = 8
        val positions = spherePositions(latitudeResolution, longitudeResolution, 1f)
        val colors = sphereColors(latitudeResolution, longitudeResolution)

        val indices = sphereIndices(latitudeResolution, longitudeResolution)//.debugSubArray(60, 0)

//        positions.printByIndex(indices)

        shaders.use()
        shaders.setPositionInput(positions)
        shaders.setColorInput(colors)
        shaders.setModelViewPerspectiveInput(
            Matrix.simpleModelViewProjectionMatrix(projectionMatrix, worldMatrix = worldMatrix))


        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.length, GLES32.GL_UNSIGNED_INT, indices.indexBuffer)
    }


}