package nl.wwbakker.android.app.shapes.characters

import nl.wwbakker.android.app.Shape
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Position2D


object CharacterE : Shape {
    object Dash : TwoLineShape() {
        override fun leftLine(): List<Position2D> {
            return listOf(
                Position2D(-0.1f, -0.1f),
                Position2D(-0.1f, 0.1f),
            )
        }

        override fun rightLine(): List<Position2D> {
            return listOf(
                Position2D(0.2f, -0.1f),
                Position2D(0.2f, 0.1f),
            )
        }

        override fun shouldNormalize(): Boolean {
            return false
        }
    }

    override fun draw(projectionMatrix: Matrix) {
        CharacterC.draw(projectionMatrix)
        Dash.draw(projectionMatrix)
    }

}