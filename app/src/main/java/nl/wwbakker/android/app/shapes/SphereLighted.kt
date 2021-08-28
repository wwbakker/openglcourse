package nl.wwbakker.android.app.shapes

import android.opengl.GLES32
import nl.wwbakker.android.app.data.*
import nl.wwbakker.android.app.shaders.PhongLightShaders
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object SphereLighted : Shape {

    private val shaders = PhongLightShaders

    override fun draw(modelViewProjection: ModelViewProjection) {
        val latitudeResolution = 32
        val longitudeResolution = 32
        val positions = Sphere.spherePositions(latitudeResolution, longitudeResolution, 1f)
        val indices = Sphere.sphereIndices(latitudeResolution, longitudeResolution)//.debugSubArray(16, 0)
        val lightColor = Vertex4(1f,1f,1f,1f)
        val lightLocation = Vertex3(3f,2f,2f)

        shaders.use()
        shaders.setPositionInput(positions)
        shaders.setColorInput(positions.singleColor(1f,0f,0f))
        shaders.setNormalInput(positions)
        shaders.setDiffuseColor(lightColor)
        shaders.setLightLocationInput(lightLocation)
        shaders.setAmbientColor(Vertex4(0.3f,0.3f,0.3f, 1f))
        shaders.setSpecularColor(lightColor)
        shaders.setMaterialShininess(10f)
        shaders.setAttenuation(Vertex3(1f, 0.35f, 0.44f))
        shaders.setModelViewPerspectiveInput(modelViewProjection.matrix)


        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indices.length, GLES32.GL_UNSIGNED_INT, indices.indexBuffer)

    }


}