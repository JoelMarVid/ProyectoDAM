import React, { useEffect } from "react"
import { BrowserRouter as Router, Routes, Route, useNavigate, useLocation } from "react-router-dom"
import Register from "./components/Register"
import Login from "./components/Login"
import Dashboard from "./components/Dashboard"
import Torneo from "./components/Torneos"
import CrearTorneos from "./components/CrearTorneos"

const App = () => {
  const navigate = useNavigate()
  const location = useLocation()

  useEffect(() => {
    const token = localStorage.getItem("authToken")
    if (token && location.pathname === "/") {
      navigate("/dashboard") // Redirigir al Dashboard si el token existe
    }
  }, [navigate, location])

  return (
    <Routes>
      <Route path="/" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/login" element={<Login />} />
      <Route path="/dashboard" element={<Dashboard />} />
      <Route path="/crear-torneo" element={<CrearTorneos/>}/>
      <Route path="/torneos/:name" element={<Torneo/>}/>
    </Routes>
  )
}

export default App
