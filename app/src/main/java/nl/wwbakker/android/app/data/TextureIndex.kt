package nl.wwbakker.android.app.data

import android.opengl.GLES32

data class TextureIndex(private val index : Int) {
    val openGl : Int = GLES32.GL_TEXTURE0 + index
    val shader : Int = index

    companion object {
        private var currentIndex = 0
        fun generate() : TextureIndex =
            TextureIndex(currentIndex++)
    }
}