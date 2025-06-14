import React, { useEffect } from "react"
import { BrowserRouter as Router, Routes, Route, useNavigate, useLocation } from "react-router-dom"
import { ToastContainer } from "react-toastify"
import Register from "./components/Register"
import Login from "./components/Login"
import Dashboard from "./components/Dashboard"
import Torneo from "./components/Torneos"
import CrearTorneos from "./components/CrearTorneos"
import VerTorneos from "./components/VerTorneos"
import VerPerfil from "./components/VerPerfil"
import Reportes from "./components/Reportes"
import VerReportes from "./components/VerReportes"
import VerParticipante from "./components/verParticipante"
import CalendarioTorneos from "./components/CalendarioTorneos"
import Emparejamiento from "./components/Emparejamiento"
import ChatTorneo from "./components/ChatTorneo"

const App = () => {
  const navigate = useNavigate()
  const location = useLocation()
  const userId = localStorage.getItem("userId")
  const userName = localStorage.getItem("userName")

  useEffect(() => {
    const token = localStorage.getItem("authToken")
    if (token && location.pathname === "/") {
      navigate("/dashboard") // Redirigir al Dashboard si el token existe
    }
  }, [navigate, location])

  return (
    <>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/verPerfil" element={<VerPerfil />} />
        <Route path="/crear-torneo" element={<CrearTorneos />} />
        <Route path="/verTorneos" element={<VerTorneos />} />
        <Route path="/torneos/:name" element={<Torneo />} />
        <Route path="/report/:torneo_id" element={<Reportes />} />
        <Route path="/verReportes" element={<VerReportes />} />
        <Route path="/verParticipantes/:id" element={<VerParticipante />} />
        <Route path="/Calendario" element={<CalendarioTorneos />} />
        <Route path="/emparejamiento/:id" element={<Emparejamiento />} />
        <Route path="/chatTorneo/:torneo_id" element={<ChatTorneo usuario_id={userId} nombre_usuario={userName} />} />
      </Routes>
      <ToastContainer position="top-right" autoClose={3000}/>
    </>
  )
}

export default App
