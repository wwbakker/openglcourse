package nl.wwbakker.android.app.shaders

import android.opengl.GLES32
import nl.wwbakker.android.app.MyRenderer
import nl.wwbakker.android.app.ShaderCompileHelper
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Vertices

class VertexAndSingleColorShaders {
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
    fun setModelViewPerspectiveInput(mvpMatrix : Matrix) {
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix.values, 0)
        MyRenderer.checkGlError("glUniformMatrix4fv")
    }

    fun setPositionInput(vertices: Vertices) {
        //set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(
            mPositionHandle, vertices.valuesPerVertex,
            GLES32.GL_FLOAT, false, vertices.vertexStride, vertices.vertexBuffer
        )
        MyRenderer.checkGlError("glVertexAttribPointer")
    }

}