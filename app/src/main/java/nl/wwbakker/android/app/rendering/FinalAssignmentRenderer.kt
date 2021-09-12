package nl.wwbakker.android.app.rendering

import android.content.Context
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.ModelViewProjection
import nl.wwbakker.android.app.data.SIMPLE_NEAR_Z
import nl.wwbakker.android.app.data.Side
import nl.wwbakker.android.app.shapes.*
import nl.wwbakker.android.app.usercontrol.SensorControl
import nl.wwbakker.android.app.usercontrol.TouchControl
import java.util.concurrent.atomic.AtomicBoolean
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos


class FinalAssignmentRenderer(private val touchControl: TouchControl,
                              private val sensorControl: SensorControl,
                              private val context: Context) : GLSurfaceView.Renderer {

    lateinit var projectionMatrix : Matrix
    var tick = 0L
    val skybox = ImperialCollegeSkybox
    val gorilla = Gorilla
    val persona = Persona
    val redSphere = SphereLighted

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES32.glCullFace(GLES32.GL_BACK)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        // Set the background frame color to black
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        frameBufferDisplay.load(context)
        skybox.load(context)
        gorilla.load(context)
        redSphere.load(context)
        persona.load(context)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        frameBufferDisplay.onSurfaceChanged(width, height)
        // Adjust the view based on view window changes, such as screen rotation
        GLES32.glViewport(0, 0, width, height)
        projectionMatrix = Matrix.simpleProjectionMatrix(width, height)
    }

    private val currentlyDrawingFrame = AtomicBoolean(false)
    override fun onDrawFrame(unused: GL10) {
        if (!currentlyDrawingFrame.compareAndSet(false, true)) {
            return
        }
        // Make sure this is not called concurrently (causing segfaults).
        // Skipping frames when rendering of previous frame is not finished
        drawFrameSafe()
        currentlyDrawingFrame.set(false)
    }

    private fun drawFrameSafe() {
        // Draw background color
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
        GLES32.glClearDepthf(1.0f) //set up the depth buffer
//        GLES32.glEnable(GLES32.GL_DEPTH_TEST) //enable depth test (so, it will not look through the surfaces)
//        GLES32.glDepthFunc(GLES32.GL_LEQUAL) //indicate what type of depth test
        frameBufferDisplay.draw()
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
            val stereoModelMatrix =
                Matrix.multiply(
                    Matrix.translate(
                        x = if (side == Side.LEFT) modelTranslationX else -modelTranslationX,
                        z = depthZ)
                )

            skybox.draw(ModelViewProjection(
                stereoProjectionMatrix,
                viewMatrix = stereoViewMatrix,
                worldMatrix = defaultWorldMatrix,
                modelMatrix = Matrix.multiply(
                    Matrix.translate(y = -1f),
                    stereoModelMatrix,
                )
            ))

//            gorilla.draw(
//                ModelViewProjection(
//                    stereoProjectionMatrix,
//                    viewMatrix = stereoViewMatrix,
//                    worldMatrix =
//                    Matrix.multiply(
//                        defaultWorldMatrix,
//                        Matrix.translate(0f, 0.5f, -0.8f),
//                        Matrix.rotate(180f, y = 1f),
//                    ),
//                    modelMatrix =
//                        Matrix.multiply(
//                            Matrix.scale(2f, 2f, 1f),
//                            stereoModelMatrix,
//                        )
//                )
//            )

            persona.draw(
                ModelViewProjection(
                    stereoProjectionMatrix,
                    viewMatrix = stereoViewMatrix,
                    worldMatrix =
                    Matrix.multiply(
                        defaultWorldMatrix,
                        Matrix.translate(cos(tick / 25f) * 2f, 0.5f, -1.8f),
                        Matrix.rotate(degrees = cos(tick.toFloat() / 10f) * 10f, z = 1f),
                        Matrix.rotate(180f, y = 1f),
                    ),
                    modelMatrix =
                        Matrix.multiply(
                            Matrix.scale(1f, 2f, 1f),
                            stereoModelMatrix,
                        )
                )
            )

            redSphere.draw(
                ModelViewProjection(
                    stereoProjectionMatrix,
                    viewMatrix = stereoViewMatrix,
                    worldMatrix =
                    Matrix.multiply(
                        defaultWorldMatrix,
                        Matrix.translate(0f, 0f, -0.4f),
                    ),
                    modelMatrix = stereoModelMatrix
                )
            )
        }

    }
}