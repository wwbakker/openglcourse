package nl.wwbakker.android.app.shapes.characters

import android.opengl.GLES32
import nl.wwbakker.android.app.data.*
import nl.wwbakker.android.app.shaders.VertexAndMultiColorShaders
import nl.wwbakker.android.app.shapes.ShapeWithWidth

object CharacterV : ShapeWithWidth {

    private val shaders = VertexAndMultiColorShaders

    val vPlane2d = listOf(
        Position2D(-2f, 2f),
        Position2D(-1f, 2f),
        Position2D(0f, -2f),
        Position2D(0f, 0f),
        Position2D(2f, 2f),
        Position2D(1f, 2f),
    ).normalize()


    fun vPlane(z : Float) = vPlane2d
        .flatMap { listOf(it.x, it.y, z) }
        .toFloatArray()


    val vPlaneFront = Vertices(vPlane(-0.3f), 3)
    val vPlaneBack = Vertices(vPlane(0.3f), 3)
    val positions = vPlaneFront + vPlaneBack


    val sPlaneFrontIndices = vPlaneFront.rectangleIndices()
    val sPlaneBackIndices = vPlaneBack.rectangleIndices(offset = vPlaneFront.vertexCount)
    val connectingIndices = positions.connectTwo2dPlanesIndices()
    val indices = sPlaneFrontIndices + sPlaneBackIndices + connectingIndices

    val colors = Vertices(/**/
        (0 until vPlaneFront.vertexCount).map { listOf(0f,0f,1f,1f) }.flatten().toFloatArray() +
                (0 until vPlaneBack.vertexCount).map { listOf(0f,1f,1f,1f) }.flatten().toFloatArray()
        , 4)

    val modelMatrix =
        Matrix.rotate(0f, x = 1f)
            .multiply(Matrix.rotate(-30f, y = 1f))
            .multiply(Matrix.translate(z = -1f))

    override fun width(): Float {
        return 1f
    }

    override fun draw(modelViewProjection: ModelViewProjection) {
        shaders.setColorInput(colors)
        shaders.setModelViewPerspectiveInput(
            modelViewProjection.copy(modelMatrix = modelMatrix).matrix)
        shaders.setPositionInput(positions)

        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.length, GLES32.GL_UNSIGNED_INT, indices.indexBuffer)
    }

}