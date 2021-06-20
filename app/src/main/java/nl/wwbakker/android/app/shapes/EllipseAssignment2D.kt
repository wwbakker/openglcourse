package nl.wwbakker.android.app.shapes

import android.opengl.GLES32
import nl.wwbakker.android.app.Shape
import nl.wwbakker.android.app.Vertices
import nl.wwbakker.android.app.shaders.VertexAndSingleColorShaders
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class EllipseAssignment2D : Shape {

    private val vertexAndSingleColorShaders = VertexAndSingleColorShaders()

    private val triangleFirstVertex = listOf(0f,0f, 1.0f)
    val vertices : Vertices = run {
        val radius = 1f
        val quadrant1 = (0..90)
            .map { it.toFloat() }
            .map { it / 180f * PI.toFloat() }
            .map { angle ->
                val x = radius * cos(angle)
                val y = radius * sin(angle) * 1.3f // go from circle to ellipse by stretching by 1.3
                listOf(x, y, 1f) }
        val quadrant2 = quadrant1.flatMap { listOf(-it[0],  it[1], it[2]) }
        val quadrant3 = quadrant1.flatMap { listOf( it[0], -it[1], it[2]) }
        val quadrant4 = quadrant1.flatMap { listOf(-it[0], -it[1], it[2]) }


        Vertices((triangleFirstVertex + quadrant1.flatten() + quadrant2 + quadrant3 + quadrant4).toFloatArray(), 3)
    }


    override fun draw(uMvpMatrix: FloatArray) {
        vertexAndSingleColorShaders.setColorInput(1.0f, 0.0f, 0.0f, 1f)
        vertexAndSingleColorShaders.setModelViewPerspectiveInput(uMvpMatrix)
        vertexAndSingleColorShaders.setPositionInput(vertices)

        // Draw the triangle
        GLES32.glDrawArrays(GLES32.GL_TRIANGLE_FAN, 0, vertices.vertexCount)

        vertexAndSingleColorShaders.setColorInput(0.0f, 1.0f, 0.0f, 1f)
        GLES32.glDrawArrays(GLES32.GL_POINTS, 1, vertices.vertexCount)
    }




}