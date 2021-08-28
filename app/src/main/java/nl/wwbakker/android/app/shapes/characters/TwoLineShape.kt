package nl.wwbakker.android.app.shapes.characters

import android.opengl.GLES32
import nl.wwbakker.android.app.data.*
import nl.wwbakker.android.app.shaders.VertexAndMultiColorShaders
import nl.wwbakker.android.app.shapes.ShapeWithWidth

abstract class TwoLineShape : ShapeWithWidth {

    private val shaders = VertexAndMultiColorShaders

    open fun shouldNormalize() : Boolean { return false }

    open fun shouldCenter() : Boolean { return false }

    abstract fun leftLine() : List<Position2D>

    abstract fun rightLine() : List<Position2D>

    open fun modelMatrix() : Matrix {
        return Matrix.identity()
    }

    private fun normalizedAndCenteredBothLines() : List<Position2D> =
        leftLine()
            .zip(rightLine())
            .flatMap { listOf(it.first, it.second) }
            .mapIf( shouldNormalize()) { it.normalize() }
            .mapIf( shouldCenter()) { it.center() }


    private fun <T> List<T>.mapIf(condition: Boolean, transform: (List<T>) -> List<T>): List<T> =
        if (condition) transform(this) else this


    fun plane(z : Float) = normalizedAndCenteredBothLines()
        .flatMap { listOf(it.x, it.y, z) }
        .toFloatArray()


    val frontPlane = Vertices(plane(0.2f), 3)
    val backPlane = Vertices(plane(-0.2f), 3)
    val positions = frontPlane + backPlane


    val sPlaneFrontIndices = frontPlane.rectangleIndices()
    val sPlaneBackIndices = backPlane.rectangleIndices(offset = frontPlane.vertexCount)
    val connectingIndices = positions.connectTwo2dPlanesIndices()//.debugSubArray(12, 12)
    val indices = sPlaneFrontIndices + sPlaneBackIndices + connectingIndices

    val colors = Vertices(/**/
        (0 until frontPlane.vertexCount).map { listOf(1f,1f,1f,1f) }.flatten().toFloatArray() +
                (0 until backPlane.vertexCount).map { listOf(0.4f,0.4f,0.4f,1f) }.flatten().toFloatArray()
        , 4)

    override fun width() : Float {
        val xPoints = normalizedAndCenteredBothLines().map { it.x }
        return (xPoints.maxByOrNull { it } ?: 0f) - (xPoints.minByOrNull { it } ?: 0f)
    }

    override fun draw(modelViewProjection: ModelViewProjection) {
        shaders.use()
        shaders.setColorInput(colors)
        shaders.setModelViewPerspectiveInput(modelViewProjection.copy(modelMatrix =
        Matrix.multiply(
            modelMatrix(),
            modelViewProjection.modelMatrix,
        )).matrix)
        shaders.setPositionInput(positions)

        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.length, GLES32.GL_UNSIGNED_INT, indices.indexBuffer)
    }

}