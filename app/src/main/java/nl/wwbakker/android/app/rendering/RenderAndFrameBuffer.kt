package nl.wwbakker.android.app.rendering

import android.opengl.GLES32
import android.util.Log
import nl.wwbakker.android.app.data.TextureIndex

class RenderAndFrameBuffer {
    private val frameBufferIds = IntArray(1)
    private val textureIds = IntArray(1)
    private val renderBufferIds = IntArray(1)
    private var initialized = false

    val textureIndex = TextureIndex.generate()
    val textureId : Int
        get() = textureIds[0]


    fun init(width: Int, height: Int) {
        if (initialized) {
            clean()
        }
        generateFrameBuffer()
        generateTexture(GLES32.GL_RGBA, GLES32.GL_UNSIGNED_BYTE, width, height)
        generateRenderBuffer(width, height)
        bindBuffersAndTexture()

        // glCheckFramebufferStatus returns a symbolic constant that identifies whether or not
        // the currently bound framebuffer is framebuffer complete, and if not, which of the rules
        // of framebuffer completeness is violated.
        // see: https://docs.gl/es2/glCheckFramebufferStatus
        val status = GLES32.glCheckFramebufferStatus(GLES32.GL_FRAMEBUFFER)
        if (status != GLES32.GL_FRAMEBUFFER_COMPLETE) {
            Log.e("RenderAndFrameBuffer", "Error while creating framebuffer")
        }

        unbindBuffersAndTexture()
    }

    private fun clean() {
        GLES32.glDeleteRenderbuffers(1, renderBufferIds, 0)
        GLES32.glDeleteFramebuffers(1, frameBufferIds, 0)
        GLES32.glDeleteTextures(1, textureIds, 0)
    }

    fun bind() {
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, frameBufferIds[0])
    }

    fun unbind() {
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0)
    }


    private fun generateFrameBuffer() {
        GLES32.glGenFramebuffers(1, frameBufferIds, 0)
        GLES32.glBindFramebuffer(GLES32.GL_DRAW_FRAMEBUFFER, frameBufferIds[0])
    }

    private fun generateTexture(pixelFormat: Int, type : Int, width: Int, height: Int) {
        GLES32.glGenTextures(1, textureIds, 0)

        GLES32.glActiveTexture(textureIndex.openGl)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureIds[0])

        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_NEAREST)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_NEAREST)

        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_CLAMP_TO_EDGE)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_CLAMP_TO_EDGE)

        GLES32.glTexImage2D(GLES32.GL_TEXTURE_2D, 0, pixelFormat, width, height, 0,
            pixelFormat, type, null)
    }

    private fun generateRenderBuffer(width: Int, height: Int) {
        GLES32.glGenRenderbuffers(1, renderBufferIds, 0)
        GLES32.glBindRenderbuffer(GLES32.GL_RENDERBUFFER, renderBufferIds[0])
        GLES32.glRenderbufferStorage(
            GLES32.GL_RENDERBUFFER,
            GLES32.GL_DEPTH_COMPONENT24,
            width,
            height
        )
    }

    private fun bindBuffersAndTexture() {
        GLES32.glFramebufferTexture2D(
            GLES32.GL_FRAMEBUFFER, GLES32.GL_COLOR_ATTACHMENT0,
            GLES32.GL_TEXTURE_2D, textureIds[0], 0
        )
        GLES32.glFramebufferRenderbuffer(
            GLES32.GL_FRAMEBUFFER, GLES32.GL_DEPTH_ATTACHMENT,
            GLES32.GL_RENDERBUFFER, renderBufferIds[0]
        )
    }

    private fun unbindBuffersAndTexture() {
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0)
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0)
    }
}