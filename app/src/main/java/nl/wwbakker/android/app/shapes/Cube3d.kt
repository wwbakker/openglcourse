package nl.wwbakker.android.app.shapes

import android.opengl.GLES32
import nl.wwbakker.android.app.data.Indices
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Vertices
import nl.wwbakker.android.app.shaders.VertexAndMultiColorShaders

class Cube3d : Shape {

    private val shaders = VertexAndMultiColorShaders
    // initialize vertex byte buffer for shape coordinates
    val positions = Vertices(arrayOf(
        //front face
        -1f,-1f, 1f,
         1f,-1f, 1f,
         1f, 1f, 1f,
        -1f, 1f, 1f,

        //back face
        -1f,-1f,-1f,
        -1f, 1f,-1f,
         1f, 1f,-1f,
         1f,-1f,-1f,

        //top face
        -1f, 1f,-1f,
        -1f, 1f, 1f,
         1f, 1f, 1f,
         1f, 1f,-1f,

        //bottom face
        -1f,-1f,-1f,
         1f,-1f,-1f,
         1f,-1f, 1f,
        -1f,-1f, 1f,

        //right face
         1f,-1f,-1f,
         1f, 1f,-1f,
         1f, 1f, 1f,
         1f,-1f, 1f,

        //left face
        -1f,-1f,-1f,
        -1f,-1f, 1f,
        -1f, 1f, 1f,
        -1f, 1f,-1f,
    ).toFloatArray(), 3)

    private val colorValues : FloatArray = listOf(
        listOf(0f,0f,1f,1f),
        listOf(0f,1f,0f,1f),
        listOf(1f,0f,0f,1f),
        listOf(0f,1f,1f,1f),
        listOf(1f,1f,0f,1f),
        listOf(1f,0f,1f,1f)
    ).flatMap { listOf(it,it,it,it) }
        .flatten()
        .toFloatArray()
    val colors = Vertices(colorValues, 4)

    private val indices = Indices(arrayOf(
        0,1,2,0,2,3,
        4,5,6,4,6,7,
        8,9,10,8,10,11,
        12,13,14,12,14,15,
        16,17,18,16,18,19,
        20,21,22,20,22,23).toIntArray())

    override fun draw(projectionMatrix: Matrix, worldMatrix: Matrix) {
        shaders.setColorInput(colors)
        shaders.setModelViewPerspectiveInput(Matrix.simpleModelViewProjectionMatrix(projectionMatrix, worldMatrix = worldMatrix))
        shaders.setPositionInput(positions)

        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.length, GLES32.GL_UNSIGNED_INT, indices.indexBuffer)
    }


}