package com.example.android.pantalla_API

import android.content.Intent
import android.icu.text.CaseMap.Title
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.R
import com.example.android.pantalla_API.adapter.TournamentAdapter
import com.example.android.pantalla_API.model.Tournament
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

class TournamentsActivity : AppCompatActivity() {

    private lateinit var rvTournaments: RecyclerView
    private lateinit var tvTitle: TextView
    private val tournaments = mutableListOf<Tournament>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tournaments)

        val gameName = intent.getStringExtra("GAME_NAME") ?: ""

        tvTitle = findViewById(R.id.tvTitle)
        rvTournaments = findViewById(R.id.rvTournaments)

        tvTitle.text = "Torneos de $gameName"
        rvTournaments.layoutManager = LinearLayoutManager(this)

        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "") ?: ""
        val userName = sharedPreferences.getString("userName", "") ?: ""

        fetchTournamentsByGame(gameName) { list ->
            runOnUiThread {
                if (list.isNullOrEmpty()) {
                    Log.e("Torneos", "Error")
                } else {
                    Log.d("MINE", "Torneos recibidos: $tournaments")
                    tournaments.addAll(list)
                    rvTournaments.adapter = TournamentAdapter(tournaments, userId, userName)
                }
            }
        }
    }

    private fun fetchTournamentsByGame(gameName: String, callback: (List<Tournament>) -> Unit) {
        val SERVER_URL = "http://10.0.2.2:3000/auth/tournaments/$gameName"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(SERVER_URL)
            .build()
        Log.e("Buenos", "$request")
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.e("API", "Error al obtener torneos", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("fetchTournaments", "Respuesta no exitosa: ${response.body()}")
                    callback(emptyList())
                    return
                }

                response.body()?.string()?.let { jsonString ->
                    try {
                        val tournamentsArray = JSONArray(jsonString)
                        val tournamentsList = mutableListOf<Tournament>()

                        for (i in 0 until tournamentsArray.length()) {
                            val tournamentJson = tournamentsArray.getJSONObject(i)
                            val tournament = Tournament(
                                id = tournamentJson.getInt("id"),
                                nombre = tournamentJson.getString("nombre"),
                                nombre_juego = tournamentJson.getString("nombre_juego"),
                                fecha_ini = tournamentJson.getString("fecha_ini"),
                                fecha_fin = tournamentJson.getString("fecha_fin"),
                                dia_torn = tournamentJson.getString("dia_torn")
                            )
                            tournamentsList.add(tournament)
                        }
                        callback(tournamentsList)
                    } catch (e: Exception) {
                        Log.e("fetchTournaments", "Error parseando JSON: ${e.message}")
                        callback(emptyList())
                    }
                } ?: run {
                    Log.e("fetchTournaments", "Body nulo")
                    callback(emptyList())
                }
            }
        })
    }
}