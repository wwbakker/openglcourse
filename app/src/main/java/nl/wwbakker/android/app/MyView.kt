package nl.wwbakker.android.app

import android.content.Context
import android.content.res.Resources
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import androidx.lifecycle.Lifecycle
import nl.wwbakker.android.app.usercontrol.SensorControl
import nl.wwbakker.android.app.usercontrol.TouchControl
import java.util.*
import kotlin.concurrent.timerTask


class MyView(context: Context, lifecycle : Lifecycle) : GLSurfaceView(context) {
    private val touchControl = TouchControl(
        Resources.getSystem().displayMetrics.widthPixels,
        Resources.getSystem().displayMetrics.heightPixels,
        context)

    private val sensorControl = SensorControl(context)
    private val mRenderer = MyRenderer(touchControl, sensorControl, context)
    init {
        lifecycle.addObserver(sensorControl)
        setEGLContextClientVersion(2) // Create an OpenGL ES 2.0 context.
        setRenderer(mRenderer)
        // Render the view only when there is a change in the drawing data
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    val timer = Timer()
    val task = timerTask {
        this@MyView.requestRender()
        mRenderer.tick += 1L
    }
    init {
        timer.scheduleAtFixedRate(task, 100L, 16L)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return touchControl.onTouchEvent(event!!)
    }
}