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

        fetchTournamentsByGame(gameName){ list ->
            runOnUiThread {
                if (list.isNullOrEmpty()){
                    Log.e("Torneos", "Error")
                }else{
                    Log.d("MINE", "Torneos recibidos: $tournaments")
                    tournaments.addAll(list)
                    rvTournaments.adapter = TournamentAdapter(tournaments)
                }
            }
        }
    }

    private fun fetchTournamentsByGame(gameName: String, callback: (List<Tournament>) -> Unit) {
        val SERVER_URL = "http://10.0.2.2:3000/auth"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${SERVER_URL}/tournaments/$gameName")
            .build()
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.e("API", "Error al obtener torneos", e)
            }

            override fun onResponse(call: Call, response: Response) {

                if (response.isSuccessful) {
                    val body = response.body()?.string()

                    val jsonArray = JSONArray(body)
                    val tournaments = mutableListOf<Tournament>()
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        Log.i("MINE","$obj")
                        val name = obj.getString("nombre")
                        val game = obj.getString("nombre_juego")
                        val fecha_ini = obj.getString("fecha_ini")
                        val fecha_fin = obj.getString("fecha_fin")
                        val dia_torn = obj.getString("dia_torn")
                        tournaments.add(Tournament(name, game,fecha_ini,fecha_fin,dia_torn))
                    }
                    callback(tournaments)
                }
            }
        })
    }
}