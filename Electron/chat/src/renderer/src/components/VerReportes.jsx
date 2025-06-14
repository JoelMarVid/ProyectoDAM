import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

const VerReportes = () => {
    const [reportesData, setReportesData] = useState([])
    const navigate = useNavigate()

    const fetchReportesData = async () => {
        try {
            const res = await axios.get("http://localhost:3000/auth/report")
            const data = res.data
            setReportesData(data)
        } catch (error) {
            console.log(error)
        }
    }

    useEffect(() => {
        fetchReportesData()
    }, [])

    const deleteTournament = async (id) => {
        toast.success("Reporte aceptado")
        try {
            await axios.delete(`http://localhost:3000/auth/tournaments/${id}`)
            setReportesData(reportesData.filter((reportes) => reportes.torneo_id !== id))
        } catch (error) {
            toast.error("Error al aceptar el reporte")
        }
    }

    const deleteReport = async (id) => {
        toast.success("Reporte rechazado")
        try {
            await axios.delete(`http://localhost:3000/auth/report/${id}`)
            setReportesData(reportesData.filter((reportes) => reportes.id !== id))
        } catch (error) {
            toast.error("Error al rechazar el reporte")
        }
    }


    return (
        <div>
            {reportesData.length > 0 ? (
                reportesData.map((reportes) => (
                    <div className="report">
                        <h3>{reportes.motivo}</h3>
                        <p>Usuario: {reportes.nombre_usuario}</p>
                        <p>Torneo: {reportes.torneo_id}</p>
                        <button id="button" onClick={() => deleteTournament(reportes.torneo_id)}>Aceptar</button>
                        <button id="button" onClick={() => deleteReport(reportes.id)}>Rechazar</button>
                    </div>
                ))
            ) : (
                <div>
                    <p>No hay reportes.</p>
                </div>
            )}
            <button id="button" onClick={() => navigate("/dashboard")}>Volver</button>
        </div>
    )

}

export default VerReportes
