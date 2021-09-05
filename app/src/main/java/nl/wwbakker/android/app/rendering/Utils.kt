package nl.wwbakker.android.app.rendering

import android.opengl.GLES32
import android.util.Log

fun checkGlError(glOperation: String) {
    var error: Int
    if (GLES32.glGetError().also { error = it } != GLES32.GL_NO_ERROR) {
        Log.e("MyRenderer", "$glOperation: glError $error")
    }
}

fun loadShader(type: Int, shaderCode: String?): Int {
    // create a vertex shader  (GLES32.GL_VERTEX_SHADER) or a fragment shader (GLES32.GL_FRAGMENT_SHADER)
    val shader = GLES32.glCreateShader(type)
    GLES32.glShaderSource(
        shader,
        shaderCode
    ) // add the source code to the shader and compile it
    GLES32.glCompileShader(shader)
    return shader
}