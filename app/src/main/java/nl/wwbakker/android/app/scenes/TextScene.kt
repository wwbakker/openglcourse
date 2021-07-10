package nl.wwbakker.android.app.scenes

import nl.wwbakker.android.app.data.Matrix
import nl.wwbakker.android.app.shapes.characters.*
import kotlin.random.Random


class TextScene(private val text : String) {

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

    val r = Random(System.nanoTime())
    fun randomAxis() : Triple<Float, Float, Float> {
        val r = Triple(randomN(), randomN(), randomN())
        return if (r == Triple(0f,0f,0f)) randomAxis() else r
    }
    fun randomN() : Float {
        val f = r.nextFloat()
        return when {
            f < 2f / 4f -> 0f
            f < 3f / 4f -> 1f
            else -> -1f
        }
    }

    var axis = randomAxis()

    fun worldRotation(tick: Long) : Matrix {
        if (tick % 360L == 0L) {
            axis = randomAxis()
            println(axis)
        }
        return Matrix.rotate((tick % 360).toFloat(), axis.first, axis.second, axis.third)
    }

    fun draw(projectionMatrix: Matrix, tick : Long) {
        drawString(text, projectionMatrix, worldRotation(tick))
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
                Matrix.scale(0.3f)
                    .multiply(Matrix.translate(z = -5f))
                    .multiply(worldMatrix)
                    .multiply(Matrix.translate(x = currentX + (shapeWidth / 2f)))


            )
            currentX + shapeWidth + kerning
        }
    }
}