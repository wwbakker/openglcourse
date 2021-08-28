package nl.wwbakker.android.app.shapes.characters

import nl.wwbakker.android.app.data.ModelViewProjection
import nl.wwbakker.android.app.data.Position2D

object CharacterA : TwoLineShape() {

    object ADash : TwoLineShape() {
        override fun leftLine(): List<Position2D> {
            return listOf(
                Position2D(-0.15f, -0.2f),
                Position2D(-0.1f, -0.05f),
            )
        }

        override fun rightLine(): List<Position2D> {
            return listOf(
                Position2D(0.15f,-0.2f),
                Position2D(0.1f,-0.05f),
            )
        }
    }

    override fun leftLine(): List<Position2D> {
        return listOf(
            Position2D(-0.4f, -0.5f),
            Position2D(-0.1f, 0.5f),
            Position2D(0.1f, 0.5f),
            Position2D(0.4f, -0.5f),
        )
    }

    override fun rightLine(): List<Position2D> {
        return listOf(
            Position2D(-0.25f,-0.5f),
            Position2D(0f,0.3f),
            Position2D(0f,0.3f),
            Position2D(0.25f,-0.5f),
        )
    }

    override fun draw(modelViewProjection: ModelViewProjection) {
        super.draw(modelViewProjection)
        ADash.draw(modelViewProjection)
    }
}