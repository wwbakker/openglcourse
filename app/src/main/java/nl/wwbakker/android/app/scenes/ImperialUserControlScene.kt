package nl.wwbakker.android.app.scenes

import nl.wwbakker.android.app.data.Matrix

object ImperialUserControlScene {
    private val textScene = TextScene("IMPERIAL")
    fun draw(projectionMatrix: Matrix, worldMatrix: Matrix) {
        textScene.draw(projectionMatrix, worldMatrix)
    }
}