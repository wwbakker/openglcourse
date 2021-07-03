package nl.wwbakker.android.app.shapes.characters

import nl.wwbakker.android.app.data.BezierCurve
import nl.wwbakker.android.app.data.Position2D

class CharacterS : Character3D() {

    override fun leftLine(): List<Position2D> =
        listOf(
            BezierCurve(listOf(
                Position2D(13f,19f),
                Position2D(1f,19f),
                Position2D(1f,10f),
                Position2D(8f,10f),
            )).vertices(10, addFinalVertex = false),
            BezierCurve(listOf(
                Position2D(10f,10f),
                Position2D(11f,10f),
                Position2D(11f,6f),
                Position2D(3f,6f),
            )).vertices(10, addFinalVertex = true),
        ).flatten()


    override fun rightLine(): List<Position2D> =
        listOf(
            BezierCurve(listOf(
                Position2D(13f,16f),
                Position2D(5f,16f),
                Position2D(5f,13f),
                Position2D(8f,13f),
            )).vertices(10, addFinalVertex = false),
            BezierCurve(listOf(
                Position2D(8f,13f),
                Position2D(15f,13f),
                Position2D(15f,3f),
                Position2D(3f,3f),
            )).vertices(10, addFinalVertex = true),
        ).flatten()

}