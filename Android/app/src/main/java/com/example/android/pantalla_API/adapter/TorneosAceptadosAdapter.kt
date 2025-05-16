package com.example.android.pantalla_API.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.android.R
import com.example.android.pantalla_API.model.Tournament
import org.w3c.dom.Text

class TorneosAceptadosAdapter(private val tournaments: List<Tournament>) :
    RecyclerView.Adapter<TorneosAceptadosAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvTournamentName)
        val juego: TextView = view.findViewById(R.id.tvTournamentGame)
        val fechaIni: TextView = view.findViewById(R.id.tvTournamentFechaIni)
        val fechaFin: TextView = view.findViewById(R.id.tvTournamentFechaFin)
        val diaTorn: TextView = view.findViewById(R.id.tvTournamentDiaTorn)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_torneo_aceptado, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val torneo = tournaments[position]
        holder.nombre.text = torneo.nombre
        holder.juego.text = torneo.nombre_juego
        holder.fechaIni.text = "Inicio: ${torneo.fecha_ini}"
        holder.fechaFin.text = "Fin: ${torneo.fecha_fin}"
        holder.diaTorn.text = "Dia: ${torneo.dia_torn}"
    }

    override fun getItemCount() = tournaments.size

}