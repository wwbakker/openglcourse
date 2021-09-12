package nl.wwbakker.android.app.rendering

import android.content.Context
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.util.Log
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.ModelViewProjection
import nl.wwbakker.android.app.data.SIMPLE_NEAR_Z
import nl.wwbakker.android.app.data.Side
import nl.wwbakker.android.app.shaders.*
import nl.wwbakker.android.app.shapes.*
import nl.wwbakker.android.app.usercontrol.SensorControl
import nl.wwbakker.android.app.usercontrol.TouchControl
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class StereoRenderer(private val touchControl: TouchControl,
                     private val sensorControl: SensorControl,
                     private val context: Context) : GLSurfaceView.Renderer {

    lateinit var projectionMatrix : Matrix
    var tick = 0L
//    val shape = nl.wwbakker.android.app.shapes.characters.CharacterM
//    val shape = PyramidTextured
    val shape = WorldLighted

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        frameBufferDisplay.load(context)
        GLES32.glCullFace(GLES32.GL_BACK)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        // Set the background frame color to black
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        shape.load(context)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        frameBufferDisplay.onSurfaceChanged(width, height)

        // Adjust the view based on view window changes, such as screen rotation
        GLES32.glViewport(0, 0, width, height)
        projectionMatrix = Matrix.simpleProjectionMatrix(width, height)
    }

    private val frameBufferDisplay = object : StereoDisplay() {
        override fun drawUnto(side: Side) {
            val intraOcularDistance = 0.8f
            val screenZ = -10f
            val depthZ = -5f
            val frustrumShift = -(intraOcularDistance / 2) * SIMPLE_NEAR_Z / screenZ
            val modelTranslationX = intraOcularDistance / 2f

            GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
            GLES32.glClearColor(0.2f, 0.2f, 0.2f, 1.0f)

            val stereoProjectionMatrix =
                Matrix.stereoProjectionMatrix(side, halfWidth, height, frustrumShift)
            val defaultWorldMatrix = Matrix.multiply(
                Matrix.translate(z = -2f),
                sensorControl.rotationMatrix,
                touchControl.scaleAndRotationMatrix,
            )
            val stereoViewMatrix = Matrix.stereoViewMatrix(side, intraOcularDistance, screenZ)
            val modelMatrix =
                Matrix.multiply(
                    Matrix.translate(
                        x = if (side == Side.LEFT) modelTranslationX else -modelTranslationX,
                        z = depthZ)
                )

            shape.draw(ModelViewProjection(
                stereoProjectionMatrix,
                viewMatrix = stereoViewMatrix,
                worldMatrix = defaultWorldMatrix,
                modelMatrix = modelMatrix
            ))
        }

    }

    override fun onDrawFrame(unused: GL10) {
        // Draw background color
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
        GLES32.glClearDepthf(1.0f) //set up the depth buffer
//        GLES32.glEnable(GLES32.GL_DEPTH_TEST) //enable depth test (so, it will not look through the surfaces)
//        GLES32.glDepthFunc(GLES32.GL_LEQUAL) //indicate what type of depth test
        frameBufferDisplay.draw()
    }
}