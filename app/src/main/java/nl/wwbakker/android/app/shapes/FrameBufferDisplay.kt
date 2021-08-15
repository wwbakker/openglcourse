package nl.wwbakker.android.app.shapes

import android.content.Context
import android.opengl.GLES32
import nl.wwbakker.android.app.R
import nl.wwbakker.android.app.data.*
import nl.wwbakker.android.app.rendering.RenderAndFrameBuffer
import nl.wwbakker.android.app.shaders.FrameBufferShaders
import kotlin.properties.Delegates

abstract class FrameBufferDisplay {

    private val renderAndFrameBuffer: RenderAndFrameBuffer = RenderAndFrameBuffer()
    private var orthographicProjectionMatrix = Matrix.identity()

    var width by Delegates.notNull<Int>()
        private set
    var height by Delegates.notNull<Int>()
        private set

    val shaders = FrameBufferShaders

    fun onSurfaceChanged(width: Int, height: Int) {
        this.width = width
        this.height = height
        renderAndFrameBuffer.init(width, height)
        orthographicProjectionMatrix = Matrix.orthographicProjectionMatrix(width, height)
    }

    private val position = Vertices(
        arrayOf(
            -1f, -1f, 1f,
            1f, -1f, 1f,
            1f, 1f, 1f,
            -1f, 1f, 1f,
        ).toFloatArray(), 3
    )
    private val indices = Indices(arrayOf(0,1,2,0,2,3).toIntArray())
    private val textureCoordinates = Vertices(
        arrayOf(
            0f,0f,
            1f,0f,
            1f,1f,
            0f,1f,
        ).toFloatArray(), 2)


    abstract fun drawUnto()

    fun draw() {
        renderAndFrameBuffer.bind()
        GLES32.glViewport(0, 0, width, height)
        drawUnto()
        renderAndFrameBuffer.unbind()

        GLES32.glViewport(0, 0, width, height)
        val mvpMatrix = Matrix.simpleModelViewProjectionMatrix(
            projectionMatrix = orthographicProjectionMatrix,
            modelMatrix = Matrix.scale(y = height.toFloat() / width)
        )

        shaders.use()
        shaders.setPositionInput(position)
        shaders.setTextureCoordinateInput(textureCoordinates)
        shaders.setModelViewPerspectiveInput(mvpMatrix)
        shaders.setTexture(renderAndFrameBuffer.textureId)
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.length, GLES32.GL_UNSIGNED_INT, indices.indexBuffer)
    }
}