package nl.wwbakker.android.app.shaders

import android.opengl.GLES32
import android.util.Log
import nl.wwbakker.android.app.data.*
import nl.wwbakker.android.app.rendering.checkGlError
import kotlin.properties.Delegates

enum class Qualifier(val value : String) {
    UniformFragmentShader("uniform"),
    UniformVertexShader("uniform"),
    Varying("varying"),
    Attribute("attribute"),
    ;
}

data class ShaderVariable(val qualifier: Qualifier, val dataType: String, val name : String) {
    val definition = "${qualifier.value} $dataType $name;"
    init {
        assert(qualifier.value.first() == name.first()) { "prefix of $name is expected to be the first letter of $qualifier" }
    }

    private var handle by Delegates.notNull<Int>()
    fun initiate(programHandle : Int) {
        when(qualifier) {
            Qualifier.UniformVertexShader, Qualifier.UniformFragmentShader -> {
                handle = GLES32.glGetUniformLocation(programHandle, name)
                checkGlError("glGetUniformLocation")
            }
            Qualifier.Attribute -> {
                handle = GLES32.glGetAttribLocation(programHandle, name)
                checkGlError("glGetAttribLocation")
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun checkVariableExists() : Boolean {
        if (handle == -1) {
            Log.w("ShaderVariable", "Could not set variable '$name'. Variable does not exist")
            return false
        }
        return true
    }

    fun setValue(vertices: Vertices) {
        assert(dataType == "vec4" || dataType == "vec3" || dataType == "vec2")
        if (checkVariableExists()) {
            //set the attribute of the vertex to Diffuse to the vertex buffer
            GLES32.glVertexAttribPointer(
                handle, vertices.valuesPerVertex,
                GLES32.GL_FLOAT, false, vertices.vertexStride, vertices.vertexBuffer
            )
            GLES32.glEnableVertexAttribArray(handle)
            checkGlError("glVertexAttribPointer")
        }
    }

    fun setValue(matrix : Matrix) {
        assert(dataType == "mat4")
        if (checkVariableExists()) {
            GLES32.glUniformMatrix4fv(handle, 1, false, matrix.values, 0)
            checkGlError("glUniformMatrix4fv")
        }
    }

    fun setValue(position: Vertex3) {
        assert(dataType == "vec3")
        if (checkVariableExists()) {
            GLES32.glUniform3fv(handle, 1, position.floatArray, 0)
            checkGlError("glUniform3fv")
        }
    }

    fun setValue(position: Vertex4) {
        assert(dataType == "vec4")
        if (checkVariableExists()) {
            GLES32.glUniform4fv(handle, 1, position.floatArray, 0)
            checkGlError("glUniform4fv")
        }
    }

    fun setValue(value: Float) {
        assert(dataType == "float")
        if (checkVariableExists()) {
            GLES32.glUniform1f(handle, value)
            checkGlError("glUniform1f")
        }
    }

    fun setIndex(index : Int) {
        assert(dataType == "sampler2D")
        if (checkVariableExists()) {
            GLES32.glUniform1i(handle, index)
            checkGlError("glUniform1i")
        }
    }
}


fun List<ShaderVariable>.vertexShaderDefinitions() : String {
    return filter { it.qualifier != Qualifier.UniformFragmentShader }.joinToString(separator = "\n", postfix = "\n") { it.definition }
}

fun List<ShaderVariable>.fragmentShaderDefinitions() : String {
    return filter { it.qualifier == Qualifier.Varying || it.qualifier == Qualifier.UniformFragmentShader }.joinToString(separator = "\n", postfix = "\n") { it.definition }
}