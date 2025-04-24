package com.example.android.iyr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android.R
import com.example.android.pantalla_API.PantallaAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class PantallaPrincipal : AppCompatActivity() {
    private val SERVER_URL = "http://10.0.2.2:3000/auth"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn){
            val intent = Intent(this, PantallaAPI::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_pantalla_principal)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerLink = findViewById<TextView>(R.id.registerLink)

        // Navegar a la pantalla de Registro al hacer clic en el enlace
        registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

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

    private suspend fun loginUser(email: String, password: String) {
        val url = "$SERVER_URL/login"
        val json = JSONObject()
        json.put("email", email)
        json.put("password", password)


        val response = sendPostRequest(url, json)
        withContext(Dispatchers.Main) {
            if (response != null) {
                val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
                val editor=sharedPreferences.edit()
                editor.putString("userEmail", email)
                editor.putBoolean("isLoggedIn", true)
                editor.apply()

                Toast.makeText(
                    this@PantallaPrincipal,
                    "Inicio de sesión exitoso",
                    Toast.LENGTH_SHORT
                ).show()

                val intent= Intent(this@PantallaPrincipal, PantallaAPI::class.java)
                startActivity(intent)
                finish()
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

                OutputStreamWriter(connection.outputStream).use { it.write(json.toString()) }

                if (connection.responseCode == 200) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}