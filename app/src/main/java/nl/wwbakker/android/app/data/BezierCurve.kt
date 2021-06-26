package nl.wwbakker.android.app.data

import java.lang.IllegalArgumentException

data class Position2D(val x: Float, val y: Float)


class BezierCurve(private val controlPoints: List<Position2D>) {
    val segmentLength = controlPoints.size

    // Resulting number will be 1 more than attribute if final vertex is added.
    fun vertices(resolutionNumberOfVertices: Int, addFinalVertex: Boolean): List<Position2D> {
        val tStep = 1f / resolutionNumberOfVertices
        val vs = (0 until resolutionNumberOfVertices)
            .map { tIndex -> tIndex * tStep }
            .map { t -> appropriateBrezier(t) }
        return if (addFinalVertex)
            vs + controlPoints.last()
        else
            vs
    }

    private fun appropriateBrezier(t: Float): Position2D =
        Position2D(
            x = appropriateBrezier(t, controlPoints.map { it.x }),
            y = appropriateBrezier(t, controlPoints.map { it.y })
        )

    private fun appropriateBrezier(t: Float, ps: List<Float>): Float =
        when (segmentLength) {
            2 -> linear(t, ps.component1(), ps.component2())
            3 -> quadratic(t, ps.component1(), ps.component2(), ps.component3())
            4 -> cubic(t, ps.component1(), ps.component2(), ps.component3(), ps.component4())
            else -> throw IllegalArgumentException("Unsupported segment length")
        }

    private fun linear(t: Float, p0: Float, p1: Float): Float =
        (1 - t) * p0 + (t * p1)

    private fun quadratic(t: Float, p0: Float, p1: Float, p2: Float): Float =
        linear(
            t,
            linear(t, p0, p1),
            linear(t, p1, p2)
        )

    private fun cubic(t: Float, p0: Float, p1: Float, p2: Float, p3: Float): Float =
        linear(t, quadratic(t, p0, p1, p2), quadratic(t, p1, p2, p3))
}
