import React, { useEffect, useState } from "react"
import { Link, useNavigate, useParams } from "react-router-dom"
import axios from "axios"
import "../assets/Torneos.css"
import { toast } from "react-toastify"

const Torneo = () => {
    const { name } = useParams()
    const navigate = useNavigate()
    const [tournamentsData, setTournamentsData] = useState([])

    const formatDate = (date) => {
        const d = new Date(date)
        d.setDate(d.getDate() + 1)
        return d.toISOString().split("T")[0]
    }

    const aceptarTournaments = async (tournaments) => {
        toast.success("Torneo aceptado exitosamente")
        const userId = localStorage.getItem("userId")
        const userName = localStorage.getItem("userName")
        try {
            axios.post("http://localhost:3000/auth/acceptTournament", {
                torneo_id: tournaments.id,
                nombre: tournaments.nombre,
                nombre_juego: tournaments.nombre_juego,
                fecha_ini: formatDate(tournaments.fecha_ini),
                fecha_fin: formatDate(tournaments.fecha_fin),
                dia_torn: formatDate(tournaments.dia_torn),
                usuario_id: userId,
                nombre_usuario: userName
            })
        } catch (error) {
            console.error(error)
            toast.error("Error al aceptar el torneo")
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
                        <button id="button" onClick={() => aceptarTournaments(tournaments)}>Aceptar</button>
                        <button id="button" onClick={() => navigate(`/report/${tournaments.id}`)}>Reportar</button>
                    </div>
                ))
            ) : (
                <div>
                    <p>No hay torneos disponibles.</p>
                </div>
            )}
            <button id="button" onClick={() => navigate("/")}>Volver</button>
        </div>
    )
}

export default Torneo