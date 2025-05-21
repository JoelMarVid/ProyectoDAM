package com.example.android.pantalla_API.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.android.R
import com.example.android.pantalla_API.ReportActivity
import com.example.android.pantalla_API.model.Tournament
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TournamentAdapter(
    private val tournaments: List<Tournament>,
    private val userId: String,
    private val userName: String
) : RecyclerView.Adapter<TournamentAdapter.TournamentViewHolder>() {
    class TournamentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvTournamentName)
        val tvDate: TextView = view.findViewById(R.id.tvTournamentDate)
        val btnAccept: Button = view.findViewById(R.id.btnTournamentAction)
        val btnReportar: Button = view.findViewById(R.id.btnReportar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TournamentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tournament, parent, false)
        return TournamentViewHolder(view)
    }

    override fun onBindViewHolder(holder: TournamentViewHolder, position: Int) {
        val tournament = tournaments[position]
        holder.tvName.text = tournament.nombre

        fun soloFecha(fecha: String): String {
            return try {
                when {
                    fecha.length == 10 -> fecha
                    fecha.contains("T") -> fecha.substring(0, 10)
                    else -> fecha
                }
            } catch (e: Exception) {
                fecha
            }
        }

        fun sumarUnDia(fecha: String): String {
            return try {
                val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = formato.parse(fecha)
                val calendar = Calendar.getInstance()
                calendar.time = date!!
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                formato.format(calendar.time)
            } catch (e: Exception) {
                fecha
            }
        }

        holder.tvDate.text =
            "Inicio de inscripciones: ${sumarUnDia(soloFecha(tournament.fecha_ini))} - Fin de inscripciones: ${sumarUnDia(soloFecha(tournament.fecha_fin))} - Dia del torneo ${sumarUnDia(soloFecha(tournament.dia_torn))}"

        holder.btnAccept.setOnClickListener {
            acceptTournament(holder.itemView.context, tournament)
        }

        holder.btnReportar.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ReportActivity::class.java)
            intent.putExtra("torneo_id", tournament.id.toString())
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = tournaments.size

    private fun acceptTournament(context: Context, tournament: Tournament) {
        val url = "http://10.0.2.2:3000/auth/acceptTournament"
        val client = OkHttpClient()

        fun soloFecha(fecha: String): String {
            return try {
                when {
                    fecha.length == 10 -> fecha
                    fecha.contains("T") -> fecha.substring(0, 10)
                    else -> fecha
                }
            } catch (e: Exception) {
                fecha
            }
        }

        fun sumarUnDia(fecha: String): String {
            return try {
                val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = formato.parse(fecha)
                val calendar = Calendar.getInstance()
                calendar.time = date!!
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                formato.format(calendar.time)
            } catch (e: Exception) {
                fecha
            }
        }

        val json = JSONObject().apply {
            put("torneo_id", tournament.id)
            put("nombre", tournament.nombre)
            put("nombre_juego", tournament.nombre_juego)
            put("fecha_ini", tournament.fecha_ini)
            put("fecha_fin", tournament.fecha_fin)
            put("dia_torn", sumarUnDia(soloFecha(tournament.dia_torn)))
            put("usuario_id", userId)
            put("nombre_usuario", userName)
        }

        val requestBody = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                (context as? Activity)?.runOnUiThread {
                    Toast.makeText(context, "Error al aceptar torneo", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                (context as? Activity)?.runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Torneo aceptado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Ya has aceptado este torneo", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        })
    }
}