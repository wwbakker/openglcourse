package nl.wwbakker.android.app.shapes

import android.opengl.GLES32
import nl.wwbakker.android.app.Shape
import nl.wwbakker.android.app.data.*
import nl.wwbakker.android.app.shaders.VertexAndMultiColorShaders


class CharacterS : Shape {

    private val shaders = VertexAndMultiColorShaders()

    val sCurve : List<Position2D> = listOf(
        BezierCurve(listOf(
            Position2D(13f,19f),
            Position2D(1f,19f),
            Position2D(1f,10f),
            Position2D(8f,10f),
        )).vertices(10, addFinalVertex = false),
        BezierCurve(listOf(
            Position2D(10f,10f),
            Position2D(11f,10f),
            Position2D(11f,6f),
            Position2D(3f,6f),
        )).vertices(10, addFinalVertex = true),
    ).flatten()

    val sCurve2 = listOf(
        BezierCurve(listOf(
            Position2D(13f,16f),
            Position2D(5f,16f),
            Position2D(5f,13f),
            Position2D(8f,13f),
        )).vertices(10, addFinalVertex = false),
        BezierCurve(listOf(
            Position2D(8f,13f),
            Position2D(15f,13f),
            Position2D(15f,3f),
            Position2D(3f,3f),
        )).vertices(10, addFinalVertex = true),
    ).flatten()


    fun sPlane(z : Float) = sCurve
        .zip(sCurve2)
        .flatMap { listOf(it.first.x, it.first.y, z, it.second.x, it.second.y, z) }
        .toFloatArray()


    val sPlaneFront = Vertices(sPlane(-0.3f), 3).normalize(x = true, y = true, z = false)
    val sPlaneBack = Vertices(sPlane(0.3f), 3).normalize(x = true, y = true, z = false)
    val positions = sPlaneFront + sPlaneBack


    val sPlaneFrontIndices = sPlaneFront.rectangleIndices()
    val sPlaneBackIndices = sPlaneBack.rectangleIndices(offset = sPlaneFront.vertexCount)
    val connectingIndices = positions.connectTwo2dPlanesIndices()//.debugSubArray(12, 12)
    val indices = sPlaneFrontIndices + sPlaneBackIndices + connectingIndices

    val colors = Vertices(/**/
        (0 until sPlaneFront.vertexCount).map { listOf(1f,1f,0f,1f) }.flatten().toFloatArray() +
                (0 until sPlaneBack.vertexCount).map { listOf(0f,0f,1f,1f) }.flatten().toFloatArray()
        , 4)


    override fun draw(projectionMatrix: Matrix) {
        shaders.setColorInput(colors)
        shaders.setModelViewPerspectiveInput(Matrix.simpleModelViewProjectionMatrix(projectionMatrix))
        shaders.setPositionInput(positions)

        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.length, GLES32.GL_UNSIGNED_INT, indices.indexBuffer)
    }

}