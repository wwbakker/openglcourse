package nl.wwbakker.android.app.shapes

import nl.wwbakker.android.app.data.Matrix

interface Shape {
    fun draw(projectionMatrix : Matrix, worldMatrix: Matrix = Matrix.identity())
}