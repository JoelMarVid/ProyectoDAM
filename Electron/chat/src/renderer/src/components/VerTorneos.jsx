import axios from "axios";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import ChatTorneo from "./ChatTorneo";

const VerTorneos = () => {
    const userId = localStorage.getItem("userId")
    const navigate = useNavigate()
    console.log(userId)
    const [tournamentsData, setTournamentsData] = useState([])

    useEffect(() => {
        const fetchTournamentsData = async () => {
            try {
                const res = await axios.get("http://localhost:3000/auth/acceptTournament/" + userId)
                const data = res.data
                console.log(data)
                setTournamentsData(data)
            } catch (error) {
                console.log(error)
            }
        }

        fetchTournamentsData()
        console.log(tournamentsData.length)
    }, [userId])

    return (
        <div>
            {tournamentsData.length > 0 ? (
                tournamentsData.map((tournaments) => (
                    <div className="tournament">
                        <h3>{tournaments.nombre || tournaments.nombre_torneo}</h3>
                        <p>Juego: {tournaments.nombre_juego}</p>
                        <p>Fecha de inicio: {new Date(tournaments.fecha_ini).toLocaleDateString()}</p>
                        <p>Fecha de fin: {new Date(tournaments.fecha_fin).toLocaleDateString()}</p>
                        <p>Fecha del torneo: {new Date(tournaments.dia_torn).toLocaleDateString()}</p>
                        <button id="button" onClick={() => navigate(`/verParticipantes/${tournaments.torneo_id}`)}>Ver participantes</button>
                        <button id="button" onClick={() => navigate(`/emparejamiento/${tournaments.torneo_id}`)}>Emparejamiento</button>
                        <button id="button" onClick={() => navigate(`/chatTorneo/${tournaments.torneo_id}`)}>Chat</button>
                    </div>
                ))
            ) : (
                <div>
                    <p>No has aceptado ningun torneo.</p>
                </div>
            )}
            <br />
            <button id="button" onClick={() => navigate("/dashboard")}>Volver</button>
        </div>
    )

}

export default VerTorneos