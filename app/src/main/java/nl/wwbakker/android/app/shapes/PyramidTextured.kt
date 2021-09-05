package nl.wwbakker.android.app.shapes

import android.content.Context
import android.opengl.GLES32
import nl.wwbakker.android.app.R
import nl.wwbakker.android.app.data.*
import nl.wwbakker.android.app.data.textures.Texture
import nl.wwbakker.android.app.shaders.TexturedLightedShaders

object PyramidTextured : Shape {

    private val shaders = TexturedLightedShaders
    private val texture = Texture()

    // initialize vertex byte buffer for shape coordinates
    val positions = Vertices(
        arrayOf(
            // front face
            0f, 1f, 0f,
            -1f, -1f, 1f,
            1f, -1f, 1f,
            // right face
            0f, 1f, 0f,
            1f, -1f, 1f,
            1f, -1f, -1f,
            //back face
            0f, 1f, 0f,
            1f, -1f, -1f,
            -1f, -1f, -1f,
            //left face
            0f, 1f, 0f,
            -1f, -1f, -1f,
            -1f, -1f, 1f,
        ).toFloatArray(), 3
    )

    val textureCoordinates = Vertices(
        arrayOf(
            // front face
            0.5f, 0f,
            0f, 1f,
            1f, 1f,
            // right face
            0.5f, 0f,
            0f, 1f,
            1f, 1f,
            //back face
            0.5f, 0f,
            0f, 1f,
            1f, 1f,
            //left face
            0.5f, 0f,
            0f, 1f,
            1f, 1f,
        ).toFloatArray(), 2
    )

    private val normals = positions.generateNormals()

    override fun load(context: Context) {
        shaders.initiate()
        texture.loadFromResources(context, R.drawable.angkor)
    }


    override fun draw(modelViewProjection: ModelViewProjection) {
        val lightColor = Vertex4(1f, 1f, 1f, 1f)
        val lightLocation = Vertex3(0f, 2f, 0f)

        val mvpMatrix = modelViewProjection.matrix

        shaders.use()
        shaders.setModelViewPerspectiveInput(mvpMatrix)
        shaders.setPositionInput(positions)
        shaders.setTextureCoordinateInput(textureCoordinates)
        shaders.setNormalInput(normals)
        shaders.setDiffuseColor(lightColor)
        shaders.setLightLocationInput(lightLocation)
        shaders.setAmbientColor(Vertex4(0.8f))
        shaders.setSpecularColor(lightColor)
        shaders.setMaterialShininess(10f)
        shaders.setAttenuation(Vertex3(1f, 0.35f, 0.44f))

        shaders.setActiveTexture(texture)


        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, positions.vertexCount)
    }


}