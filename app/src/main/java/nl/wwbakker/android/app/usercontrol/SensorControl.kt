package nl.wwbakker.android.app.usercontrol

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.view.Surface
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import nl.wwbakker.android.app.data.Matrix
import kotlin.math.PI

class SensorControl(private val context : Context) : SensorEventListener, LifecycleEventObserver {
    private lateinit var sensorManager: SensorManager
    private lateinit var deviceSensors: MutableList<Sensor>
    private lateinit var rotationSensor : Sensor
    private val radiansToDegrees = (-180f / PI).toFloat()
    private var yaw : Float = 0f
    private var pitch : Float = 0f
    private var roll : Float = 0f

    val rotationMatrix : Matrix
        get() =
            Matrix.multiply(
                Matrix.rotate(pitch, x = -1f),
                Matrix.rotate(roll, z = -1f),
                Matrix.rotate(yaw, y = -1f),
            )

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor == rotationSensor) {
            updateRotation(event.values)
        }
    }

    private fun updateRotation(rotationVector : FloatArray) {
        val rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)
        var deviceRelativeAxisX : Int = 0
        var deviceRelativeAxisY : Int = 0
        when(context.display?.rotation) {
            Surface.ROTATION_0 -> {
                deviceRelativeAxisX = SensorManager.AXIS_X
                deviceRelativeAxisY = SensorManager.AXIS_Z
            }
            Surface.ROTATION_90 -> {
                deviceRelativeAxisX = SensorManager.AXIS_Z
                deviceRelativeAxisY = SensorManager.AXIS_MINUS_X
            }
            Surface.ROTATION_180 -> {
                deviceRelativeAxisX = SensorManager.AXIS_MINUS_X
                deviceRelativeAxisY = SensorManager.AXIS_MINUS_Z
            }
            Surface.ROTATION_270 -> {
                deviceRelativeAxisX = SensorManager.AXIS_MINUS_Z
                deviceRelativeAxisY = SensorManager.AXIS_X
            }
        }
        val adjustedRotationMatrix = FloatArray(9)
        SensorManager.remapCoordinateSystem(rotationMatrix, deviceRelativeAxisX, deviceRelativeAxisY, adjustedRotationMatrix)
        val orientation = FloatArray(3)
        SensorManager.getOrientation(adjustedRotationMatrix, orientation)
        yaw = orientation[0] * radiansToDegrees
        pitch = orientation[1] * radiansToDegrees
        roll = orientation[2] * radiansToDegrees
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when(event) {
            Lifecycle.Event.ON_CREATE -> {
                sensorManager = context.getSystemService(SensorManager::class.java)!!
                deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
                deviceSensors.forEach { s ->
                    Log.i("Sensor", "Type: ${s.name}")
                }
                rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            }
            Lifecycle.Event.ON_RESUME ->
                 sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_FASTEST)
            Lifecycle.Event.ON_PAUSE ->
                sensorManager.unregisterListener(this)
            else -> {}// do nothing
        }
    }
}