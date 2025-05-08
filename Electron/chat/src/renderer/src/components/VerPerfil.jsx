import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import CalendarioTorneos from "./CalendarioTorneos";

const VerPerfil = () => {
    const nameUser = localStorage.getItem("userName")
    const roleUser = localStorage.getItem("userRole")
    const navigate = useNavigate()

    return (
        <div>
            <h1>{nameUser}</h1>
            <button id="button" onClick={() => navigate("/verTorneos")}>Ver torneos</button>
            <button id="button" onClick={() => navigate("/Calendario")}>Calendario</button>
            {roleUser === "admin" && <button id="button" onClick={() => navigate("/VerReportes")}>Reportes</button>}
            <button id="button" onClick={() => navigate("/dashboard")}>Volver</button>
        </div>
    )
}

export default VerPerfil