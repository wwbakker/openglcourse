package nl.wwbakker.android.app.shapes.characters

import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Position2D

object CharacterM : TwoLineShape() {

    override fun leftLine(): List<Position2D> {
        return listOf(
            Position2D(-1f, -1f), //1
            Position2D(-0.8f, 1f), //2
            Position2D(-0.2f, 1f), //3
            Position2D(0f, -0.3f), //4
            Position2D(0f, -0.3f), //5
            Position2D(0.2f, 1f), //6
            Position2D(0.8f, 1f), //7
            Position2D(1f, -1f), //8
        )
    }

    override fun rightLine(): List<Position2D> {
        return listOf(
            Position2D(-0.6f, -1f),
            Position2D(-0.5f, 0f),
            Position2D(-0.5f, 0f),
            Position2D(-0.2f, -1f),
            Position2D(0.2f, -1f),
            Position2D(0.5f, 0f),
            Position2D(0.5f, 0f),
            Position2D(0.6f, -1f),
        )
    }

    override fun modelMatrix() : Matrix {
        return Matrix.translate(z = -2f)
                .multiply(Matrix.rotate(30f, x = 1f))
                .multiply(Matrix.rotate(30f, y = 1f))
    }
}