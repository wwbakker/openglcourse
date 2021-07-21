package nl.wwbakker.android.app

import android.content.Context
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.util.Log
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.scenes.*
import nl.wwbakker.android.app.shaders.*
import nl.wwbakker.android.app.shapes.*
import nl.wwbakker.android.app.usercontrol.TouchControl
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class MyRenderer(private val touchControl: TouchControl, private val context: Context) : GLSurfaceView.Renderer {

    lateinit var projectionMatrix : Matrix
    var tick = 0L
    val shape = WorldLighted

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        VertexAndMultiColorShaders.initiate()
        PointLightShaders.initiate()
        DirectionalLightShaders.initiate()
        PhongLightShaders.initiate()
        TexturedLightedShaders.initiate()
        GLES32.glCullFace(GLES32.GL_BACK)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

        // Set the background frame color to black
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        shape.load(context)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        // Adjust the view based on view window changes, such as screen rotation
        GLES32.glViewport(0, 0, width, height)
        projectionMatrix = Matrix.simpleProjectionMatrix(width, height)
    }

    override fun onDrawFrame(unused: GL10) {

        // Draw background color
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
        GLES32.glClearDepthf(1.0f) //set up the depth buffer
        GLES32.glEnable(GLES32.GL_DEPTH_TEST) //enable depth test (so, it will not look through the surfaces)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL) //indicate what type of depth test

        val defaultWorldMatrix = Matrix.multiply(
            Matrix.translate(z = -3f),
            touchControl.rotationMatrix
        )

//        OwnLogoScene.draw(projectionMatrix, tick)
//        ImperialUserControlScene.draw(projectionMatrix, defaultWorldMatrix)
//        PentagonPrismLighted.draw(projectionMatrix, defaultWorldMatrix)
//        SphereLighted.draw(projectionMatrix, defaultWorldMatrix)
//        WorldLighted.draw(projectionMatrix, defaultWorldMatrix)
        shape.draw(projectionMatrix, defaultWorldMatrix)
    }

    companion object {
        fun checkGlError(glOperation: String) {
            var error: Int
            if (GLES32.glGetError().also { error = it } != GLES32.GL_NO_ERROR) {
                Log.e("MyRenderer", "$glOperation: glError $error")
            }
        }

        fun loadShader(type: Int, shaderCode: String?): Int {
            // create a vertex shader  (GLES32.GL_VERTEX_SHADER) or a fragment shader (GLES32.GL_FRAGMENT_SHADER)
            val shader = GLES32.glCreateShader(type)
            GLES32.glShaderSource(
                shader,
                shaderCode
            ) // add the source code to the shader and compile it
            GLES32.glCompileShader(shader)
            return shader
        }
    }
}