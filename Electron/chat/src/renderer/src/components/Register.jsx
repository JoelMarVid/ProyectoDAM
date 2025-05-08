import React, { useState } from "react"
import axios from "axios"
import { useNavigate } from "react-router-dom"
import "../assets/styles.css"

const Register = () => {
    const [nombre, setNombre] = useState("")
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [message, setMessage] = useState("")
    const navigate = useNavigate()

    const handleSubmit = async (e) => {
        e.preventDefault()
        try {
            const res = await axios.post("http://localhost:3000/auth/register", {
                nombre,
                email,
                password
            })
            setMessage(res.data.message)
            setTimeout(() => navigate("/login"), 2000) 
        } catch (error) {
            setMessage(error.response.data.error || "Error en el registro")
        }
    }
    return (
        <div className="form-container">
            <h2>Registro</h2>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    placeholder="Nombre"
                    value={nombre}
                    onChange={(e) => setNombre(e.target.value)}
                />
                <input
                    type="email"
                    placeholder="Correo"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="Contraseña"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <button id="button" type="submit">Registrar</button>
            </form>
            <p>{message}</p>
        </div>
    )
}

export default Register