package nl.wwbakker.android.app.shapes.characters

import nl.wwbakker.android.app.data.BezierCurve
import nl.wwbakker.android.app.data.Position2D

object CharacterP : TwoLineShape() {

    override fun leftLine(): List<Position2D> {
        return listOf(
            Position2D(-0.3f, -0.5f),
            Position2D(-0.3f,  0.5f),
            Position2D(0.2f,  0.5f),
        ) +
        BezierCurve(listOf(
            Position2D(0.2f,  0.5f),
            Position2D(0.5f,0.5f),
            Position2D(0.5f,0f ),
            Position2D(0.2f, 0f),
        )).vertices(10, addFinalVertex = true) +
        listOf(
            Position2D(0.2f, 0f),
            Position2D(-0.1f, 0f),
        )
    }

    override fun rightLine(): List<Position2D> {
        return listOf(
            Position2D(-0.1f,-0.5f),
            Position2D(-0.1f,0.35f),
            Position2D(0.2f,0.35f),
        ) +
        BezierCurve(listOf(
            Position2D(0.2f,0.35f),
            Position2D(0.3f,0.35f),
            Position2D(0.3f,0.15f),
            Position2D(0.2f,0.15f),
        )).vertices(10, addFinalVertex = true) +
        listOf(
            Position2D(0.2f, 0.15f),
            Position2D(-0.1f,0.15f),
        )
    }

}