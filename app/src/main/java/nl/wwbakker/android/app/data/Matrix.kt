package nl.wwbakker.android.app.data

import android.opengl.Matrix as GlMatrix

class Matrix(val values : FloatArray = FloatArray(16)) {

    fun set(f : (FloatArray) -> Unit) : Matrix {
        f(values)
        return this
    }

    fun multiply(m : Matrix) : Matrix {
        return identity().set {
            GlMatrix.multiplyMM(it, 0, this.values, 0, m.values, 0)
        }
    }

    companion object {
        fun identity() : Matrix {
            val values = FloatArray(16)
            GlMatrix.setIdentityM(values, 0)
            return Matrix(values)
        }

        val simpleViewMatrix =
            identity().set {
                GlMatrix.setLookAtM(
                    it, 0,
                    0.0f, 0f, 1.0f,  //camera is at (0,0,1)
                    0f, 0f, 0f,  //looks at the origin
                    0f, 1f, 0.0f
                ) //head is down (set to (0,1,0) to look from the top)
            }

        val simpleModelMatrix =
            translate(0f, 0f, -5f)
                .multiply(rotate(30f, y = 1f))
                .multiply(rotate(30f, x = 1f))

        fun simpleProjectionMatrix(width: Int, height : Int) : Matrix {
            return identity().set {
                val ratio = width.toFloat() / height
                val left = -ratio
                android.opengl.Matrix.frustumM(it, 0, left, ratio, -1.0f, 1.0f, 1.0f, 8.0f)
            }
        }

        fun translate(x : Float = 0f, y : Float = 0f, z : Float = 0f) : Matrix =
            identity().set { GlMatrix.translateM(it, 0, x, y, z) }

        fun rotate(degrees: Float, x : Float = 0f, y : Float = 0f, z : Float = 0f) : Matrix =
            identity().set { GlMatrix.rotateM(it, 0, degrees, x, y, z) }

        fun simpleModelViewProjectionMatrix(projectionMatrix : Matrix) : Matrix =
            simpleModelMatrix
                .multiply(simpleViewMatrix)
                .multiply(projectionMatrix)

    }
}