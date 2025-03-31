import React, { useState } from "react"
import { Link } from "react-router-dom"
import axios from "axios"
import "../assets/styles.css"

const Login = () => {
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [message, setMessage] = useState("")

    const handleLogin = async (e) => {
        e.preventDefault()
        try {
            const res = await axios.post("http://localhost:3000/auth/login", {
                email,
                password
            })
            setMessage(`Bienvenido, ${res.data.usuario.nombre}`)
        } catch (error) {
            setMessage(error.response.data.error || "Error en el inicio de sesión")
        }
    }

    return (
        <div className="form-container">
            <h2>Iniciar sesión</h2>
            <form onSubmit={handleLogin}>
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
                <button type="submit">Iniciar sesión</button>
            </form>
            <p>
                No tienes cuenta?<Link to="/register" className="register-link">Registrate aquí</Link>
            </p>
            <p>{message}</p>
        </div>
    )
}

export default Login