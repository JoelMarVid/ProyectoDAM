import React, { useEffect, useState } from "react"
import { Link, useNavigate, useParams } from "react-router-dom"
import axios from "axios"
import "../assets/Torneos.css"

const Torneo = () => {
    const { name } = useParams()
    const navigate = useNavigate()
    const [tournamentsData, setTournamentsData] = useState([])

    const aceptarTournaments = async (tournaments) => {
        const userId = localStorage.getItem("userId")
        try {
            axios.post("http://localhost:3000/auth/acceptTournament", {
                torneo_id: tournaments.id,
                nombre: tournaments.nombre,
                nombre_juego: tournaments.nombre_juego,
                fecha_ini: tournaments.fecha_ini,
                fecha_fin: tournaments.fecha_fin,
                dia_torn: tournaments.dia_torn,
                usuario_id: userId
            })
            alert("Torneo aceptado")
        } catch (error) {
            console.error(error)
            alert("Error al aceptar el torneo")
        }
    }

    useEffect(() => {
        const fetchTournamentsData = async () => {
            try {
                const res = await axios.get(`http://localhost:3000/auth/tournaments/${name}`)
                const data = res.data
                setTournamentsData(data)
            } catch (error) {
                console.log(error)
            }
        }

        fetchTournamentsData()
    }, [name])


    return (
        <div className="tournament-container">
            {tournamentsData.length > 0 ? (
                tournamentsData.map((tournaments) => (
                    <div className="tournament">
                        <h3>{tournaments.nombre}</h3>
                        <p>Juego: {tournaments.nombre_juego}</p>
                        <p>Fecha de inicio: {new Date(tournaments.fecha_ini).toLocaleDateString()}</p>
                        <p>Fecha de fin: {new Date(tournaments.fecha_fin).toLocaleDateString()}</p>
                        <p>Fecha del torneo: {new Date(tournaments.dia_torn).toLocaleDateString()}</p>
                        <button onClick={() => aceptarTournaments(tournaments)}>Aceptar</button>
                    </div>
                ))
            ) : (
                <div>
                    <p>No hay torneos disponibles.</p>
                </div>
            )}
            <button onClick={() => navigate("/")}>Volver</button>
        </div>
    )
}

export default Torneo