package com.example.android.pantalla_API.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.R
import com.example.android.pantalla_API.TournamentsActivity
import com.example.android.pantalla_API.model.Game

class GameAdapter(private val games:List<Game>) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    class GameViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val gameImage : ImageView = view.findViewById(R.id.gameImage)
        val gameTitle : TextView = view.findViewById(R.id.gameTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_game_adapter, parent,false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.gameTitle.text = game.name
        Glide.with(holder.itemView.context).load(game.imageUrl).into(holder.gameImage)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TournamentsActivity::class.java)
            intent.putExtra("GAME_NAME", game.name)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = games.size
}