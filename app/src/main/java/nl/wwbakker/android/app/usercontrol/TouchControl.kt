package nl.wwbakker.android.app.usercontrol

import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import nl.wwbakker.android.app.data.Matrix

class TouchControl(screenWidth: Int, screenHeight: Int, context : Context) :
    ScaleGestureDetector.SimpleOnScaleGestureListener() {
    private val TOUCH_SCALE_FACTOR_X = 180f / screenWidth // 180 degrees over screen width
    private val TOUCH_SCALE_FACTOR_Y = 180f / screenHeight // 180 degrees over screen width

    private var previousX = 0f
    private var previousY = 0f
    private var angleX = 0f
    private var angleY = 0f
    private var scaleFactor = 1f

    private val sgd = ScaleGestureDetector(context, this)

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        scaleFactor *= sgd.scaleFactor;

        // Don't let the object get too small or too large.
        scaleFactor = scaleFactor.coerceIn(0.1f, 5.0f)

        return true;
    }


    fun onTouchEvent(event: MotionEvent): Boolean {
        sgd.onTouchEvent(event)
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                if (!sgd.isInProgress) {
                    calculateRotationAngles(x, y)
                }
            }
        }
        previousX = x
        previousY = y
        return true
    }

    private fun calculateRotationAngles(x: Float, y: Float) {
        val dx = x - previousX
        val dy = y - previousY
        angleX += dy * TOUCH_SCALE_FACTOR_Y
        angleY += dx * TOUCH_SCALE_FACTOR_X
    }

    private val rotationMatrix: Matrix
        get() {
            return Matrix.multiply(
                Matrix.rotate(angleX, x = 1f),
                Matrix.rotate(angleY, y = 1f),
            )
        }

    private val scaleMatrix: Matrix
        get() {
            return Matrix.scale(scaleFactor)
        }

    val scaleAndRotationMatrix: Matrix
        get() {
            return Matrix.multiply(
                scaleMatrix,
                rotationMatrix,
            )
        }

}