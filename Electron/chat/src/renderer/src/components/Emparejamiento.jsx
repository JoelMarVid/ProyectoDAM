import React, { useEffect, useState } from "react"
import axios from "axios"
import { useNavigate, useParams } from "react-router-dom"

const Emparejamiento = () => {
    const { id } = useParams()
    const navigate = useNavigate()
    const [participantes, setParticipantes] = useState([])
    const [emparejamientos, setEmparejamientos] = useState([])

    useEffect(() => {
        const fetchParticipantes = async () => {
            try {
                const participantesRes = await axios.get("http://localhost:3000/auth/tournamentuser/" + id)
                setParticipantes(participantesRes.data)

                const emparejamientosRes = await axios.get(`http://localhost:3000/auth/emparejamientos/${id}`)
                setEmparejamientos(emparejamientosRes.data)
            } catch (error) {
                console.error("Error al obtener los participantes:", error)
            }
        }
        fetchParticipantes()
    }, [id])

    const realizarEmparejamiento = async () => {
        const participantesRestantes = [...participantes]
        const nuevosEmparejamientos = []

        while (participantesRestantes.length > 1) {
            const index1 = Math.floor(Math.random() * participantesRestantes.length)
            const participante1 = participantesRestantes.splice(index1, 1)[0]

            const index2 = Math.floor(Math.random() * participantesRestantes.length)
            const participante2 = participantesRestantes.splice(index2, 1)[0]

            nuevosEmparejamientos.push({
                torneo_id: id,
                participante1_id: participante1.usuario_id,
                participante2_id: participante2.usuario_id,
            })
        }

        if (participantesRestantes.length === 1) {
            alert(`El participante ${participantesRestantes[0].nombre_usuario} quedó sin pareja.`)
        }

        await axios.post(`http://localhost:3000/auth/emparejamientos/${id}`, nuevosEmparejamientos)
        setEmparejamientos(nuevosEmparejamientos)
    }

    return (
        <div>
            <h2>Emparejamiento de Participantes</h2>
            <button onClick={realizarEmparejamiento}>Realizar Emparejamiento</button>
            <div>
                {emparejamientos.map((pareja, index) => (
                    <div key={index}>
                        <p>Pareja {index + 1}:</p>
                        <p>{pareja.participante1_nombre} vs {pareja.participante2_nombre}</p>
                    </div>
                ))}
            </div>
            <button id="button" onClick={() => navigate("/dashboard")}>Volver</button>
        </div>
    )
}
export default Emparejamiento