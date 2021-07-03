package nl.wwbakker.android.app.shapes.characters

import nl.wwbakker.android.app.data.BezierCurve
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Position2D

class CharacterP : Character3D() {

    override fun leftLine(): List<Position2D> {
        return listOf(
            Position2D(-0.3f, -1f),
            Position2D(-0.3f, 1f),
        ) +
        BezierCurve(listOf(
            Position2D(-0.3f, 1f),
            Position2D(1f,1f),
            Position2D(1f,0f ),
            Position2D(0f, 0f),
        )).vertices(10, addFinalVertex = true)
    }

    override fun rightLine(): List<Position2D> {
        return listOf(
            Position2D(0f,-1f),
            Position2D(0f,0.7f),
        ) +
        BezierCurve(listOf(
            Position2D(0f,0.7f),
            Position2D(0.6f,0.7f),
            Position2D(0.6f,0.3f),
            Position2D(0f,0.3f),
        )).vertices(10, addFinalVertex = true)
    }
    override fun modelMatrix() : Matrix {
        return Matrix.translate(z = -2f)
                .multiply(Matrix.rotate(30f, x = 1f))
                .multiply(Matrix.rotate(30f, y = 1f))
    }

}