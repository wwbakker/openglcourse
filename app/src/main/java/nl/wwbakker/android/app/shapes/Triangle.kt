package nl.wwbakker.android.app.shapes

import android.opengl.GLES32
import nl.wwbakker.android.app.MyRenderer
import nl.wwbakker.android.app.Shape
import nl.wwbakker.android.app.data.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


class Triangle : Shape {
    private val vertexShaderCode =
        """attribute vec3 aVertexPosition;
           uniform mat4 uMVPMatrix;
           varying vec4 vColor;
           void main() {
               gl_Position = uMVPMatrix *vec4(aVertexPosition,1.0);
               gl_PointSize = 40.0;
               vColor=vec4(1.0,0.0,0.0,1.0);
           }""".trimIndent()
    private val fragmentShaderCode =
        """precision mediump float;
           varying vec4 vColor;
           void main() {
                gl_FragColor = vColor;
           }""".trimIndent()
    private val vertexBuffer: FloatBuffer
    private val mProgram: Int
    private val mPositionHandle: Int
    private val mMVPMatrixHandle: Int
    private val vertexCount // number of vertices
            : Int
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    init {
        // initialize vertex byte buffer for shape coordinates
        val bb =
            ByteBuffer.allocateDirect(triangleVertex.size * 4) // (# of coordinate values * 4 bytes per float)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(triangleVertex)
        vertexBuffer.position(0)
        vertexCount = triangleVertex.size / COORDS_PER_VERTEX
        // prepare shaders and OpenGL program
        val vertexShader = MyRenderer.loadShader(GLES32.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = MyRenderer.loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderCode)
        mProgram = GLES32.glCreateProgram() // create empty OpenGL Program
        GLES32.glAttachShader(mProgram, vertexShader) // add the vertex shader to program
        GLES32.glAttachShader(mProgram, fragmentShader) // add the fragment shader to program
        GLES32.glLinkProgram(mProgram) // link the  OpenGL program to create an executable
        GLES32.glUseProgram(mProgram) // Add program to OpenGL environment
        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES32.glGetAttribLocation(mProgram, "aVertexPosition")
        // Enable a handle to the triangle vertices
        GLES32.glEnableVertexAttribArray(mPositionHandle)
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix")
        MyRenderer.checkGlError("glGetUniformLocation")
    }


    override fun draw(projectionMatrix: Matrix, worldMatrix: Matrix) {
        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, Matrix.simpleModelViewProjectionMatrix(projectionMatrix, worldMatrix = worldMatrix).values, 0)
        MyRenderer.checkGlError("glUniformMatrix4fv")
        //set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(
            mPositionHandle, COORDS_PER_VERTEX,
            GLES32.GL_FLOAT, false, vertexStride, vertexBuffer
        )
        // Draw the triangle
        GLES32.glDrawArrays(GLES32.GL_LINE_LOOP, 0, vertexCount)
    }

    companion object {
        // number of coordinates per vertex in this array
        const val COORDS_PER_VERTEX = 3
        var triangleVertex = floatArrayOf(
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            0.0f, 1.0f, 1.0f
        )
    }


}