package nl.wwbakker.android.app.data

data class Position2D(val x: Float, val y: Float)

fun List<Position2D>.normalize() : List<Position2D> {
    val minX = this.map { it.x }.minByOrNull{it}!!
    val maxX = this.map { it.x }.maxByOrNull{it}!!
    val minY = this.map { it.y }.minByOrNull{it}!!
    val maxY = this.map { it.y }.maxByOrNull{it}!!

    val scalingFactor = 1f / Math.max(maxX - minX, maxY - minY)
    return this.map { Position2D(
        x = it.x * scalingFactor,
        y = it.y * scalingFactor) }
}
fun List<Position2D>.center() : List<Position2D> {
    val minX = this.map { it.x }.minByOrNull{it}!!
    val maxX = this.map { it.x }.maxByOrNull{it}!!
    val minY = this.map { it.y }.minByOrNull{it}!!
    val maxY = this.map { it.y }.maxByOrNull{it}!!
    val centerOffsetX = (maxX + minX) / 2f
    val centerOffsetY = (maxY + minY) / 2f

    return this.map { Position2D(
        x = it.x - centerOffsetX,
        y = it.y - centerOffsetY) }
}