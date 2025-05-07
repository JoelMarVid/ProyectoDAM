import { useParams } from "react-router-dom";
import axios from "axios";
import React, {useEffect, useState} from "react";

const VerParticipante = () => {
    const { id } = useParams()
    const [tournamentsData, setTournamentsData] = useState([])
    console.log(id)
    useEffect(() => {
        const fetchTournamentsData = async () => {
            try {
                const res = await axios.get("http://localhost:3000/auth/tournamentuser/" + id);
                console.log("Datos recibidos:", res.data); // Log para verificar los datos
                setTournamentsData(res.data);
            } catch (error) {
                console.error("Error al obtener los datos:", error); // Log para errores
            }
        };
    
        fetchTournamentsData();
    }, [id]);

    console.log(tournamentsData)
    return (
        <div>
            {tournamentsData.length > 0 ? (
                tournamentsData.map((tournaments) => (
                    <div className="tournament">
                        <p>{tournaments.nombre_usuario}</p>
                    </div>
                ))
            ) : (
                <div>
                    <p>No hay participantes.</p>
                </div>
            )}
        </div>
    )
}

export default VerParticipante