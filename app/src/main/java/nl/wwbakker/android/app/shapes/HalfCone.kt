package nl.wwbakker.android.app.shapes

import android.opengl.GLES32
import nl.wwbakker.android.app.data.*
import nl.wwbakker.android.app.shaders.VertexAndMultiColorShaders
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class HalfCone : Shape {

    private val shaders = VertexAndMultiColorShaders

    private fun circlePositions(resolution : Int, radius : Float, z : Float) : Vertices {
        val thetaStep : Float = ((2 * PI) / resolution).toFloat()
        val positionCoordinates = (0 until resolution).flatMap { i ->
            val theta = i * thetaStep
            listOf(radius * cos(theta), radius * sin(theta), z)
        }
        return Vertices(positionCoordinates.toFloatArray(), 3)

    }

    val upperCirclePositions = circlePositions(30, 1f, 0.5f)
    val lowerCirclePositions = circlePositions(30, 0.8f, -0.5f)

    val positions =  lowerCirclePositions + upperCirclePositions
    val indices = positions.connect2LinesClosed()//.debugSubArray(12, 0)
    val colors = Vertices(/**/
        (0 until lowerCirclePositions.vertexCount).map { listOf(1f,0f,0f,1f) }.flatten().toFloatArray() +
                (0 until upperCirclePositions.vertexCount).map { listOf(1f,0f,1f,1f) }.flatten().toFloatArray()
        , 4)


    val modelMatrix =
        Matrix.translate(z = -4f)
            .multiply(Matrix.rotate(30f, y = 1f))
            .multiply(Matrix.rotate(30f, x = 1f))

    override fun draw(modelViewProjection: ModelViewProjection) {
        positions.printByIndex(indices)

        shaders.setColorInput(colors)
        shaders.setModelViewPerspectiveInput(modelViewProjection.copy(modelMatrix = modelMatrix).matrix)
        shaders.setPositionInput(positions)

        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.length, GLES32.GL_UNSIGNED_INT, indices.indexBuffer)
    }

}