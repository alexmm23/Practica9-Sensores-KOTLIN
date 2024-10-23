package com.example.sensores

import android.annotation.SuppressLint
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var detalle: TextView
    private lateinit var sensorManager: SensorManager
    private lateinit var sensor: Sensor
    private var existeSensorProximidad: Boolean = false
    private lateinit var listadoSensores: List<Sensor>
    private lateinit var imageView: ImageView
    private var lightSensor: Sensor? = null
    private var accelerometer: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        detalle = findViewById(R.id.textView)
        imageView = findViewById(R.id.imageView)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    @SuppressLint("SetTextI18n")
    fun click(view: View) {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        listadoSensores = sensorManager.getSensorList(Sensor.TYPE_ALL)
        detalle.setBackgroundColor(Color.WHITE)
        detalle.text = "Listado de sensores del dispositivo"
        for (sensor in listadoSensores) {
            detalle.text = "${detalle.text}\n Nombre: ${sensor.name}\n Version: ${sensor.version}"
        }
    }

    @SuppressLint("SetTextI18n")
    fun clickMagnetico(view: View) {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            Toast.makeText(applicationContext, "El dispositivo tiene sensor magnetico", Toast.LENGTH_SHORT).show()
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!!
            detalle.setBackgroundColor(Color.GRAY)
            detalle.text = "Propiedades del sensor magnetico: \nNombre: ${sensor.name}" + "\nVersion: ${sensor.version}\nFabricantes: ${sensor.vendor}"
        } else {
            Toast.makeText(applicationContext, "El dispositivo no cuenta con sensor magnetico", Toast.LENGTH_SHORT).show()
        }
    }

    fun clickProximidad(view: View) {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            val promiximidadSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
            existeSensorProximidad = true
            detalle.text = "El dispositivo tiene sensor: ${promiximidadSensor!!.name}"
            detalle.setBackgroundColor(Color.GREEN)
            sensorManager.registerListener(this, promiximidadSensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            detalle.text = "No se cuenta con sensor de proximidad"
            existeSensorProximidad = false
        }
    }

    fun clickLuz(view: View) {
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
            detalle.text = "Sensor de luz activado"
        } else {
            detalle.text = "No se cuenta con sensor de luz"
        }
    }

    fun clickAcelerometro(view: View) {
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            detalle.text = "Acelerómetro activado"
        } else {
            detalle.text = "No se cuenta con acelerómetro"
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val valorCambio: Float
        if (event?.sensor?.type == Sensor.TYPE_PROXIMITY && existeSensorProximidad) {
            valorCambio = event.values[0]
            if (valorCambio < 1.0) {
                detalle.textSize = 30f
                detalle.setBackgroundColor(Color.BLUE)
                detalle.setTextColor(Color.WHITE)
                detalle.text = "\nCERCA $valorCambio"
            } else {
                detalle.textSize = 14f
                detalle.setBackgroundColor(Color.GREEN)
                detalle.setTextColor(Color.BLACK)
            }
        } else if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            valorCambio = event.values[0]
            Log.d("SENSOR", "Valor: $valorCambio")
            if (valorCambio < 120) {
                imageView.setImageResource(R.drawable.ic_launcher_foreground)
            } else {
                imageView.setImageResource(R.drawable.ic_launcher_background)
            }
        } else if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            //desactivar el sensor de proximidad
            //sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY))
            valorCambio = event.values[0]
            if (valorCambio > 3) {
                imageView.setImageResource(R.drawable.ic_launcher_foreground)
            } else {
                imageView.setImageResource(R.drawable.ic_launcher_background)
            }
        } else {
            Toast.makeText(applicationContext, "Sin cambios", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not yet implemented
    }
}