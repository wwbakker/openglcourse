package nl.wwbakker.android.app.shaders

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.GLUtils
import nl.wwbakker.android.app.ShaderCompileHelper
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Vertex3
import nl.wwbakker.android.app.data.Vertex4
import nl.wwbakker.android.app.data.Vertices
import nl.wwbakker.android.app.shaders.Qualifier.*
import kotlin.properties.Delegates

object TexturedLightedShaders {
    private var programHandle by Delegates.notNull<Int>()
    private val aVertexPosition = ShaderVariable(Attribute, "vec3", "aVertexPosition")
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

    private val aTextureCoordinate = ShaderVariable(Attribute, "vec2", "aTextureCoordinate")
    private val vTextureCoordinate = ShaderVariable(Varying, "vec2", "vTextureCoordinate")
    private val uTextureSampler = ShaderVariable(UniformFragmentShader, "sampler2D", "uTextureSampler")
    private var textureHandle by Delegates.notNull<Int>()

    private val variables = listOf(
        aVertexPosition,
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
        aTextureCoordinate,
        vTextureCoordinate,
        uTextureSampler,
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
              vTextureCoordinate=aTextureCoordinate;
           }""".trimIndent()
    private val fragmentShaderCode =
        """precision mediump float;
           ${variables.fragmentShaderDefinitions()}
           void main() {
                vec4 diffuseColor = vDiffuseLightWeight * uDiffuseColor;
                vec4 specularColor = vSpecularLightWeight * uSpecularColor;
                vec4 fragmentColor = texture2D(uTextureSampler,vec2(vTextureCoordinate.s,vTextureCoordinate.t));
                gl_FragColor = fragmentColor*uAmbientColor+specularColor+diffuseColor;
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

    fun setNormalInput(vertices: Vertices) {
        aVertexNormal.setValue(vertices)
    }

    fun setTextureCoordinateInput(vertices: Vertices) {
        aTextureCoordinate.setValue(vertices)
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

    fun loadTextureFromResourcesOnce(context : Context, textureResource : Int) {
        val newTextureHandle = arrayOf(0).toIntArray()
        GLES32.glGenTextures(1, newTextureHandle, 0)
        assert(newTextureHandle[0] != 0) {"Cannot create texture handle"}
        val options = BitmapFactory.Options()
        options.inScaled = false
        val bitmap = BitmapFactory.decodeResource(context.resources, textureResource)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, newTextureHandle[0])
        GLUtils.texImage2D(GLES32.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
        textureHandle = newTextureHandle[0]
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D,GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D,GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_NEAREST)
    }

    fun setActiveTexture() {
        GLES32.glActiveTexture(GLES32.GL_TEXTURE1)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureHandle)
        uTextureSampler.setIndex(0)
    }


}