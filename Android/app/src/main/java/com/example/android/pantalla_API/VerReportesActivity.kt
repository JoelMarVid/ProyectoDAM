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
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

class VerReportesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_reportes)

        val container = findViewById<LinearLayout>(R.id.containerReportes)
        fetchReportes(container)
    }

    private fun fetchReportes(container: LinearLayout) {
        val url = "http://10.0.2.2:3000/auth/report"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@VerReportesActivity, "Error al cargar", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    container.removeAllViews()
                    if (!response.isSuccessful) {
                        val textView = TextView(this@VerReportesActivity)
                        textView.text = "No hay reportes."
                        container.addView(textView)
                        return@runOnUiThread
                    }
                    val body = response.body()?.string() ?: "[]"
                    val reportes = JSONArray(body)
                    if (reportes.length() == 0) {
                        val textView = TextView(this@VerReportesActivity)
                        textView.text = "No hay reportes."
                        container.addView(textView)
                    } else {
                        for (i in 0 until reportes.length()) {
                            val reporte = reportes.getJSONObject(i)
                            val view =
                                layoutInflater.inflate(R.layout.item_reporte, container, false)
                            view.findViewById<TextView>(R.id.textMotivo).text =
                                reporte.getString("motivo")
                            view.findViewById<TextView>(R.id.textUsuario).text =
                                "Usuario: ${reporte.getString("nombre_usuario")}"
                            view.findViewById<TextView>(R.id.textTorneo).text =
                                "Torneo: ${reporte.getInt("torneo_id")}"
                            view.findViewById<Button>(R.id.buttonAceptar).setOnClickListener {
                                eliminarTorneo(reporte.getInt("torneo_id"))
                            }
                            view.findViewById<Button>(R.id.buttonRechazar).setOnClickListener {
                                eliminarReporte(reporte.getInt("id"))
                            }
                            container.addView(view)
                        }
                    }
                }
            }
        })
    }

    private fun eliminarTorneo(torneoId: Int) {
        val url = "http://10.0.2.2:3000/auth/tournaments/$torneoId"
        val client = OkHttpClient()
        val request = Request.Builder().delete().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread { recreate() }
            }

        })
    }

    private fun eliminarReporte(reporteId: Int) {
        val url = "http://10.0.2.2:3000/auth/report/$reporteId"
        val client = OkHttpClient()
        val request = Request.Builder().delete().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                runOnUiThread { recreate() }
            }
        })
    }
}