package nl.wwbakker.android.app.shapes

import android.opengl.GLES32
import nl.wwbakker.android.app.data.Indices
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Vertices
import nl.wwbakker.android.app.shaders.VertexAndMultiColorShaders

class PentagonPrism : Shape {

    private val shaders = VertexAndMultiColorShaders

    val points = listOf(
        listOf(0f,1f,1f),
        listOf(-0.95f,0.31f,1f),
        listOf(-0.59f,-0.81f,1f),
        listOf(0.59f,-0.81f,1f),
        listOf(0.95f,0.31f,1f),
        listOf(0f,1f,-1f),
        listOf(0.95f,0.31f,-1f),
        listOf(0.59f,-0.81f,-1f),
        listOf(-0.59f,-0.81f,-1f),
        listOf(-0.95f,0.31f,-1f),
    )

    val faces = listOf(
        // front face
        points[0],
        points[1],
        points[2],
        points[3],
        points[4],
        // back face
        points[5],
        points[6],
        points[7],
        points[8],
        points[9],
        // top left
        points[5],
        points[9],
        points[1],
        points[0],
        // bottom left
        points[9],
        points[8],
        points[2],
        points[1],
        // bottom
        points[2],
        points[8],
        points[7],
        points[3],
        // bottom right
        points[4],
        points[3],
        points[7],
        points[6],
        // top right
        points[0],
        points[4],
        points[6],
        points[5],
    )

    // initialize vertex byte buffer for shape coordinates
    val positions = Vertices(faces.flatten().toFloatArray(), 3)

    private val colorValues : FloatArray = listOf(
        listOf(0f,0f,1f,1f), //blue
        listOf(1f,0f,0f,1f), //red
        listOf(1f,1f,1f,1f), //white
        listOf(1f,0f,1f,1f), //magenta
        listOf(0f,1f,0f,1f), //green
        listOf(1f,1f,0f,1f), //yellow
        listOf(0f,1f,1f,1f), //cyan

    ).withIndex()
        .flatMap {
            val v = it.value
            if (it.index == 0 || it.index == 1) // front and back face
                (1..5).map { v }
            else
                (1..4).map { v }
        }
        .flatten()
        .toFloatArray()

    val colors = Vertices(colorValues, 4)

    private val indices = Indices(arrayOf(
        0,1,2,0,2,3,0,3,4,
        5,6,7,5,7,8,5,8,9,
        10,11,12,12,13,10,
        14,15,16,16,17,14,
        18,19,20,20,21,18,
        22,23,24,24,25,22,
        26,27,28,28,29,26,
        ).toIntArray())

    override fun draw(projectionMatrix: Matrix, worldMatrix: Matrix) {
        shaders.setColorInput(colors)
        shaders.setModelViewPerspectiveInput(
            Matrix.simpleModelViewProjectionMatrix(projectionMatrix, worldMatrix = worldMatrix))
        shaders.setPositionInput(positions)

        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.length, GLES32.GL_UNSIGNED_INT, indices.indexBuffer)
    }

}