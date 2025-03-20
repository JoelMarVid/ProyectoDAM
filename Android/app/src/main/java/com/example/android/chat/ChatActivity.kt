package com.example.android.chat

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.android.R
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class ChatActivity : AppCompatActivity() {
    private lateinit var socket: Socket
    private lateinit var editTextMensaje: EditText
    private lateinit var buttonEnviar: Button
    private lateinit var textViewChat: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        editTextMensaje = findViewById(R.id.editTextMensaje)
        buttonEnviar = findViewById(R.id.buttonEnviar)
        textViewChat = findViewById(R.id.textViewChat)

        conectarSocket()

        buttonEnviar.setOnClickListener {
            val mensaje = editTextMensaje.text.toString().trim()
            if (mensaje.isNotEmpty()) {
                enviarMensaje("UsuarioAndroid", mensaje)
                editTextMensaje.text.clear()
            }
        }
    }

    private fun conectarSocket() {
        try {
            socket = IO.socket("http://10.0.2.2:3000")
            socket.connect()

            socket.on(Socket.EVENT_CONNECT) {
                Log.d("ChatActivity", "Conectado al servidor Socket.IO")
            }

            socket.on("mensajeChat") {args ->
                val mensaje = args[0] as JSONObject
                val texto = "${mensaje.getString("usuario")}: ${mensaje.getString("contenido")}"
                Log.d("ChatActivity", "Mensaje recibido: $texto")
                runOnUiThread {
                    textViewChat.append("\n$texto")
                }
            }

            socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
                Log.e("ChatActivity", "Error de conexión: ${args[0]}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun enviarMensaje(usuario: String, mensaje: String) {
        val jsonMensaje = JSONObject()
        jsonMensaje.put("usuario", usuario)
        jsonMensaje.put("contenido", mensaje)
        socket.emit("mensajeChat", jsonMensaje)
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
    }

}

