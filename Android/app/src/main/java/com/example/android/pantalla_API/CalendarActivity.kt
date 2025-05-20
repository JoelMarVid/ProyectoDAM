package com.example.android.pantalla_API

import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.android.R
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity() {
    private lateinit var calendarView: CalendarView
    private lateinit var detailsTextView: TextView
    private val tournamentsByDate = mutableMapOf<String, MutableList<String>>() // yyyy-MM-dd -> [detalles]
    private var lastSelectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        calendarView = findViewById(R.id.calendarView)
        detailsTextView = findViewById(R.id.detailsTextView)

        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "") ?: ""

        fetchTournaments(userId)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val dateStr = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            lastSelectedDate = dateStr
            showTournamentsForDate(dateStr)
            Log.d("CalendarActivity", "DÍA SELECCIONADO: $dateStr")
        }
    }

    private fun showTournamentsForDate(dateStr: String) {
        val details = tournamentsByDate[dateStr]
        if (details != null && details.isNotEmpty()) {
            detailsTextView.text = details.joinToString("\n\n")
        } else {
            detailsTextView.text = "No hay torneos este día"
        }
    }

    private fun fetchTournaments(userId: String) {
        val url = "http://10.0.2.2:3000/auth/acceptTournament/$userId"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("CalendarActivity", "Error de red: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body()?.string()?.let { jsonString ->
                        val jsonArray = JSONArray(jsonString)
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val nombre = obj.optString("nombre_torneo", obj.optString("nombre", ""))
                            val diaTorn = obj.getString("dia_torn")
                            val nombreJuego = obj.getString("nombre_juego")
                            val detalle = "$nombre\nJuego: $nombreJuego\nDía: $diaTorn"

                            // Parseo robusto de fecha ISO a yyyy-MM-dd
                            val diaTornNormalizado = parseIsoToYMD(diaTorn)
                            if (diaTornNormalizado != null) {
                                tournamentsByDate.getOrPut(diaTornNormalizado) { mutableListOf() }.add(detalle)
                            } else {
                                Log.e("CalendarActivity", "Fecha inválida: $diaTorn")
                            }
                        }
                        Log.d("CalendarActivity", "MAPA DE TORNEOS: $tournamentsByDate")
                        runOnUiThread {
                            lastSelectedDate?.let { showTournamentsForDate(it) }
                        }
                    }
                }
            }
        })
    }

    private fun parseIsoToYMD(iso: String): String? {
        // Intenta parsear como ISO 8601
        return try {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoFormat.timeZone = TimeZone.getTimeZone("UTC")
            val dateObj = isoFormat.parse(iso)
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            outputFormat.format(dateObj!!)
        } catch (e: Exception) {
            // Si falla, intenta parsear como yyyy-MM-dd
            try {
                val simpleFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateObj = simpleFormat.parse(iso)
                simpleFormat.format(dateObj!!)
            } catch (e2: Exception) {
                null
            }
        }
    }
}