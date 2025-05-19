package com.example.android.pantalla_API

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.android.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ReportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val motivoEditText = findViewById<EditText>(R.id.editTextMotivo)
        val enviarButton = findViewById<Button>(R.id.buttonEnviarReporte)
        val torneoId = intent.getStringExtra("torneo_id") ?: ""

        enviarButton.setOnClickListener {
            val motivo = motivoEditText.text.toString()
            val userName = getSharedPreferences("AppPreferences", MODE_PRIVATE).getString("userName", "") ?: ""
            if (motivo.isNotEmpty() && torneoId.isNotEmpty()){
                enviarReporte(torneoId, userName, motivo)
            }else{
                Toast.makeText(this, "Completa el motivo", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun enviarReporte(torneoId: String, userName: String, motivo: String) {
        val url = "http://10.0.2.2:3000/auth/report/$torneoId"
        val client = OkHttpClient()
        val json = JSONObject().apply {
            put("usuario_name", userName)
            put("motivo", motivo)
        }

        val requestBody = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )

        val request= Request.Builder().url(url).post(requestBody).build()
        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ReportActivity, "Error al enviar el reporte", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful){
                        Toast.makeText(this@ReportActivity, "Reporte enviado", Toast.LENGTH_SHORT).show()
                        finish()
                    }else{
                        Toast.makeText(this@ReportActivity, "Ya has reportado este torneo", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}