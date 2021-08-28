package nl.wwbakker.android.app.shapes

import android.content.Context
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.ModelViewProjection

interface Shape {
    fun load(context : Context) {}
    fun draw(modelViewProjection: ModelViewProjection)
}