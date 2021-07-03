package nl.wwbakker.android.app.shapes.characters

import nl.wwbakker.android.app.data.Position2D

object CharacterL : TwoLineShape() {

    override fun leftLine(): List<Position2D> {
        return listOf(
            Position2D(0.25f, -0.5f),
            Position2D(-0.25f, -0.5f),
            Position2D(-0.25f, 0.5f),
        )
    }

    override fun rightLine(): List<Position2D> {
        return listOf(
            Position2D(0.25f, -0.35f),
            Position2D(-0.1f, -0.35f),
            Position2D(-0.1f, 0.5f),
        )
    }
}