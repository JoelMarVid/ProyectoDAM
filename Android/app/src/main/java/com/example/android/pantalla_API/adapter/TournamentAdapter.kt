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

class TournamentAdapter(private val tournaments: List<Tournament>) : RecyclerView.Adapter<TournamentAdapter.TournamentViewHolder>() {
    class TournamentViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tvName: TextView = view.findViewById(R.id.tvTournamentName)
        val tvDate: TextView = view.findViewById(R.id.tvTournamentDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TournamentViewHolder {
        val  view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tournament, parent, false)
        return TournamentViewHolder(view)
    }

    override fun onBindViewHolder(holder: TournamentViewHolder, position: Int) {
        val tournament = tournaments[position]
        holder.tvName.text = tournament.nombre
        holder.tvDate.text= "Inicio de inscripciones: ${tournament.fecha_ini} - Fin de inscripciones: ${tournament.fecha_fin} - Dia del torneo ${tournament.dia_torn}"
    }

    override fun getItemCount(): Int = tournaments.size
}