package nl.wwbakker.android.app.data

data class ModelViewProjection(
    val projectionMatrix: Matrix,
    val viewMatrix: Matrix = Matrix.simpleViewMatrix,
    val modelMatrix: Matrix = Matrix.identity(),
    val worldMatrix: Matrix = Matrix.identity(),
) {
    val matrix : Matrix = Matrix.simpleModelViewProjectionMatrix(
        projectionMatrix,
        viewMatrix,
        modelMatrix,
        worldMatrix,
    )
}