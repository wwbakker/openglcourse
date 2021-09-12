package nl.wwbakker.android.app.shapes

import android.content.Context
import android.opengl.GLES32
import androidx.annotation.DrawableRes
import nl.wwbakker.android.app.R
import nl.wwbakker.android.app.data.*
import nl.wwbakker.android.app.data.textures.Texture
import nl.wwbakker.android.app.shaders.TexturedShaders
import java.lang.Float.max

object WorldTextured : SphereTextured(textureResource = R.drawable.world)
object ImperialCollegeSkybox : SphereTextured(
    textureResource = R.drawable.imperial_college_360,
    ambientBrightness = 0.6f,
    radius = 10f,
    cameraIsInside = true,
    flattenFloor = true,
)

open class SphereTextured(
    @DrawableRes private val textureResource : Int,
    private val ambientBrightness : Float = 1f,
    cameraIsInside : Boolean = false,
    radius : Float = 2f,
    flattenFloor : Boolean = false
    ) : Shape {

    private val shaders = TexturedShaders
    private val texture = Texture()
    private val latitudeResolution = 32
    private val longitudeResolution = 32
    private val sphereTextureCoords =
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
        texture.loadFromResources(context, textureResource)
    }


    val positions = run {
        val spherePositions = Sphere.spherePositions(latitudeResolution, longitudeResolution, radius)
        if (flattenFloor)
            spherePositions.toVertex3List().map { v -> v.copy(y = max(0f, v.y)) }.toVertices()
        else
            spherePositions
    }

    val indices = Sphere.sphereIndices(latitudeResolution, longitudeResolution, cameraIsInside)//.debugSubArray(16, 0)

    override fun draw(modelViewProjection: ModelViewProjection) {
        shaders.use()
        shaders.setPositionInput(positions)
        shaders.setTextureCoordinateInput(sphereTextureCoords)
        shaders.setAmbientColor(Vertex4(ambientBrightness, ambientBrightness, ambientBrightness, 1f))
        shaders.setModelViewPerspectiveInput(modelViewProjection.matrix)
        shaders.setTexture(texture)
        shaders.setTextureTransparency(false)

        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.length, GLES32.GL_UNSIGNED_INT, indices.indexBuffer)
    }


}