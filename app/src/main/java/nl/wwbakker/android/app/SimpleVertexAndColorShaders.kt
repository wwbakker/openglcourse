package nl.wwbakker.android.app

import android.opengl.GLES32
import java.nio.FloatBuffer

class SimpleVertexAndColorShaders {
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

    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    private val mProgram: Int =
        ShaderCompileHelper.createProgram(vertexShaderCode, fragmentShaderCode)
    private val mPositionHandle: Int
    private val mMVPMatrixHandle: Int
    private val uColorHandle: Int

    init {
        GLES32.glUseProgram(mProgram)  // Add program to OpenGL environment
        uColorHandle = GLES32.glGetUniformLocation(mProgram, "uColor")
        mPositionHandle = GLES32.glGetAttribLocation(mProgram, "aVertexPosition")
        // Enable a handle to the vertices
        GLES32.glEnableVertexAttribArray(mPositionHandle)
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix")
        MyRenderer.checkGlError("glGetUniformLocation")
    }

    fun setColorInput(r : Float, g : Float, b: Float, a : Float) {
        GLES32.glUniform4f(uColorHandle, r, g, b, a)
        MyRenderer.checkGlError("glUniform4f")

    }
    fun setMatrixInput(mvpMatrix : FloatArray) {
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        MyRenderer.checkGlError("glUniformMatrix4fv")
    }

    fun setVertexInput(vertexBuffer : FloatBuffer) {
        //set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(
            mPositionHandle, COORDS_PER_VERTEX,
            GLES32.GL_FLOAT, false, vertexStride, vertexBuffer
        )
        MyRenderer.checkGlError("glVertexAttribPointer")
    }

    companion object {
        val COORDS_PER_VERTEX = 3
    }
}