import axios from "axios"
import React, { useState } from "react"
import { useNavigate } from "react-router-dom"
import { toast } from "react-toastify"

const CrearTorneos = () => {
    const navigate = useNavigate()
    const [nombre, setNombre] = useState("")
    const [nombre_juego, setNombreJuego] = useState("")
    const [fecha_ini, setFechaIni] = useState("")
    const [fecha_fin, setFechaFin] = useState("")
    const [dia_torn, setDiaTorn] = useState("")

    const handleSubmit = async (e) => {
        e.preventDefault()
        toast.success("Torneo creado exitosamente")
        navigate("/dashboard")
        if (!nombre || !nombre_juego || !fecha_ini || !fecha_fin || !dia_torn) {
            toast.error("Por favor, completa todos los campos.")
            return
        }

        if (new Date(fecha_ini) > new Date(fecha_fin)) {
            toast.error("La fecha de inicio no puede ser mayor que la fecha de fin.")
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
                <select value={nombre_juego} onChange={(e) => setNombreJuego(e.target.value)}>
                    <option value="">Selecciona un juego</option>
                    <option value="LoL">LoL</option>
                    <option value="Valorant">Valorant</option>
                    <option value="Dota 2">Dota 2</option>
                    <option value="Counter-Strike">Counter-Strike</option>
                    <option value="Mobile Legends: Bang Bang">Mobile Legends: Bang Bang</option>
                    <option value="Call of Duty">Call of Duty</option>
                    <option value="StarCraft Brood War">StarCraft Brood War</option>
                    <option value="StarCraft 2">StarCraft 2</option>
                    <option value="Lol Wild Rift">LoL Wild Rift</option>
                    <option value="King of Glory">King of Glory</option>
                    <option value="EA Sports FC">EA Sports FC</option>
                    <option value="Rainbow 6 Siege">Rainbow 6 Siege</option>
                    <option value="Rocket League">Rocket League</option>
                    <option value="PUBG">PUBG</option>
                    <option value="Overwatch">Overwatch</option>
                </select>
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