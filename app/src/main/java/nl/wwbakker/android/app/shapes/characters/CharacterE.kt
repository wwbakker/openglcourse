package nl.wwbakker.android.app.shapes.characters

import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.Position2D
import nl.wwbakker.android.app.shapes.ShapeWithWidth


object CharacterE : ShapeWithWidth {
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
    }

    override fun draw(projectionMatrix: Matrix, worldMatrix: Matrix) {
        CharacterC.draw(projectionMatrix, worldMatrix)
        Dash.draw(projectionMatrix, worldMatrix)
    }

    override fun width(): Float {
        return CharacterC.width()
    }

}