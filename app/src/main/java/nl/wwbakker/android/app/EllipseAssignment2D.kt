package nl.wwbakker.android.app

import android.content.res.Resources
import android.opengl.GLES32
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class EllipseAssignment2D {

    private val screenCenterX = Resources.getSystem().displayMetrics.widthPixels / 2.0
    private val screenCenterY = Resources.getSystem().displayMetrics.heightPixels / 2.0

//    // number of coordinates per vertex in this array
    private val COORDS_PER_VERTEX = 3
    private val triangleVertex = floatArrayOf(
        -1.0f, -1.0f, 1.0f,
        1.0f, -1.0f, 1.0f,
        0.0f, 1.0f, 1.0f
    )


    private val vertexShaderCode =
        """attribute vec3 aVertexPosition;
           uniform mat4 uMVPMatrix;
           void main() {
               gl_Position = uMVPMatrix *vec4(aVertexPosition,1.0);
               gl_PointSize = 3.0;
           }""".trimIndent()
    private val fragmentShaderCode =
        """precision mediump float;
           uniform vec4 uColor;
           void main() {
                gl_FragColor = uColor;
           }""".trimIndent()
    private val vertexBuffer: FloatBuffer
    private val mProgram: Int
    private val mPositionHandle: Int
    private val mMVPMatrixHandle: Int
    private val uColorHandle: Int
    private val vertexCount // number of vertices
            : Int
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex


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
        vertexCount = vertices.size / COORDS_PER_VERTEX
        // prepare shaders and OpenGL program
        val vertexShader = MyRenderer.loadShader(GLES32.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = MyRenderer.loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderCode)
        mProgram = GLES32.glCreateProgram() // create empty OpenGL Program
        GLES32.glAttachShader(mProgram, vertexShader) // add the vertex shader to program
        GLES32.glAttachShader(mProgram, fragmentShader) // add the fragment shader to program
        GLES32.glLinkProgram(mProgram) // link the  OpenGL program to create an executable
        GLES32.glUseProgram(mProgram) // Add program to OpenGL environment
        // get handle to vertex shader's vPosition member
        uColorHandle = GLES32.glGetUniformLocation(mProgram, "uColor")
        mPositionHandle = GLES32.glGetAttribLocation(mProgram, "aVertexPosition")
        // Enable a handle to the triangle vertices
        GLES32.glEnableVertexAttribArray(mPositionHandle)
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix")
        MyRenderer.checkGlError("glGetUniformLocation")
    }


    fun draw(mvpMatrix: FloatArray?) {
        GLES32.glUniform4f(uColorHandle, 1.0f, 0.0f, 0.0f, 1f)
        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        MyRenderer.checkGlError("glUniformMatrix4fv")

        //set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(
            mPositionHandle, COORDS_PER_VERTEX,
            GLES32.GL_FLOAT, false, vertexStride, vertexBuffer
        )
        // Draw the triangle
        GLES32.glDrawArrays(GLES32.GL_TRIANGLE_FAN, 0, vertexCount)

        GLES32.glUniform4f(uColorHandle, 0.0f, 1.0f, 0.0f, 1f)

        GLES32.glDrawArrays(GLES32.GL_POINTS, 1, vertexCount)
    }




}