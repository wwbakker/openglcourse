package nl.wwbakker.android.app.shapes.characters

import nl.wwbakker.android.app.Shape
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Position2D


object CharacterR : Shape {
    object RLeg : TwoLineShape() {
        override fun leftLine(): List<Position2D> {
            return listOf(
                Position2D(0f, 0f),
                Position2D(0.05f, -0.05f),
                Position2D(0.3f, -0.5f),
            )
        }

        override fun rightLine(): List<Position2D> {
            return listOf(
                Position2D(0.2f, 0f),
                Position2D(0.25f, -0.05f),
                Position2D(0.5f, -0.5f),
            )
        }
    }

    override fun draw(projectionMatrix: Matrix) {
        CharacterP.draw(projectionMatrix)
        RLeg.draw(projectionMatrix)
    }

}