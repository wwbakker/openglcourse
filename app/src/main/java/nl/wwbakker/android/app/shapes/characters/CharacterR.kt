package nl.wwbakker.android.app.shapes.characters

import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.data.ModelViewProjection
import nl.wwbakker.android.app.data.Position2D
import nl.wwbakker.android.app.shapes.ShapeWithWidth


object CharacterR : ShapeWithWidth {
    object RLeg : TwoLineShape() {
        override fun leftLine(): List<Position2D> {
            return listOf(
                Position2D(-0.1f, 0f),
                Position2D(-0.05f, -0.05f),
                Position2D(0.2f, -0.5f),
            )
        }

        override fun rightLine(): List<Position2D> {
            return listOf(
                Position2D(0.1f, 0f),
                Position2D(0.15f, -0.05f),
                Position2D(0.4f, -0.5f),
            )
        }
    }

    override fun draw(modelViewProjection: ModelViewProjection) {
        CharacterP.draw(modelViewProjection)
        RLeg.draw(modelViewProjection)
    }

    override fun width(): Float {
        return CharacterP.width()
    }

}