package com.example.android.iyr

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class PantallaPrincipal : AppCompatActivity() {
    private val SERVER_URL = "http://10.0.2.2:3000/auth"  // Cambia esto con tu servidor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal)

        val userEditText = findViewById<EditText>(R.id.userEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginButton = findViewById<Button>(R.id.loginButton)

        // Botón de Registro
        registerButton.setOnClickListener {
            val username = userEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    registerUser(username, email, password)
                }
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // Botón de Inicio de Sesión
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    loginUser(email, password)
                }
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private suspend fun registerUser(username: String, email: String, password: String) {
        val url = "$SERVER_URL/register"
        val json = JSONObject()
        json.put("nombre", username)
        json.put("email", email)
        json.put("password", password)

        val response = sendPostRequest(url, json)
        withContext(Dispatchers.Main) {
            if (response != null) {
                Toast.makeText(this@PantallaPrincipal, "Registro exitoso", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this@PantallaPrincipal, "Error en el registro", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private suspend fun loginUser(email: String, password: String) {
        val url = "$SERVER_URL/login"
        val json = JSONObject()
        json.put("email", email)
        json.put("password", password)

        val response = sendPostRequest(url, json)
        withContext(Dispatchers.Main) {
            if (response != null) {
                Toast.makeText(
                    this@PantallaPrincipal,
                    "Inicio de sesión exitoso",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@PantallaPrincipal,
                    "Error en el inicio de sesión",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private suspend fun sendPostRequest(urlString: String, json: JSONObject): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val outputStreamWriter = OutputStreamWriter(connection.outputStream)
                outputStreamWriter.write(json.toString())
                outputStreamWriter.flush()

                // Log para ver el código de respuesta
                Log.d("RegisterRequest", "Response code: ${connection.responseCode}")

                if (connection.responseCode == 200) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    // Imprimir la respuesta de error del servidor
                    val errorStream =
                        connection.errorStream?.bufferedReader()?.use { it.readText() }
                    Log.e("RegisterRequest", "Error response: $errorStream")
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}