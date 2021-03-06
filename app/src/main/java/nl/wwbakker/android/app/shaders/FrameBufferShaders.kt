package nl.wwbakker.android.app.shaders

import android.opengl.GLES32
import nl.wwbakker.android.app.ShaderCompileHelper
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Vertices
import nl.wwbakker.android.app.data.textures.Texture
import nl.wwbakker.android.app.shaders.Qualifier.*
import kotlin.properties.Delegates

object FrameBufferShaders {
    private var programHandle by Delegates.notNull<Int>()
    private val aVertexPosition = ShaderVariable(Attribute, "vec3", "aVertexPosition")
    private val uMvpMatrix = ShaderVariable(UniformVertexShader, "mat4", "uMVPMatrix")
    private val aTextureCoordinate = ShaderVariable(Attribute, "vec2", "aTextureCoordinate")
    private val vTextureCoordinate = ShaderVariable(Varying, "vec2", "vTextureCoordinate")
    private val uTextureSampler = ShaderVariable(UniformFragmentShader, "sampler2D", "uTextureSampler")

    private val variables = listOf(
        aVertexPosition,
        uMvpMatrix,
        aTextureCoordinate,
        vTextureCoordinate,
        uTextureSampler,
    )

    private val vertexShaderCode =
        """${variables.vertexShaderDefinitions()}
            void main() {
              gl_Position = uMVPMatrix *vec4(aVertexPosition,1.0);
              vTextureCoordinate=aTextureCoordinate;
           }""".trimIndent()
    private val fragmentShaderCode =
        """precision lowp float;
           ${variables.fragmentShaderDefinitions()}
           void main() {
                vec4 fragmentColor = texture2D(uTextureSampler,vec2(vTextureCoordinate.s,vTextureCoordinate.t));
                if (fragmentColor.r<0.01 && fragmentColor.g<0.01 && fragmentColor.b<0.01) discard;
                else gl_FragColor = vec4(fragmentColor.rgb, fragmentColor.a);
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

    fun setTextureCoordinateInput(vertices: Vertices) {
        aTextureCoordinate.setValue(vertices)
    }

    fun setModelViewPerspectiveInput(mvpMatrix : Matrix) {
        uMvpMatrix.setValue(mvpMatrix)
    }

    fun setTexture(texture: Texture) {
        GLES32.glActiveTexture(texture.index.openGl)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, texture.handle)
        uTextureSampler.setIndex(texture.index.shader)
    }
}