package nl.wwbakker.android.app

import android.content.Context
import android.opengl.GLSurfaceView
import java.util.*
import kotlin.concurrent.timerTask


class MyView(context: Context?) : GLSurfaceView(context) {
    private val mRenderer: MyRenderer

    init {
        setEGLContextClientVersion(2) // Create an OpenGL ES 2.0 context.
        mRenderer = MyRenderer() // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer)
        // Render the view only when there is a change in the drawing data
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    val timer = Timer()
    val task = timerTask {
        this@MyView.invalidate()
        this@MyView.requestRender()
        mRenderer.tick += 1L
    }
    init {
        timer.scheduleAtFixedRate(task, 100L, 16L)
    }
}