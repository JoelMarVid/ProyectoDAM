package com.example.android.pantalla_API

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.R
import com.example.android.pantalla_API.adapter.TorneosAceptadosAdapter
import com.example.android.pantalla_API.model.Tournament
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

class TorneosAceptadosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val tournaments = mutableListOf<Tournament>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_torneos_aceptados)

        recyclerView = findViewById(R.id.recyclerViewTorneosAceptados)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TorneosAceptadosAdapter(tournaments)

        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "") ?: ""
        if (userId.isNotEmpty()){
            fetchAcceptedTournaments(userId)
        }
    }

    private fun fetchAcceptedTournaments(userId: String) {
        val url = "http://10.0.2.2:3000/auth/acceptTournament/$userId"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@TorneosAceptadosActivity, "Error al cargar torneos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful){
                    response.body()?.string()?.let { jsonString ->
                        val tournamentsList = mutableListOf<Tournament>()
                        val jsonArray = JSONArray(jsonString)
                        for (i in 0 until jsonArray.length()){
                            val obj = jsonArray.getJSONObject(i)
                            val tournament = Tournament(
                                id = obj.getInt("torneo_id"),
                                nombre = obj.getString("nombre_torneo"),
                                nombre_juego = obj.getString("nombre_juego"),
                                fecha_ini = obj.getString("fecha_ini"),
                                fecha_fin = obj.getString("fecha_fin"),
                                dia_torn = obj.getString("dia_torn")
                            )
                            tournamentsList.add(tournament)
                        }
                        runOnUiThread {
                            tournaments.clear()
                            tournaments.addAll(tournamentsList)
                            recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }

        })
    }
}