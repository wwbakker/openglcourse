package nl.wwbakker.android.app.shapes

import android.content.Context
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

    fun load(context: Context) {
        FrameBufferShaders.initiate()
    }

    fun onSurfaceChanged(width: Int, height: Int) {
        this.width = width
        this.height = height
        renderAndFrameBufferLeft.init(width, height)
        renderAndFrameBufferRight.init(width, height)
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

    abstract fun drawUnto(side: Side)

    val stereoViewMatrix =
        Matrix.identity().set {
            android.opengl.Matrix.setLookAtM(
                it, 0,
                0.0f, 0f, 0.1f,  //camera is at (0,0,0.1)
                0f, 0f, 0f,  //looks at the origin
                0f, 1f, 0.0f
            ) //head is down (set to (0,1,0) to look from the top)
        }

    private fun mvpMatrix(side: Side) : Matrix =
        Matrix.simpleModelViewProjectionMatrix(
            projectionMatrix = orthographicProjectionMatrix,
            viewMatrix = stereoViewMatrix,
            modelMatrix =
            Matrix.multiply(
                Matrix.scale(x = halfWidth.toFloat() / height, 1f, 1f),
                Matrix.translate(x = if (side == Side.LEFT) -0.8f else 0.8f)
            )
        )

    fun draw() {
        renderAndFrameBufferLeft.bind()
        drawUnto(Side.LEFT)
        renderAndFrameBufferLeft.unbind()

        renderAndFrameBufferRight.bind()
        drawUnto(Side.RIGHT)
        renderAndFrameBufferRight.unbind()

        shaders.use()
        shaders.setPositionInput(position)
        shaders.setTextureCoordinateInput(textureCoordinates)

        shaders.setModelViewPerspectiveInput(mvpMatrix(Side.LEFT))
        shaders.setTexture(renderAndFrameBufferLeft.texture)
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.length, GLES32.GL_UNSIGNED_INT, indices.indexBuffer)

        shaders.setModelViewPerspectiveInput(mvpMatrix(Side.RIGHT))
        shaders.setTexture(renderAndFrameBufferRight.texture)
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.length, GLES32.GL_UNSIGNED_INT, indices.indexBuffer)
    }
}