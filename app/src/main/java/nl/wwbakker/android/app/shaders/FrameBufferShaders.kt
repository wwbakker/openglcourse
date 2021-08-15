package nl.wwbakker.android.app.shaders

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.GLUtils
import nl.wwbakker.android.app.ShaderCompileHelper
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Vertices
import nl.wwbakker.android.app.rendering.RenderAndFrameBuffer.Companion.FRAMEBUFFER_TEXTURE_INDEX
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

    private var textureHandle by Delegates.notNull<Int>()

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
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_NEAREST)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_NEAREST)

        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_CLAMP_TO_EDGE)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_CLAMP_TO_EDGE)

    }

    fun setActiveTexture() {
        GLES32.glActiveTexture(GLES32.GL_TEXTURE1)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureHandle)
        uTextureSampler.setIndex(1)
    }

    fun setTexture(frameBufferTextureId: Int) {
        GLES32.glActiveTexture(FRAMEBUFFER_TEXTURE_INDEX)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, frameBufferTextureId)
        uTextureSampler.setIndex(1)
    }
}