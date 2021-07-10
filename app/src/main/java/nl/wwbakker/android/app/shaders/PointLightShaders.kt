package nl.wwbakker.android.app.shaders

import android.opengl.GLES32
import nl.wwbakker.android.app.ShaderCompileHelper
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Vertex3
import nl.wwbakker.android.app.data.Vertices
import nl.wwbakker.android.app.shaders.Qualifier.*
import kotlin.properties.Delegates

object PointLightShaders {
    private var programHandle by Delegates.notNull<Int>()
    private val aVertexPosition = ShaderVariable(Attribute, "vec3", "aVertexPosition")
    private val aVertexColor = ShaderVariable(Attribute, "vec4", "aVertexColor")
    private val uMvpMatrix = ShaderVariable(Uniform, "mat4", "uMVPMatrix")
    private val vColor = ShaderVariable(Varying, "vec4", "vColor")
    private val uPointLightIntensity = ShaderVariable(Uniform, "float", "uPointLightIntensity")
    private val uPointLightLocation = ShaderVariable(Uniform, "vec3", "uPointLightLocation")
    private val vPointLightWeighting = ShaderVariable(Varying, "float", "vPointLightWeighting")
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
        programHandle = ShaderCompileHelper.createProgram(vertexShaderCode, fragmentShaderCode)

        use()  // Add program to OpenGL environment
        variables.forEach{it.initiate(programHandle)}
    }

    fun use() {
        GLES32.glUseProgram(programHandle)
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

    fun setPointLightPosition(position: Vertex3) {
        uPointLightLocation.setValue(position)
    }

    fun setPointLightIntensity(intensity: Float) {
        uPointLightIntensity.setValue(intensity)
    }

}