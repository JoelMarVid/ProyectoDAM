import React, { useEffect, useState } from "react"
import { Link, useNavigate, useParams } from "react-router-dom"
import axios from "axios"
import "../assets/Torneos.css"

const Torneo = () => {
    const { name } = useParams()
    const navigate = useNavigate()
    const [tournamentsData, setTournamentsData] = useState([])

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
                        <p>Fecha de inicio: {tournaments.fecha_ini}</p>
                        <p>Fecha de fin: {tournaments.fecha_fin}</p>
                        <p>Fecha del torneo: {tournaments.dia_torn}</p>
                        
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