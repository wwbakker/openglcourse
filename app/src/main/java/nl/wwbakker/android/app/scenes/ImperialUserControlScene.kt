package nl.wwbakker.android.app.scenes

import nl.wwbakker.android.app.data.Matrix

object ImperialUserControlScene {

    val textScene = TextScene("IMPERIAL")
    fun draw(projectionMatrix: Matrix, angleX : Float, angleY: Float) {
        textScene.draw(projectionMatrix, angleX, angleY)
    }
}