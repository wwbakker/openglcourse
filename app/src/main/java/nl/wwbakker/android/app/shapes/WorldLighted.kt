package nl.wwbakker.android.app.shapes

import android.content.Context
import android.opengl.GLES32
import nl.wwbakker.android.app.R
import nl.wwbakker.android.app.data.*
import nl.wwbakker.android.app.data.textures.Texture
import nl.wwbakker.android.app.shaders.TexturedLightedShaders
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object WorldLighted : Shape {

    private val shaders = TexturedLightedShaders
    private val texture = Texture()

    fun sphereTextureCoords(latitudeResolution: Int, longitudeResolution: Int) =
        Vertices(
            (0 until latitudeResolution).flatMap { latitude ->
                (0 until longitudeResolution).map { longitude ->
                    listOf(1f - longitude.toFloat() / (longitudeResolution - 1), latitude.toFloat() / latitudeResolution)
                }
            }.flatten().toFloatArray(),
            2
        )


    override fun load(context: Context) {
        shaders.initiate()
        texture.loadFromResources(context, R.drawable.world)
    }

    val latitudeResolution = 32
    val longitudeResolution = 32
    val positions = Sphere.spherePositions(latitudeResolution, longitudeResolution, 2f)
    val textureCoords = sphereTextureCoords(latitudeResolution, longitudeResolution)
    val indices = Sphere.sphereIndices(latitudeResolution, longitudeResolution)//.debugSubArray(16, 0)
    val lightColor = Vertex4(1f,1f,1f,1f)
    val lightLocation = Vertex3(0f,2f,0f)

    override fun draw(modelViewProjection: ModelViewProjection) {

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
        shaders.setModelViewPerspectiveInput(modelViewProjection.matrix)

        shaders.setActiveTexture(texture)

        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.length, GLES32.GL_UNSIGNED_INT, indices.indexBuffer)

    }


}