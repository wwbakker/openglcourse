package nl.wwbakker.android.app.shapes.characters

import nl.wwbakker.android.app.data.Position2D

object CharacterC : TwoLineShape() {
    override fun leftLine(): List<Position2D> {
        return listOf(
            Position2D(0.25f, -0.5f),
            Position2D(-0.25f, -0.5f),
            Position2D(-0.25f, 0.5f),
            Position2D(0.25f, 0.5f),
        )
    }

    override fun rightLine(): List<Position2D> {
        return listOf(
            Position2D(0.25f, -0.3f),
            Position2D(-0.1f, -0.3f),
            Position2D(-0.1f, 0.3f),
            Position2D(0.25f, 0.3f),
        )
    }

    override fun shouldNormalize(): Boolean {
        return false
    }
}