package nl.wwbakker.android.app.shapes.characters

import android.opengl.GLES32
import nl.wwbakker.android.app.Shape
import nl.wwbakker.android.app.data.*
import nl.wwbakker.android.app.shaders.VertexAndMultiColorShaders

abstract class Character3D : Shape {

    private val shaders = VertexAndMultiColorShaders()

    open fun shouldNormalize() : Boolean { return true }

    abstract fun leftLine() : List<Position2D>

    abstract fun rightLine() : List<Position2D>

    open fun modelMatrix() : Matrix {
        return Matrix.simpleModelMatrix
    }

    fun normalizedBothLines() : List<Position2D> {
        val bothLines = leftLine().zip(rightLine()).flatMap { listOf(it.first, it.second) }
        return if (shouldNormalize()) bothLines.normalize() else bothLines
    }

    fun plane(z : Float) = normalizedBothLines()
        .flatMap { listOf(it.x, it.y, z) }
        .toFloatArray()


    val frontPlane = Vertices(plane(-0.3f), 3)
    val backPlane = Vertices(plane(0.3f), 3)
    val positions = frontPlane + backPlane


    val sPlaneFrontIndices = frontPlane.rectangleIndices()
    val sPlaneBackIndices = backPlane.rectangleIndices(offset = frontPlane.vertexCount)
    val connectingIndices = positions.connectTwo2dPlanesIndices()//.debugSubArray(12, 12)
    val indices = sPlaneFrontIndices + sPlaneBackIndices + connectingIndices

    val colors = Vertices(/**/
        (0 until frontPlane.vertexCount).map { listOf(1f,1f,0f,1f) }.flatten().toFloatArray() +
                (0 until backPlane.vertexCount).map { listOf(0f,0f,1f,1f) }.flatten().toFloatArray()
        , 4)


    override fun draw(projectionMatrix: Matrix) {
        shaders.setColorInput(colors)
        shaders.setModelViewPerspectiveInput(Matrix.simpleModelViewProjectionMatrix(projectionMatrix, modelMatrix()))
        shaders.setPositionInput(positions)

        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.length, GLES32.GL_UNSIGNED_INT, indices.indexBuffer)
    }

}