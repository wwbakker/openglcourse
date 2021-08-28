package nl.wwbakker.android.app.shapes.characters

import android.opengl.GLES32
import nl.wwbakker.android.app.shapes.Shape
import nl.wwbakker.android.app.data.Indices
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.ModelViewProjection
import nl.wwbakker.android.app.data.Vertices
import nl.wwbakker.android.app.shaders.VertexAndMultiColorShaders


object CharacterAOld : Shape {

    private val shaders = VertexAndMultiColorShaders
    // initialize vertex byte buffer for shape coordinates
    val positions = Vertices(arrayOf(
        -0.2f,1.0f,-0.3f,
        -0.2f,1.0f,0.3f,
        0.2f,1.0f,-0.3f,
        0.2f,1.0f,0.3f,
        -1.0f,-1.0f,-0.5f,
        -1.0f,-1.0f,0.5f,
        -0.6f,-1.0f,-0.5f,
        -0.6f,-1.0f,0.5f,
        0.6f,-1.0f,0.5f,
        0.6f,-1.0f,-0.5f,
        1.0f,-1.0f,0.5f,
        1.0f,-1.0f,-0.5f,
        0.0f,0.8f,0.3f,
        0.0f,0.8f,-0.3f,
        0.25f,0.1f,0.382f,
        0.25f,0.1f,-0.382f,
        -0.25f,0.1f,0.382f,
        -0.25f,0.1f,-0.382f,
        0.32f,-0.1f,0.41f,
        0.32f,-0.1f,-0.41f,
        -0.32f,-0.1f,0.41f,
        -0.32f,-0.1f,-0.41f
    ).toFloatArray(), 3)

    val indices = Indices(arrayOf(
        0,1,2,2,3,1,
        0,4,5,5,1,0,
        4,5,6,6,7,5,
        1,5,7,7,3,1,
        0,4,6,6,2,0,
        3,10,11,11,3,2,
        8,9,10,10,11,9,
        3,10,8,8,3,1,
        2,11,9,9,2,0,
        12,13,6,6,7,12,
        12,8,9,9,13,12,
        14,15,16,16,17,15,
        19,18,20,20,21,19,
        14,18,20,20,16,14,
        15,19,21,21,17,15,
    ).toIntArray())

    val colors = Vertices(arrayOf(
        0f,0f,1f,1f,//0
        0f,0f,1f,1f,//1
        0f,0f,1f,1f,//2
        0f,0f,1f,1f,//3
        0f,1f,0f,1f,//4
        0f,1f,0f,1f,//5
        0f,1f,0f,1f,//6
        0f,1f,0f,1f,//7
        0f,1f,0f,1f,//8
        0f,1f,0f,1f,//9
        0f,1f,0f,1f,//10
        0f,1f,0f,1f,//11
        0f,0f,1f,1f,//12
        0f,0f,1f,1f,//13
        0f,0f,1f,1f,//14
        0f,0f,1f,1f,//15
        0f,0f,1f,1f,//16
        0f,0f,1f,1f,//17
        0f,1f,0f,1f,//18
        0f,1f,0f,1f,//19
        0f,1f,0f,1f,//20
        0f,1f,0f,1f,//21
    ).toFloatArray()
        , 4)


    override fun draw(modelViewProjection: ModelViewProjection) {
        shaders.setColorInput(colors)
        shaders.setModelViewPerspectiveInput(
            modelViewProjection.copy(modelMatrix = Matrix.scale(0.5f)).matrix)
        shaders.setPositionInput(positions)

        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.length, GLES32.GL_UNSIGNED_INT, indices.indexBuffer)
    }

}