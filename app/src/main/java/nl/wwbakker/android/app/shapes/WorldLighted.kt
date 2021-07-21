package nl.wwbakker.android.app.shapes

import android.content.Context
import android.opengl.GLES32
import nl.wwbakker.android.app.R
import nl.wwbakker.android.app.data.*
import nl.wwbakker.android.app.shaders.TexturedLightedShaders
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object WorldLighted : Shape {

    private val shaders = TexturedLightedShaders

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


    fun sphereTextureCoords(latitudeResolution: Int, longitudeResolution: Int) =
        Vertices(
            (0 until latitudeResolution).flatMap { latitude ->
                (0 until longitudeResolution).map { longitude ->
                    listOf(1f - longitude.toFloat() / longitudeResolution, latitude.toFloat() / latitudeResolution)
                }
            }.flatten().toFloatArray(),
            2
        )


    override fun load(context: Context) {
        shaders.loadTextureFromResourcesOnce(context, R.drawable.world_smaller)
    }

    override fun draw(projectionMatrix: Matrix, worldMatrix: Matrix) {
        val latitudeResolution = 128
        val longitudeResolution = 128
        val positions = spherePositions(latitudeResolution, longitudeResolution, 2f)
        val textureCoords = sphereTextureCoords(latitudeResolution, longitudeResolution)
        val indices = sphereIndices(latitudeResolution, longitudeResolution)//.debugSubArray(16, 0)
        val lightColor = Vertex4(1f,1f,1f,1f)
        val lightLocation = Vertex3(0f,2f,0f)

        shaders.use()
        shaders.setPositionInput(positions)
        shaders.setTextureCoordinateInput(textureCoords)
        shaders.setNormalInput(positions)
        shaders.setDiffuseColor(lightColor)
        shaders.setLightLocationInput(lightLocation)
        shaders.setAmbientColor(Vertex4(0.5f,0.5f,0.5f, 1f))
        shaders.setSpecularColor(lightColor)
        shaders.setMaterialShininess(10f)
        shaders.setAttenuation(Vertex3(1f, 0.35f, 0.44f))
        shaders.setModelViewPerspectiveInput(
            Matrix.simpleModelViewProjectionMatrix(projectionMatrix, worldMatrix = worldMatrix))

        shaders.setActiveTexture()

        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.length, GLES32.GL_UNSIGNED_INT, indices.indexBuffer)

    }


}