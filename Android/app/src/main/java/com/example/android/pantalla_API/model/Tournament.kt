package com.example.android.pantalla_API.model

import java.sql.Date

data class Tournament(
    val id: Int,
    val nombre: String,
    val nombre_juego:String,
    val fecha_ini: String,
    val fecha_fin: String,
    val dia_torn: String
)
