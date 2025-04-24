package com.example.android.pantalla_API

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.android.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("userEmail","") ?: ""


        val usernameTextView = findViewById<TextView>(R.id.usernameTextView)
        val emailTextView = findViewById<TextView>(R.id.emailTextView)
        emailTextView.text=userEmail
        if (userEmail.isNotEmpty()){
            CoroutineScope(Dispatchers.Main).launch {
                val nombre = fetchUserProfile(userEmail)
                usernameTextView.text = nombre
            }
        }
    }
}

private suspend fun fetchUserProfile(email: String): String {
    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:3000/auth/profile/$email")
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = JSONObject(response.body()?.string() ?: "")
                return@withContext json.getString("nombre")
            } else {
                return@withContext ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext ""
        }
    }
}