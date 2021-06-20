package nl.wwbakker.android.app.shapes

import android.opengl.GLES32
import nl.wwbakker.android.app.Shape
import nl.wwbakker.android.app.data.Vertices
import nl.wwbakker.android.app.shaders.VertexAndMultiColorShaders

class Pyramid3d : Shape {

    private val shaders = VertexAndMultiColorShaders()
    // initialize vertex byte buffer for shape coordinates
    val positions = Vertices(arrayOf(
        // front face
         0f, 1f, 0f,
        -1f,-1f, 1f,
         1f,-1f, 1f,
        // right face
         0f, 1f, 0f,
         1f,-1f, 1f,
         1f,-1f,-1f,
        //back face
         0f, 1f, 0f,
         1f,-1f,-1f,
        -1f,-1f,-1f,
        //left face
         0f, 1f, 0f,
        -1f,-1f,-1f,
        -1f,-1f, 1f,
    ).toFloatArray(), 3)

    val colors = Vertices(arrayOf(
        // front face
        1f,0f,0f,1f,
        0f,1f,0f,1f,
        0f,0f,1f,1f,
        //right face
        1f,0f,0f,1f,
        0f,0f,1f,1f,
        0f,1f,0f,1f,
        //back face
        1f,0f,0f,1f,
        0f,1f,0f,1f,
        0f,0f,1f,1f,
        //left face
        1f,0f,0f,1f,
        0f,0f,1f,1f,
        0f,1f,0f,1f
    ).toFloatArray()
        , 4)


    override fun draw(uMvpMatrix: FloatArray) {
        shaders.setColorInput(colors)
        shaders.setModelViewPerspectiveInput(uMvpMatrix)
        shaders.setPositionInput(positions)

        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, positions.vertexCount)
    }


}