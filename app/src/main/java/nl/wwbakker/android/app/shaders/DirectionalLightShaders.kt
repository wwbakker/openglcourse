package nl.wwbakker.android.app.shaders

import android.opengl.GLES32
import nl.wwbakker.android.app.ShaderCompileHelper
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Vertex3
import nl.wwbakker.android.app.data.Vertex4
import nl.wwbakker.android.app.data.Vertices
import nl.wwbakker.android.app.shaders.Qualifier.*
import kotlin.properties.Delegates

object DirectionalLightShaders {
    private var programHandle by Delegates.notNull<Int>()
    private val aVertexPosition = ShaderVariable(Attribute, "vec3", "aVertexPosition")
    private val aVertexColor = ShaderVariable(Attribute, "vec4", "aVertexColor")
    private val aVertexNormal = ShaderVariable(Attribute, "vec3", "aVertexNormal")
    private val uMvpMatrix = ShaderVariable(Uniform, "mat4", "uMVPMatrix")
    private val vColor = ShaderVariable(Varying, "vec4", "vColor")
    private val uDiffuseLightLocation = ShaderVariable(Uniform, "vec3", "uDiffuseLightLocation")
    private val uDiffuseColor = ShaderVariable(Uniform, "vec4", "uDiffuseColor")
    private val vDiffuseColor = ShaderVariable(Varying, "vec4", "vDiffuseColor")
    private val vDiffuseLightWeight = ShaderVariable(Varying, "float", "vDiffuseLightWeight")
    private val uAttenuation = ShaderVariable(Uniform, "vec3", "uAttenuation")
    private val uAmbientColor = ShaderVariable(Uniform, "vec4", "uAmbientColor")
    private val vAmbientColor = ShaderVariable(Varying, "vec4", "vAmbientColor")
    private val uSpecularColor = ShaderVariable(Uniform, "vec4", "uSpecularColor")
    private val vSpecularColor = ShaderVariable(Varying, "vec4", "vSpecularColor")
    private val vSpecularLightWeight = ShaderVariable(Varying, "float", "vSpecularLightWeight")
    private val uSpecularLightLocation = ShaderVariable(Uniform, "vec3", "uSpecularLightLocation")
    private val uMaterialShininess = ShaderVariable(Uniform, "float", "uMaterialShininess")

    private val variables = listOf(
        aVertexPosition,
        aVertexColor,
        aVertexNormal,
        uMvpMatrix,
        vColor,
        uDiffuseLightLocation,
        uDiffuseColor,
        vDiffuseColor,
        vDiffuseLightWeight,
        uAttenuation,
        uAmbientColor,
        vAmbientColor,
        uSpecularColor,
        vSpecularColor,
        vSpecularLightWeight,
        uSpecularLightLocation,
        uMaterialShininess,
    )

    private val vertexShaderCode =
        """${variables.vertexShaderDefinitions()}
            float diffuseLightWeight();
            float specularLightWeight();
            float diffuseLightWeight() {
                vec3 diffuseLightDirection = normalize(uDiffuseLightLocation-gl_Position.xyz);
                vec3 transformedNormal = normalize((uMVPMatrix * vec4(aVertexNormal, 0.0)).xyz);
                vec3 vertexToLightSource = uDiffuseLightLocation-gl_Position.xyz;
                float diffuseLightDistance = length(vertexToLightSource);
                float attenuation = 1.0 / (uAttenuation.x + uAttenuation.y * diffuseLightDistance 
                    + uAttenuation.z * diffuseLightDistance * diffuseLightDistance);
                return attenuation * max(dot(transformedNormal,diffuseLightDirection),0.0);
            }
            float specularLightWeight() {
                vec3 viewDirection = normalize(-gl_Position.xyz);
                vec3 transformedNormal = normalize((uMVPMatrix * vec4(aVertexNormal, 0.0)).xyz);
                vec3 vertexToLightSource = uSpecularLightLocation-gl_Position.xyz;
                vec3 specularLightDirection= normalize(vertexToLightSource);
                float specularLightDistance = length(vertexToLightSource);
                vec3 reflectionDirection=reflect(-specularLightDirection, transformedNormal);
                float attenuation = 1.0 / (uAttenuation.x + uAttenuation.y * specularLightDistance 
                    + uAttenuation.z * specularLightDistance * specularLightDistance);
                return attenuation*pow(max(dot(reflectionDirection,viewDirection),0.0),uMaterialShininess);
            }
            
            void main() {
              gl_Position = uMVPMatrix *vec4(aVertexPosition,1.0);
              vDiffuseLightWeight = diffuseLightWeight();
              vSpecularLightWeight = specularLightWeight();
              vSpecularColor = uSpecularColor;
              vDiffuseColor = uDiffuseColor;
              vColor=aVertexColor;
              vAmbientColor=uAmbientColor;
           }""".trimIndent()
    private val fragmentShaderCode =
        """precision mediump float;
           ${variables.fragmentShaderDefinitions()}
           void main() {
                vec4 diffuseColor = vDiffuseLightWeight * vDiffuseColor;
                vec4 specularColor = vSpecularLightWeight * vSpecularColor;
                gl_FragColor = vColor*vAmbientColor+specularColor+diffuseColor;
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

    fun setDiffuseLightLocationInput(position: Vertex3) {
        uDiffuseLightLocation.setValue(position)
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

    fun setSpecularLightLocationInput(position: Vertex3) {
        uSpecularLightLocation.setValue(position)
    }

    fun setSpecularColor(color: Vertex4) {
        uSpecularColor.setValue(color)
    }

    fun setMaterialShininess(shininess: Float) {
        uMaterialShininess.setValue(shininess)
    }

}