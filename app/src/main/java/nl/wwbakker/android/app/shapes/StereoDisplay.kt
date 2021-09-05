package nl.wwbakker.android.app.shapes

import android.opengl.GLES32
import nl.wwbakker.android.app.data.Indices
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Side
import nl.wwbakker.android.app.data.Vertices
import nl.wwbakker.android.app.rendering.RenderAndFrameBuffer
import nl.wwbakker.android.app.shaders.FrameBufferShaders
import kotlin.properties.Delegates

abstract class StereoDisplay {

    private val renderAndFrameBufferLeft: RenderAndFrameBuffer = RenderAndFrameBuffer()
    private val renderAndFrameBufferRight: RenderAndFrameBuffer = RenderAndFrameBuffer()
    private var orthographicProjectionMatrix = Matrix.identity()

    var width by Delegates.notNull<Int>()
        private set
    var height by Delegates.notNull<Int>()
        private set

    val halfWidth: Int
        get() = width / 2

    val shaders = FrameBufferShaders

    fun onSurfaceChanged(width: Int, height: Int) {
        this.width = width
        this.height = height
        renderAndFrameBufferLeft.init(halfWidth, height)
        renderAndFrameBufferRight.init(halfWidth, height)
        orthographicProjectionMatrix = Matrix.orthographicProjectionMatrix(halfWidth, height)
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

    abstract fun drawUnto(side: Side)

    fun draw() {
        renderAndFrameBufferLeft.bind()
        GLES32.glViewport(0, 0, halfWidth, height)
        drawUnto(Side.LEFT)
        renderAndFrameBufferLeft.unbind()

        renderAndFrameBufferRight.bind()
        GLES32.glViewport(0, 0, halfWidth, height)
        drawUnto(Side.RIGHT)
        renderAndFrameBufferRight.unbind()

        val mvpMatrix = Matrix.simpleModelViewProjectionMatrix(
            projectionMatrix = orthographicProjectionMatrix,
            modelMatrix = Matrix.scale(y = height.toFloat() / width)
        )

        shaders.use()
        shaders.setPositionInput(position)
        shaders.setTextureCoordinateInput(textureCoordinates)
        shaders.setModelViewPerspectiveInput(mvpMatrix)

        GLES32.glViewport(0, 0, halfWidth, height)
        shaders.setTexture(renderAndFrameBufferLeft.texture)
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.length, GLES32.GL_UNSIGNED_INT, indices.indexBuffer)

        GLES32.glViewport(halfWidth, 0, halfWidth, height)
        shaders.setTexture(renderAndFrameBufferRight.texture)
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.length, GLES32.GL_UNSIGNED_INT, indices.indexBuffer)
    }
}