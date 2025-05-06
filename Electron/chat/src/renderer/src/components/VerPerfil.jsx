import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

const VerPerfil = () => {
    const nameUser = localStorage.getItem("userName")
    const roleUser = localStorage.getItem("userRole")
    const navigate = useNavigate()

    return (
        <div>
            <h1>{nameUser}</h1>
            <button onClick={() => navigate("/verTorneos")}>Ver torneos</button>
            {roleUser === "admin" && <button onClick={() => navigate("/VerReportes")}>Reportes</button>}
            <button onClick={() => navigate("/dashboard")}>Volver</button>
        </div>
    )
}

export default VerPerfil