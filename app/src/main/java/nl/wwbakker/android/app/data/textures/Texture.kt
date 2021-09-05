package nl.wwbakker.android.app.data.textures

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.GLUtils

class Texture(
    // Is persistent
    val index: TextureIndex = TextureIndex.generate()) {

    // Needs to be reloaded when new surface is initialized
    val handleInArray = IntArray(1)
    var handle
        get() = handleInArray[0]
        set(v) {
            handleInArray[0] = v
        }

    fun loadFromResources(context : Context, textureResource : Int) {
        GLES32.glGenTextures(1, handleInArray, 0)
        assert(handleInArray[0] != 0) {"Cannot create texture handle"}
        val options = BitmapFactory.Options()
        options.inScaled = false
        val bitmap = BitmapFactory.decodeResource(context.resources, textureResource)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, handle)
        GLUtils.texImage2D(GLES32.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_NEAREST)
    }
}
