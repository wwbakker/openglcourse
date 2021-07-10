package nl.wwbakker.android.app.usercontrol

import android.view.MotionEvent
import nl.wwbakker.android.app.data.Matrix

class TouchControl(screenWidth: Int, screenHeight: Int) {
    private val TOUCH_SCALE_FACTOR_X = 180f / screenWidth // 180 degrees over screen width
    private val TOUCH_SCALE_FACTOR_Y = 180f / screenHeight // 180 degrees over screen width
    private var previousX = 0f
    private var previousY = 0f
    var angleX = 0f
    var angleY = 0f

    fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx = x - previousX
                val dy = y - previousY
                angleX += dy * TOUCH_SCALE_FACTOR_Y
                angleY += dx * TOUCH_SCALE_FACTOR_X
            }
        }
        previousX = x
        previousY = y
        return true
    }

    val rotationMatrix: Matrix
        get() {
            return Matrix.multiply(
                Matrix.rotate(angleX, x = 1f),
                Matrix.rotate(angleY, y = 1f),
            )
        }
}