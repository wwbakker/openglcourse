package nl.wwbakker.android.app.shaders

import android.opengl.GLES32
import nl.wwbakker.android.app.MyRenderer
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Vertex
import nl.wwbakker.android.app.data.Vertices
import kotlin.properties.Delegates

enum class Qualifier(val value : String) {
    Uniform("uniform"),
    Varying("varying"),
    Attribute("attribute"),
    ;
}

data class ShaderVariable(val qualifier: Qualifier, val dataType: String, val name : String) {
    val definition = "${qualifier.value} $dataType $name;"

    private var handle by Delegates.notNull<Int>()
    fun initiate(programHandle : Int) {
        when(qualifier) {
            Qualifier.Uniform -> {
                handle = GLES32.glGetUniformLocation(programHandle, name)
                MyRenderer.checkGlError("glGetUniformLocation")
            }
            Qualifier.Attribute -> {
                handle = GLES32.glGetAttribLocation(programHandle, name)
                MyRenderer.checkGlError("glGetAttribLocation")
            }
            else -> {
                // do nothing
            }
        }
    }

    fun setValue(vertices: Vertices) {
        assert(dataType == "vec4" || dataType == "vec3")
        //set the attribute of the vertex to Diffuse to the vertex buffer
        GLES32.glVertexAttribPointer(
            handle, vertices.valuesPerVertex,
            GLES32.GL_FLOAT, false, vertices.vertexStride, vertices.vertexBuffer
        )
        MyRenderer.checkGlError("glVertexAttribPointer")
    }

    fun setValue(matrix : Matrix) {
        assert(dataType == "mat4")
        GLES32.glUniformMatrix4fv(handle, 1, false, matrix.values, 0)
        MyRenderer.checkGlError("glUniformMatrix4fv")
    }

    fun setValue(position: Vertex) {
        assert(dataType == "vec3")
        GLES32.glUniform3fv(handle, 1, position.floatArray, 0)
        MyRenderer.checkGlError("glUniform3fv")
    }

    fun setValue(value: Float) {
        assert(dataType == "float")
        GLES32.glUniform1f(handle, value)
        MyRenderer.checkGlError("glUniform1f")
    }
}


fun List<ShaderVariable>.vertexShaderDefinitions() : String {
    return joinToString(separator = "\n", postfix = "\n") { it.definition }
}

fun List<ShaderVariable>.pixelShaderDefinitions() : String {
    return filter { it.qualifier == Qualifier.Varying }.joinToString(separator = "\n", postfix = "\n") { it.definition }
}