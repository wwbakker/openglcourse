package nl.wwbakker.android.app.shaders

import android.opengl.GLES32
import nl.wwbakker.android.app.MyRenderer
import nl.wwbakker.android.app.ShaderCompileHelper
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Vertex
import nl.wwbakker.android.app.data.Vertices
import kotlin.properties.Delegates

object DirectionalLightShaders {


    private val vertexShaderCode =
        """attribute vec3 aVertexPosition;
           attribute vec4 aVertexColor;
           attribute vec3 aVertexNormal;
           uniform mat4 uMVPMatrix;
           varying vec4 vColor;
           uniform vec3 uDiffuseLightLocation;
           uniform vec4 uDiffuseColor;
           varying vec4 vDiffuseColor;
           uniform float uDiffuseLightAttenuation;
           varying float vDiffuseLightWeighting;
           void main() {
              gl_Position = uMVPMatrix *vec4(aVertexPosition,1.0);
              vec3 diffuseLightDirection = normalize(uDiffuseLightLocation-gl_Position.xyz);
              vec3 transformedNormal = normalize((uMVPMatrix * vec4(aVertexNormal, 0.0)).xyz);
              vDiffuseColor = uDiffuseColor;
              vec3 vertexToLightSource = uDiffuseLightLocation-gl_Position.xyz;
              float diffuseLightDistance = length(vertexToLightSource);
              float attenuation = 1.0 / ( uAttenuation.x + uAttenuation.y * diffuseLightDistance +
                        uAttenuation.z * diffuseLightDistance * diffuseLightDistance);
              vColor=aVertexColor;
           }""".trimIndent()
    private val fragmentShaderCode =
        """precision mediump float;
           varying vec4 vColor;
           varying vec4 vDiffuseColor;
           varying float vDiffuseLightWeighting;
           void main() {
                vec4 diffuseColor = vDiffuseCLightWeighting * vDiffuseColor;
                gl_FragColor = vColor+diffuseColor;
           }""".trimIndent()

    private var mProgram by Delegates.notNull<Int>()
    private var mPositionHandle by Delegates.notNull<Int>()
    private var mMVPMatrixHandle by Delegates.notNull<Int>()
    private var mColorHandle by Delegates.notNull<Int>()
    private var mDiffuseLightLocationHandle by Delegates.notNull<Int>()
    private var mDiffuseLightIntensityHandle by Delegates.notNull<Int>()

    fun initiate() {
        mProgram = ShaderCompileHelper.createProgram(vertexShaderCode, fragmentShaderCode)

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

        mDiffuseLightLocationHandle = GLES32.glGetUniformLocation(mProgram, "uDiffuseLightLocation")
        MyRenderer.checkGlError("glGetUniformLocation")

        mDiffuseLightIntensityHandle = GLES32.glGetUniformLocation(mProgram, "uDiffuseLightIntensity")
        MyRenderer.checkGlError("glGetUniformLocation")

    }

    fun setColorInput(vertices: Vertices) {
        //set the attribute of the vertex to Diffuse to the vertex buffer
        GLES32.glVertexAttribPointer(
            mColorHandle, vertices.valuesPerVertex,
            GLES32.GL_FLOAT, false, vertices.vertexStride, vertices.vertexBuffer
        )
        MyRenderer.checkGlError("glVertexAttribDiffuseer")
    }
    fun setModelViewPerspectiveInput(mvpMatrix : Matrix) {
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix.values, 0)
        MyRenderer.checkGlError("glUniformMatrix4fv")
    }

    fun setPositionInput(vertices: Vertices) {
        //set the attribute of the vertex to Diffuse to the vertex buffer
        GLES32.glVertexAttribPointer(
            mPositionHandle, vertices.valuesPerVertex,
            GLES32.GL_FLOAT, false, vertices.vertexStride, vertices.vertexBuffer
        )
        MyRenderer.checkGlError("glVertexAttribDiffuseer")
    }

    fun setDiffuseLightPosition(position: Vertex) {
        //set the attribute of the vertex to Diffuse to the vertex buffer
        GLES32.glUniform3fv(mDiffuseLightLocationHandle, 1, position.floatArray, 0)
        MyRenderer.checkGlError("glUniform3fv")
    }

    fun setDiffuseLightIntensity(intensity: Float) {
        GLES32.glUniform1f(mDiffuseLightIntensityHandle, intensity)
        MyRenderer.checkGlError("glUniform1f")
    }

}