package com.example.android.pantalla_API

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.android.R
import com.example.android.pantalla_API.model.Participante
import com.google.api.Distribution.BucketOptions.Linear
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class EmparejamientoActivity : AppCompatActivity() {

    private val participantes = mutableListOf<Participante>()
    private val emparejamiento = mutableListOf<Map<String, Any>>()
    private lateinit var container: LinearLayout
    private var torneoId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emparejamiento)

        container = findViewById(R.id.containerEmparejamientos)
        val btnEmparejar = findViewById<Button>(R.id.btnEmparejar)

        torneoId = intent.getIntExtra("TORNEO_ID", 0)
        if (torneoId == 0) {
            Toast.makeText(this, "ID de torneo no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        btnEmparejar.setOnClickListener {
            realizarEmparejamiento()
        }

        obtenerParticipantes()
        mostrarEmparejamientos()
    }

    private fun obtenerParticipantes() {
        val url = "http://10.0.2.2:3000/auth/tournamentuser/$torneoId"
        val cliente = OkHttpClient()
        val request = Request.Builder().url(url).build()
        cliente.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@EmparejamientoActivity,
                        "Error al obtener los participantes",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body()?.string()?.let { jsonString ->
                        val jsonArray = JSONArray(jsonString)
                        participantes.clear()
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val participante = Participante(
                                usuario_id = obj.getInt("usuario_id"),
                                nombre_usuario = obj.getString("nombre_usuario")
                            )
                            participantes.add(participante)
                        }
                        runOnUiThread { mostrarParticipantes() }
                    }
                }
            }
        })
    }

    private fun mostrarEmparejamientos() {
        val listaEmparejamientos = findViewById<LinearLayout>(R.id.listaEmparejamientos)
        listaEmparejamientos.removeAllViews()
        val url = "http://10.0.2.2:3000/auth/emparejamientos/$torneoId"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@EmparejamientoActivity,
                        "Error al obtener emparejamiento",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body()?.string()?.let { jsonString ->
                        val jsonArray = JSONArray(jsonString)
                        runOnUiThread {
                            for (i in 0 until jsonArray.length()) {
                                val obj = jsonArray.getJSONObject(i)
                                val tv = TextView(this@EmparejamientoActivity)
                                val nombre1 = obj.optString("participante1_nombre", "Jugador 1")
                                val nombre2 = obj.optString("participante2_nombre", "Jugador 2")
                                tv.text = "Pareja ${i + 1}: $nombre1 vs $nombre2"
                                listaEmparejamientos.addView(tv)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun mostrarParticipantes() {
        val listaParticipantes = findViewById<LinearLayout>(R.id.listaParticipantes)
        listaParticipantes.removeAllViews()
        participantes.forEach { participante ->
            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.HORIZONTAL

            val tv = TextView(this)
            tv.text = participante.nombre_usuario
            tv.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            val btnEliminar = Button(this)
            btnEliminar.text = "Eliminar"
            btnEliminar.setOnClickListener {
                eliminarUsuario(participante.usuario_id)
            }

            layout.addView(tv)
            layout.addView(btnEliminar)
            listaParticipantes.addView(layout)
        }
    }

    private fun eliminarUsuario(usuarioId: Int) {
        val url = "http://10.0.2.2:3000/auth/eliminarJugador/$usuarioId"
        val client = OkHttpClient()
        val request = Request.Builder().delete().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@EmparejamientoActivity, "Error al eliminar el usuario", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful){
                        Toast.makeText(this@EmparejamientoActivity, "Usuario y emparejamiento correctamente", Toast.LENGTH_SHORT).show()
                        obtenerParticipantes()
                        mostrarEmparejamientos()
                    }else{
                        Toast.makeText(this@EmparejamientoActivity, "Error al eliminar el usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

    }

    private fun realizarEmparejamiento() {
        val participantesRestantes = participantes.toMutableList()
        val nuevosEmparejamientos = mutableListOf<Map<String, Any>>()

        while (participantesRestantes.size > 1) {
            val index1 = (participantesRestantes.indices).random()
            val participante1 = participantesRestantes.removeAt(index1)

            val index2 = (participantesRestantes.indices).random()
            val participante2 = participantesRestantes.removeAt(index2)

            nuevosEmparejamientos.add(
                mapOf(
                    "torneo_id" to torneoId,
                    "participante1_id" to participante1.usuario_id,
                    "participante2_id" to participante2.usuario_id
                )
            )
        }

        if (participantesRestantes.size == 1) {
            val nombre = participantesRestantes[0].nombre_usuario
            Toast.makeText(this, "El participante $nombre quedó sin pareja.", Toast.LENGTH_SHORT)
                .show()
        }

        enviarEmparejamientosAlBackend(nuevosEmparejamientos)
    }

    private fun enviarEmparejamientosAlBackend(emparejamientos: MutableList<Map<String, Any>>) {
        val url = "http://10.0.2.2:3000/auth/emparejamientos/$torneoId"
        val client = OkHttpClient()
        val jsonArray = JSONArray()
        emparejamientos.forEach { emp ->
            val obj = JSONObject()
            obj.put("torneo_id", emp["torneo_id"])
            obj.put("participante1_id", emp["participante1_id"])
            obj.put("participante2_id", emp["participante2_id"])
            jsonArray.put(obj)
        }
        val requestBody = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            jsonArray.toString()
        )
        val request = Request.Builder().url(url).post(requestBody).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@EmparejamientoActivity,
                        "Error al enviar el emparejamiento",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@EmparejamientoActivity,
                            "Emparejamiento guardados correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        mostrarEmparejamientos()
                    } else {
                        Toast.makeText(
                            this@EmparejamientoActivity,
                            "Error en el servidor",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
}