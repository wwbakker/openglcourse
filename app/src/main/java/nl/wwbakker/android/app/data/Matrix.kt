package nl.wwbakker.android.app.data

import android.opengl.Matrix as GlMatrix

const val SIMPLE_NEAR_Z = 1.0f
const val SIMPLE_FAR_Z = 20.0f

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

        fun stereoViewMatrix(side: Side, intraOcularDistance: Float, screenZ: Float) : Matrix = identity().set {
            when(side) {
                Side.LEFT -> android.opengl.Matrix.setLookAtM(it, 0,
                    -intraOcularDistance / 2f, 0f, 0.1f,
                    0f, 0f, screenZ,
                    0f, 1f, 0f
                ) //head is down (set to (0,1,0) to look from the top)
                Side.RIGHT -> android.opengl.Matrix.setLookAtM(it, 0,
                    intraOcularDistance / 2f, 0f, 0.1f,
                    0f, 0f, screenZ,
                    0f, 1f, 0f
                ) //head is down (set to (0,1,0) to look from the top)
            }
        }

        fun simpleProjectionMatrix(width: Int, height : Int) : Matrix {
            return identity().set {
                val ratio = width.toFloat() / height
                val left = -ratio
                android.opengl.Matrix.frustumM(it, 0, left, ratio, -1.0f, 1.0f, SIMPLE_NEAR_Z, SIMPLE_FAR_Z)
            }
        }

        fun stereoProjectionMatrix(side: Side, width: Int, height : Int, frustumShift: Float) : Matrix = identity().set {
            val aspectRatio = width.toFloat() / height
            when(side) {
                Side.LEFT -> android.opengl.Matrix.frustumM(it, 0, frustumShift-aspectRatio, frustumShift+aspectRatio, -1.0f, 1.0f, SIMPLE_NEAR_Z, SIMPLE_FAR_Z)
                Side.RIGHT -> android.opengl.Matrix.frustumM(it, 0, -frustumShift-aspectRatio, -frustumShift+aspectRatio, -1.0f, 1.0f, SIMPLE_NEAR_Z, SIMPLE_FAR_Z)
            }
        }



        fun orthographicProjectionMatrix(width: Int, height: Int) : Matrix {
            return identity().set {
                if (width > height) {
                    val ratio = width / height.toFloat()
                    GlMatrix.orthoM(it, 0, -ratio, ratio, -1f, 1f, -10f, 200f)
                } else {
                    val ratio = height / width.toFloat()
                    GlMatrix.orthoM(it, 0, -1f, 1f, -ratio, ratio, -10f, 200f)
                }
            }
        }

        fun translate(x : Float = 0f, y : Float = 0f, z : Float = 0f) : Matrix =
            identity().set { GlMatrix.translateM(it, 0, x, y, z) }

        fun rotate(degrees: Float, x : Float = 0f, y : Float = 0f, z : Float = 0f) : Matrix =
            identity().set { GlMatrix.rotateM(it, 0, degrees, x, y, z) }

        fun scale(xyz : Float = 0f) : Matrix =
            identity().set { GlMatrix.scaleM(it, 0, xyz, xyz, xyz) }

        fun scale(x : Float = 1f, y : Float = 1f, z : Float = 1f) : Matrix =
            identity().set { GlMatrix.scaleM(it, 0, x, y, z) }


        fun simpleModelViewProjectionMatrix(projectionMatrix : Matrix,
                                            viewMatrix : Matrix = simpleViewMatrix,
                                            modelMatrix : Matrix = identity(),
                                            worldMatrix : Matrix = identity()) : Matrix =
            projectionMatrix
                .multiply(simpleViewMatrix)
                .multiply(modelMatrix)
                .multiply(worldMatrix)

        fun multiply(vararg m : Matrix) : Matrix {
            return m.fold(identity()){ acc, matrix -> acc.multiply(matrix) }
        }

    }
}