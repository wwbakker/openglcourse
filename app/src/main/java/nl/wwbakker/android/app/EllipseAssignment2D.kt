package nl.wwbakker.android.app

import android.opengl.GLES32
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class EllipseAssignment2D {
    private val vertexBuffer: FloatBuffer
    private val vertexCount // number of vertices
            : Int

    private val simpleVertexAndColorShaders : SimpleVertexAndColorShaders

    private val triangleFirstVertex = listOf(0f,0f, 1.0f)
    val vertices : FloatArray = run {
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


        (triangleFirstVertex + quadrant1.flatten() + quadrant2 + quadrant3 + quadrant4).toFloatArray()
    }

    init {
        // initialize vertex byte buffer for shape coordinates
        val bb =
            ByteBuffer.allocateDirect(vertices.size * 4) // (# of coordinate values * 4 bytes per float)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)
        vertexCount = vertices.size / SimpleVertexAndColorShaders.COORDS_PER_VERTEX
        // prepare shaders and OpenGL program
        simpleVertexAndColorShaders = SimpleVertexAndColorShaders()

    }


    fun draw(mvpMatrix: FloatArray) {
        simpleVertexAndColorShaders.setColorInput(1.0f, 0.0f, 0.0f, 1f)
        simpleVertexAndColorShaders.setMatrixInput(mvpMatrix)
        simpleVertexAndColorShaders.setVertexInput(vertexBuffer)

        // Draw the triangle
        GLES32.glDrawArrays(GLES32.GL_TRIANGLE_FAN, 0, vertexCount)

        simpleVertexAndColorShaders.setColorInput(0.0f, 1.0f, 0.0f, 1f)
        GLES32.glDrawArrays(GLES32.GL_POINTS, 1, vertexCount)
    }




}