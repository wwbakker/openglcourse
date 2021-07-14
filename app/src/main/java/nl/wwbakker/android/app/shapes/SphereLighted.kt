package nl.wwbakker.android.app.shapes

import android.opengl.GLES32
import nl.wwbakker.android.app.data.*
import nl.wwbakker.android.app.shaders.DirectionalLightShaders
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

object SphereLighted : Shape {

    private val shaders = DirectionalLightShaders

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
                    listOf(p0, p1, p0 + 1, p1, (p1 + 1) % (latitudeResolution * longitudeResolution), p0 + 1)
                }
            }.flatten().toIntArray()
        )




    override fun draw(projectionMatrix: Matrix, worldMatrix: Matrix) {
        val latitudeResolution = 8
        val longitudeResolution = 8
        val positions = spherePositions(latitudeResolution, longitudeResolution, 1f)

        val indices = sphereIndices(latitudeResolution, longitudeResolution)//.debugSubArray(16, 0)

        shaders.use()
        shaders.setPositionInput(positions)
        shaders.setColorInput(positions.singleColor(0.3f,0f,0f))
        shaders.setNormalInput(positions)
        shaders.setDiffuseColor(Vertex4(1f,1f,1f,1f))
        shaders.setDiffuseLightLocationInput(Vertex3(3f,2f,2f))
        shaders.setAttenuation(Vertex3(1f, 0.35f, 0.44f))
        shaders.setModelViewPerspectiveInput(
            Matrix.simpleModelViewProjectionMatrix(projectionMatrix, worldMatrix = worldMatrix))


        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.length, GLES32.GL_UNSIGNED_INT, indices.indexBuffer)

    }


}