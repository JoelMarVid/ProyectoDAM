package com.example.android.pantalla_API

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.android.R
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class ChatTorneoActivity : AppCompatActivity() {
    private lateinit var socket: Socket
    private lateinit var mensajesLayout: LinearLayout
    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_torneo)

        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val usuarioId = sharedPreferences.getString("userId", "") ?: ""
        val usuarioName = sharedPreferences.getString("userName", "") ?: ""
        val torneoId = intent.getStringExtra("torneoId")

        mensajesLayout = findViewById(R.id.mensajesLayout)
        scrollView = findViewById(R.id.scrollView)
        val inputMensaje = findViewById<EditText>(R.id.inputMensaje)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)

        socket = IO.socket("http://10.0.2.2:3000")
        socket.connect()
        socket.emit("join_tournament", JSONObject().apply {
            put("torneo_id", torneoId)
            put("usuario_id", usuarioId)
            put("nombre_usuario", usuarioName)
        })

        socket.on("chat_message") { args ->
            val data = args[0] as JSONObject
            if (data.has("mensaje") && data.has("nombre_usuario")) {
                val mensaje = data.getString("mensaje")
                val nombre = data.getString("nombre_usuario")
                runOnUiThread {
                    val tv = TextView(this)
                    tv.text = "$nombre: $mensaje"
                    mensajesLayout.addView(tv)
                    scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
                }
            }
        }

        btnEnviar.setOnClickListener {
            val mensaje = inputMensaje.text.toString()
            if (mensaje.isNotBlank()) {
                val json = JSONObject()
                json.put("torneo_id", torneoId)
                json.put("usuario_id", usuarioId)
                json.put("nombre_usuario", usuarioName)
                json.put("mensaje", mensaje)
                socket.emit("chat_message", json)
                inputMensaje.text.clear()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
    }
}