package nl.wwbakker.android.app.scenes

import nl.wwbakker.android.app.data.Matrix

class ImperialScene {
    private val text = TextScene("IMPERIAL")

    fun draw(projectionMatrix: Matrix, tick : Long) {
        text.draw(projectionMatrix, tick)
    }
}