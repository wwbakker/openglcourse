package nl.wwbakker.android.app.shapes

import android.content.Context
import android.opengl.GLES32
import nl.wwbakker.android.app.R
import nl.wwbakker.android.app.data.*
import nl.wwbakker.android.app.data.textures.Texture
import nl.wwbakker.android.app.shaders.TexturedShaders

object Gorilla : Picture2D(
    textureContainsTransparency = true
)
object Persona : Picture2D(
    textureResource = R.drawable.persona,
    textureContainsTransparency = true,
    transparencyColor = Vertex4(0f, 1f, 0f, 0f)
)

open class Picture2D(
    private val textureResource: Int = R.drawable.gorilla,
    private val textureContainsTransparency : Boolean = false,
    private val transparencyColor : Vertex4 = Vertex4(0f)
) : Shape {
    val shaders = TexturedShaders
    private val texture = Texture()

    private val position = Vertices(
        arrayOf(
            -1f, -1f, 0f, // bottom left
            1f, -1f, 0f, // bottom right
            1f, 1f, 0f, // top right
            -1f, 1f, 0f, // top left
        ).toFloatArray(), 3
    )
    private val indices = Indices(arrayOf(0, 3, 2, 2, 1, 0).toIntArray())
    private val textureCoordinates = Vertices(
        arrayOf(
            1f, 1f,
            0f, 1f,
            0f, 0f,
            1f, 0f,
        ).toFloatArray(), 2
    )

    override fun load(context: Context) {
        texture.loadFromResources(context, textureResource)
    }

    override fun draw(modelViewProjection: ModelViewProjection) {
        shaders.use()
        shaders.setPositionInput(position)
        shaders.setTextureCoordinateInput(textureCoordinates)
        shaders.setAmbientColor(Vertex4(1f, 1f, 1f, 1f))
        shaders.setModelViewPerspectiveInput(modelViewProjection.matrix)
        shaders.setTexture(texture)
        shaders.setTextureTransparency(textureContainsTransparency)
        shaders.setTransparencyColor(transparencyColor)

        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.length, GLES32.GL_UNSIGNED_INT, indices.indexBuffer)
    }
}