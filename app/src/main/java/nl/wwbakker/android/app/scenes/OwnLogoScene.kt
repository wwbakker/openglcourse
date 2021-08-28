package nl.wwbakker.android.app.scenes

import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.ModelViewProjection
import nl.wwbakker.android.app.shapes.Pyramid3d

object OwnLogoScene {
    private val text = TextScene("SPIRE")
    private val pyramid = Pyramid3d()

    fun draw(projectionMatrix: Matrix, tick : Long) {
        PentagonFlyScene.draw(projectionMatrix, tick)

        val mvp = ModelViewProjection(projectionMatrix, worldMatrix = Matrix.multiply(
            Matrix.translate(z = -3f),
            Matrix.rotate(-30f, x = -1f),
        ))

        pyramid.draw(mvp)

        text.draw(mvp.projectionMatrix, tick)
    }


}