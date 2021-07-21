package nl.wwbakker.android.app.data

data class Vertex4(val r : Float, val g : Float, val b : Float, val a : Float) {
    val floatArray = arrayOf(r,g,b,a).toFloatArray()
    constructor(v : Float) : this(v, v, v, 1f)
}