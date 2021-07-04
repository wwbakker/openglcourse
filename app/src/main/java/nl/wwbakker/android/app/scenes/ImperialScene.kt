package nl.wwbakker.android.app.scenes

import nl.wwbakker.android.app.Shape
import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.shapes.characters.*


object ImperialScene : Shape {

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
        drawString("IMPERIAL", projectionMatrix, worldMatrix)
    }

    fun drawString(s : String, projectionMatrix: Matrix, worldMatrix: Matrix) {
        val mostLeftX = -(s.length / 2f) + 0.5f
        s.map { characterMap.getValue(it) }.withIndex().forEach {
            it.value.draw(
                projectionMatrix,
                worldMatrix =
                    worldMatrix
                        .multiply(Matrix.scale(0.2f))
                        .multiply(Matrix.translate(z = -5f, x = mostLeftX + it.index))

            )
        }
    }
}