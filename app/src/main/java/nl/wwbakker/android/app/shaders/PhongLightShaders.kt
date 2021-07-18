package nl.wwbakker.android.app.shaders

import android.opengl.GLES32
import nl.wwbakker.android.app.ShaderCompileHelper
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Vertex3
import nl.wwbakker.android.app.data.Vertex4
import nl.wwbakker.android.app.data.Vertices
import nl.wwbakker.android.app.shaders.Qualifier.*
import kotlin.properties.Delegates

object PhongLightShaders {
    private var programHandle by Delegates.notNull<Int>()
    private val aVertexPosition = ShaderVariable(Attribute, "vec3", "aVertexPosition")
    private val aVertexColor = ShaderVariable(Attribute, "vec4", "aVertexColor")
    private val aVertexNormal = ShaderVariable(Attribute, "vec3", "aVertexNormal")
    private val uMvpMatrix = ShaderVariable(UniformVertexShader, "mat4", "uMVPMatrix")
    private val vColor = ShaderVariable(Varying, "vec4", "vColor")
    private val uLightLocation = ShaderVariable(UniformVertexShader, "vec3", "uLightLocation")
    private val uDiffuseColor = ShaderVariable(UniformFragmentShader, "vec4", "uDiffuseColor")
    private val vDiffuseLightWeight = ShaderVariable(Varying, "float", "vDiffuseLightWeight")
    private val uAttenuation = ShaderVariable(UniformVertexShader, "vec3", "uAttenuation")
    private val uAmbientColor = ShaderVariable(UniformFragmentShader, "vec4", "uAmbientColor")
    private val uSpecularColor = ShaderVariable(UniformFragmentShader, "vec4", "uSpecularColor")
    private val vSpecularLightWeight = ShaderVariable(Varying, "float", "vSpecularLightWeight")
    private val uMaterialShininess = ShaderVariable(UniformVertexShader, "float", "uMaterialShininess")

    private val variables = listOf(
        aVertexPosition,
        aVertexColor,
        aVertexNormal,
        uMvpMatrix,
        vColor,
        uLightLocation,
        uDiffuseColor,
        vDiffuseLightWeight,
        uAttenuation,
        uAmbientColor,
        uSpecularColor,
        vSpecularLightWeight,
        uMaterialShininess,
    )

    private val vertexShaderCode =
        """${variables.vertexShaderDefinitions()}
            void calculateDiffuseAndSpecularLightWeights();
            void calculateDiffuseAndSpecularLightWeights() {
                vec3 lightDirection = normalize(uLightLocation-gl_Position.xyz);
                vec3 transformedNormal = normalize((uMVPMatrix * vec4(aVertexNormal, 0.0)).xyz);
                vec3 vertexToLightSource = uLightLocation-gl_Position.xyz;
                float lightDistance = length(vertexToLightSource);
                float attenuation = 1.0 / (uAttenuation.x + uAttenuation.y * lightDistance 
                    + uAttenuation.z * lightDistance * lightDistance);
                
                vDiffuseLightWeight = attenuation * max(dot(transformedNormal,lightDirection),0.0);
                
                vec3 viewDirection = normalize(-gl_Position.xyz);
                vec3 reflectionDirection=reflect(-lightDirection, transformedNormal);
                vSpecularLightWeight = attenuation*
                    pow(max(dot(reflectionDirection,viewDirection),0.0),uMaterialShininess);
            }
            
            void main() {
              gl_Position = uMVPMatrix *vec4(aVertexPosition,1.0);
              calculateDiffuseAndSpecularLightWeights();
              vColor=aVertexColor;
           }""".trimIndent()
    private val fragmentShaderCode =
        """precision mediump float;
           ${variables.fragmentShaderDefinitions()}
           void main() {
                vec4 diffuseColor = vDiffuseLightWeight * uDiffuseColor;
                vec4 specularColor = vSpecularLightWeight * uSpecularColor;
                gl_FragColor = vColor*uAmbientColor+specularColor+diffuseColor;
           }""".trimIndent()


    fun initiate() {
        programHandle = ShaderCompileHelper.createProgram(vertexShaderCode, fragmentShaderCode)

        use()  // Add program to OpenGL environment
        variables.forEach{it.initiate(programHandle)}
    }

    fun use() {
        GLES32.glUseProgram(programHandle)
    }

    fun setPositionInput(vertices: Vertices) {
        aVertexPosition.setValue(vertices)
    }

    fun setColorInput(vertices: Vertices) {
        //set the attribute of the vertex to point to the vertex buffer
        aVertexColor.setValue(vertices)
    }
    fun setNormalInput(vertices: Vertices) {
        aVertexNormal.setValue(vertices)
    }

    fun setModelViewPerspectiveInput(mvpMatrix : Matrix) {
        uMvpMatrix.setValue(mvpMatrix)
    }

    fun setLightLocationInput(position: Vertex3) {
        uLightLocation.setValue(position)
    }

    fun setDiffuseColor(color: Vertex4) {
        uDiffuseColor.setValue(color)
    }

    fun setAttenuation(attenuation: Vertex3) {
        uAttenuation.setValue(attenuation)
    }

    fun setAmbientColor(color: Vertex4) {
        uAmbientColor.setValue(color)
    }

    fun setSpecularColor(color: Vertex4) {
        uSpecularColor.setValue(color)
    }

    fun setMaterialShininess(shininess: Float) {
        uMaterialShininess.setValue(shininess)
    }

}