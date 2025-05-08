import axios from "axios"
import React, { useState } from "react"
import { useNavigate } from "react-router-dom"

const CrearTorneos = () => {
    const navigate = useNavigate()
    const [nombre, setNombre] = useState("")
    const [nombre_juego, setNombreJuego] = useState("")
    const [fecha_ini, setFechaIni] = useState("")
    const [fecha_fin, setFechaFin] = useState("")
    const [dia_torn, setDiaTorn] = useState("")

    const handleSubmit = async (e) => {
        e.preventDefault()
        navigate("/dashboard")
        if (!nombre || !nombre_juego || !fecha_ini || !fecha_fin || !dia_torn) {
            alert("Por favor, completa todos los campos.")
            return
        }

        if (new Date(fecha_ini) > new Date(fecha_fin)) {
            alert("La fecha de inicio no puede ser mayor que la fecha de fin.")
            return
        }

        try {
            await axios.post("http://localhost:3000/auth/tournaments", {
                nombre,
                nombre_juego,
                fecha_ini,
                fecha_fin,
                dia_torn
            })
            alert("Torneo creado exitosamente.")
        } catch (error) {
            console.error(error)
        }
    }

    return (
        <div>
            <h2>Crear Torneos</h2>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    placeholder="Nombre del torneo"
                    value={nombre}
                    onChange={(e) => setNombre(e.target.value)}
                />
                <input
                    type="text"
                    placeholder="Nombre del juego"
                    value={nombre_juego}
                    onChange={(e) => setNombreJuego(e.target.value)}
                />
                <input
                    type="date"
                    placeholder="Fecha de inicio"
                    value={fecha_ini}
                    onChange={(e) => setFechaIni(e.target.value)}
                />
                <input
                    type="date"
                    placeholder="Fecha de fin"
                    value={fecha_fin}
                    onChange={(e) => setFechaFin(e.target.value)}
                />
                <input
                    type="date"
                    placeholder="Día del torneo"
                    value={dia_torn}
                    onChange={(e) => setDiaTorn(e.target.value)}
                />
                <button id="button" type="submit">Crear Torneo</button>
            </form>
        </div>
    )
}

export default CrearTorneos