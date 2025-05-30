package com.example.android.pantalla_API

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.R
import com.example.android.dialog_fragment.CreateTournamentDialogFragment
import com.example.android.iyr.PantallaPrincipal
import com.example.android.pantalla_API.adapter.GameAdapter
import com.example.android.pantalla_API.model.Game
import com.example.android.pantalla_API.model.Tournament
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class PantallaAPI : AppCompatActivity() {
    private val API_KEY = "7dAeSgsyEsfPn59lw2BHOjPzQ9Xm3M3chdiSC0r8jGEABOoe344"
    private val PANDA_URL = "https://api.pandascore.co/videogames?token=${API_KEY}"

    private lateinit var recyclerView: RecyclerView
    private lateinit var gameAdapter: GameAdapter
    private lateinit var AddTournament: FloatingActionButton
    private val gameList = mutableListOf<Game>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_api)

        recyclerView = findViewById(R.id.recyclerView)
        AddTournament = findViewById(R.id.add_tournament)
        recyclerView.layoutManager = LinearLayoutManager(this)


        AddTournament.setOnClickListener {
            val dialog = CreateTournamentDialogFragment()
            dialog.show(supportFragmentManager, "CreateTournamentDialog")
        }

        CoroutineScope(Dispatchers.Main).launch {
            val games = fetchGames()
            if (games != null){
                gameList.addAll(games)
                gameAdapter = GameAdapter(gameList)
                recyclerView.adapter = gameAdapter
            }
        }

        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("userEmail","") ?: ""

        val profileImageView = findViewById<ImageView>(R.id.profileImageView)
        val usernameTextView = findViewById<TextView>(R.id.usernameTextView)
        if (userEmail.isNotEmpty()){
            fetchUserProfile(userEmail) { nombre ->
                usernameTextView.text=nombre
            }
        }

        profileImageView.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)

            menuInflater.inflate(R.menu.profile_menu, popupMenu.menu)

            popupMenu.show()

            popupMenu.setOnMenuItemClickListener { item ->
                when(item.itemId){
                    R.id.menu_view_profile ->{
                        val intent = Intent(this, ProfileActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.menu_logout ->{
                        logout()
                        true
                    }
                    else -> false
                }
            }
        }

    }

    private fun fetchUserProfile(email: String, callback: (String) -> Unit) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:3000/auth/profile/$email")
            .build()
        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Perfil","Error al obtener perfil", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful){
                    val json= JSONObject(response.body()?.string()?: "")
                    val nombre = json.getString("nombre")
                    runOnUiThread {
                        callback(nombre)
                    }
                }
            }
        })
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