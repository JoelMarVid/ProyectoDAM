package com.example.android.pantalla_API

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.R
import com.example.android.iyr.PantallaPrincipal
import com.example.android.pantalla_API.adapter.GameAdapter
import com.example.android.pantalla_API.model.Game
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class PantallaAPI : AppCompatActivity() {
    private val API_KEY = "7dAeSgsyEsfPn59lw2BHOjPzQ9Xm3M3chdiSC0r8jGEABOoe344"
    private val PANDA_URL = "https://api.pandascore.co/videogames?token=${API_KEY}"

    private lateinit var recyclerView: RecyclerView
    private lateinit var gameAdapter: GameAdapter
    private lateinit var logout: Button
    private val gameList = mutableListOf<Game>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_api)

        recyclerView = findViewById(R.id.recyclerView)
        logout = findViewById(R.id.cerrarSesion)
        recyclerView.layoutManager = LinearLayoutManager(this)

        logout.setOnClickListener {
            logout()
        }

        CoroutineScope(Dispatchers.Main).launch {
            val games = fetchGames()
            if (games != null){
                gameList.addAll(games)
                gameAdapter = GameAdapter(gameList)
                recyclerView.adapter = gameAdapter
            }
        }

    }

    private suspend fun fetchGames(): List<Game>? {
        return  withContext(Dispatchers.IO){
            try {
                val url = URL(PANDA_URL)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                if (connection.responseCode == 200){
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonArray = JSONArray(response)
                    val games = mutableListOf<Game>()
                    for (i in 0 until jsonArray.length()) {
                        val gameObject = jsonArray.getJSONObject(i)
                        val name = gameObject.getString("name")
                        val leaguesArray= gameObject.optJSONArray("leagues")
                        var imageUrl = ""
                        if (leaguesArray != null){
                            for (j in 0 until leaguesArray.length()){
                                val leagueObject = leaguesArray.getJSONObject(j)
                                imageUrl = leagueObject.optString("image_url","")
                                if (imageUrl.isNotEmpty()) break
                            }
                        }
                        games.add(Game(name, imageUrl))
                    }
                    games
                }else{
                    Log.e("RAW_API","Error: ${connection.responseCode}")
                    null
                }
            }catch (e: Exception){
                e.printStackTrace()
                null
            }
        }
    }

    private fun logout(){
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

        val intent = Intent(this, PantallaPrincipal::class.java)
        startActivity(intent)
        finish()
    }
}