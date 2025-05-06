import axios from "axios";
import React, { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

const Reportes = () => {
    const [motivo, setMotivo] = useState("")
    const navigate = useNavigate()
    const { torneo_id } = useParams()
    console.log(torneo_id)
    const handleSubmit = async (e) => {
        e.preventDefault()
        navigate("/dashboard")
        try {
            await axios.post(`http://localhost:3000/auth/report/${torneo_id}`, {
                usuario_name: localStorage.getItem("userName"),
                motivo: motivo
            })
           
        } catch (error) {
            console.error(error)
            alert("Error al enviar el reporte")
        }
        alert("Reporte enviado exitosamente.")
    }

    return (
        <div className="report-form">
            <h2>Enviar Reporte</h2>
            <form onSubmit={handleSubmit}>
                <textarea
                    placeholder="Motivo del reporte"
                    value={motivo}
                    onChange={(e) => setMotivo(e.target.value)}
                />
                <button type="submit">Enviar Reporte</button>
            </form>
        </div>
    )
}

export default Reportes