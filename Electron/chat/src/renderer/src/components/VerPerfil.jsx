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
            <button onClick={() => navigate("/dashboard")}>Volver</button>
            {roleUser === "admin" && <button onClick={() => alert("Eres administrador")}>Panel de Administrador</button>}
        </div>
    )
}

export default VerPerfil