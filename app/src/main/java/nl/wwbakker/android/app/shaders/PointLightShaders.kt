package nl.wwbakker.android.app.shaders

import android.opengl.GLES32
import nl.wwbakker.android.app.ShaderCompileHelper
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Vertex
import nl.wwbakker.android.app.data.Vertices


object PointLightShaders {
    private val aVertexPosition = ShaderVariable(Qualifier.Attribute, "vec3", "aVertexPosition")
    private val aVertexColor = ShaderVariable(Qualifier.Attribute, "vec4", "aVertexColor")
    private val uMvpMatrix = ShaderVariable(Qualifier.Uniform, "mat4", "uMVPMatrix")
    private val vColor = ShaderVariable(Qualifier.Varying, "vec4", "vColor")
    private val uPointLightIntensity = ShaderVariable(Qualifier.Uniform, "float", "uPointLightIntensity")
    private val uPointLightLocation = ShaderVariable(Qualifier.Uniform, "vec3", "uPointLightLocation")
    private val vPointLightWeighting = ShaderVariable(Qualifier.Varying, "float", "vPointLightWeighting")
    private val variables = listOf(
        aVertexPosition,
        aVertexColor,
        uMvpMatrix,
        vColor,
        uPointLightIntensity,
        uPointLightLocation,
        vPointLightWeighting,
    )

    private val vertexShaderCode =
        """${variables.vertexShaderDefinitions()}
           void main() {
               gl_Position = uMVPMatrix *vec4(aVertexPosition,1.0);
               float distanceFromLight = distance(uPointLightLocation, gl_Position.xyz);
               vPointLightWeighting = uPointLightIntensity/(distanceFromLight*distanceFromLight);
               vColor = aVertexColor;
               gl_PointSize = 6.0;
           }""".trimIndent()
    private val fragmentShaderCode =
        """precision mediump float;
           ${variables.pixelShaderDefinitions()}
           void main() {
                gl_FragColor = vec4(vColor.xyz*vPointLightWeighting,1);
           }""".trimIndent()

    fun initiate() {
        val mProgram = ShaderCompileHelper.createProgram(vertexShaderCode, fragmentShaderCode)

        GLES32.glUseProgram(mProgram)  // Add program to OpenGL environment
        variables.forEach{it.initiate(mProgram)}
    }

    fun setColorInput(vertices: Vertices) {
        //set the attribute of the vertex to point to the vertex buffer
        aVertexColor.setValue(vertices)
    }
    fun setModelViewPerspectiveInput(mvpMatrix : Matrix) {
        uMvpMatrix.setValue(mvpMatrix)
    }

    fun setPositionInput(vertices: Vertices) {
        aVertexPosition.setValue(vertices)
    }

    fun setPointLightPosition(position: Vertex) {
        uPointLightLocation.setValue(position)
    }

    fun setPointLightIntensity(intensity: Float) {
        uPointLightIntensity.setValue(intensity)
    }

}