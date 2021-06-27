package nl.wwbakker.android.app.shaders

import android.opengl.GLES32
import nl.wwbakker.android.app.MyRenderer
import nl.wwbakker.android.app.ShaderCompileHelper
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Vertices

class VertexAndMultiColorShaders {
    private val vertexShaderCode =
        """attribute vec3 aVertexPosition;
           attribute vec4 aVertexColor;
           uniform mat4 uMVPMatrix;
           varying vec4 vColor;
           void main() {
               gl_Position = uMVPMatrix *vec4(aVertexPosition,1.0);
               vColor = aVertexColor;
               gl_PointSize = 6.0;
           }""".trimIndent()
    private val fragmentShaderCode =
        """precision mediump float;
           varying vec4 vColor;
           void main() {
                gl_FragColor = vColor;
           }""".trimIndent()

    private val mProgram: Int =
        ShaderCompileHelper.createProgram(vertexShaderCode, fragmentShaderCode)
    private val mPositionHandle: Int
    private val mMVPMatrixHandle: Int
    private val mColorHandle: Int

    init {
        GLES32.glUseProgram(mProgram)  // Add program to OpenGL environment
        mPositionHandle = GLES32.glGetAttribLocation(mProgram, "aVertexPosition")
        // Enable a handle to the vertices
        GLES32.glEnableVertexAttribArray(mPositionHandle)

        mColorHandle = GLES32.glGetAttribLocation(mProgram, "aVertexColor")
        // Enable a handle to the vertices
        GLES32.glEnableVertexAttribArray(mColorHandle)

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix")
        MyRenderer.checkGlError("glGetUniformLocation")
    }

    fun setColorInput(vertices: Vertices) {
        //set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(
            mColorHandle, vertices.valuesPerVertex,
            GLES32.GL_FLOAT, false, vertices.vertexStride, vertices.vertexBuffer
        )
        MyRenderer.checkGlError("glVertexAttribPointer")
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