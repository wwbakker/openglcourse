package nl.wwbakker.android.app.scenes

import nl.wwbakker.android.app.shapes.Shape
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.shapes.ShapeWithWidth
import nl.wwbakker.android.app.shapes.characters.*


class TextScene(private val text : String) : Shape {

    private val characterMap = mapOf(
        'A' to CharacterA,
        'C' to CharacterC,
        'E' to CharacterE,
        'I' to CharacterI,
        'L' to CharacterL,
        'M' to CharacterM,
        'P' to CharacterP,
        'R' to CharacterR,
        'S' to CharacterS,
        'V' to CharacterV,
    )

    override fun draw(projectionMatrix: Matrix, worldMatrix: Matrix) {
        drawString(text, projectionMatrix, worldMatrix)
    }

    private fun drawString(s : String, projectionMatrix: Matrix, worldMatrix: Matrix) {
        val characterShapes = s.map { characterMap.getValue(it) }
        val kerning = 0.1f
        val totalWidth =
            characterShapes.fold(0f) { acc, shape -> acc + shape.width() } +
                    ((s.length - 1) * kerning)
        val mostLeftX : Float = -totalWidth / 2f

        characterShapes.fold(mostLeftX) { currentX, shape ->
            val shapeWidth = shape.width()
            shape.draw(
                projectionMatrix,
                worldMatrix =
                    worldMatrix
                        .multiply(Matrix.scale(0.3f))
                        .multiply(Matrix.translate(z = -5f, x = currentX + (shapeWidth / 2f)))

            )
            currentX + shapeWidth + kerning
        }
    }
}