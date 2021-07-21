package nl.wwbakker.android.app.shapes

import android.content.Context
import nl.wwbakker.android.app.data.Matrix

interface Shape {
    fun load(context : Context) {}
    fun draw(projectionMatrix : Matrix, worldMatrix: Matrix = Matrix.identity())
}