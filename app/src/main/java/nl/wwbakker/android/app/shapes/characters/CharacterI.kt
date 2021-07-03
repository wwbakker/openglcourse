package nl.wwbakker.android.app.shapes.characters

import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Position2D

object CharacterI : TwoLineShape() {

    override fun leftLine(): List<Position2D> {
        return listOf(
            Position2D(-0.15f, -0.5f),
            Position2D(-0.15f, 0.5f),
        )
    }

    override fun rightLine(): List<Position2D> {
        return listOf(
            Position2D(0.15f, -0.5f),
            Position2D(0.15f, 0.5f),
        )
    }

    override fun modelMatrix() : Matrix {
        return Matrix.translate(z = -2f)
                .multiply(Matrix.rotate(30f, x = 1f))
                .multiply(Matrix.rotate(30f, y = 1f))
    }
}