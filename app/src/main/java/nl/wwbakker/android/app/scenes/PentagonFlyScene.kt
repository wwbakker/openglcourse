package nl.wwbakker.android.app.scenes

import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.shapes.PentagonPrism

object PentagonFlyScene {

    private val pentagonPrism = PentagonPrism()


    fun rotate(tick :  Long) : Matrix {
        return Matrix.multiply(
            Matrix.rotate((tick % 360).toFloat(), 0f, 0f, 1f),
            Matrix.rotate((tick % 360).toFloat(), 0f, 0.75f, 0f),
            Matrix.rotate((tick % 360).toFloat(), 0.35f, 0f, 0f),
        )
    }


    fun draw(projectionMatrix: Matrix, tick : Long) {
        pentagonPrism.draw(
            projectionMatrix,
            worldMatrix = Matrix.multiply(
                Matrix.translate(z = -3f, y = 0f),
                Matrix.scale(0.3f),
                rotate(tick),
                Matrix.translate(y = 6f),
            ))
    }
}