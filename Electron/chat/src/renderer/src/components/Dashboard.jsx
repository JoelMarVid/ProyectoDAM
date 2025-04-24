import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../assets/Dashboard.css"

const Dashboard = () => {
    const [gameData, setGameData] = useState([])
    const [error, setError] = useState("")
    const APIkey = "7dAeSgsyEsfPn59lw2BHOjPzQ9Xm3M3chdiSC0r8jGEABOoe344"
    const navigate = useNavigate()

    useEffect(() => {
        const token = localStorage.getItem("authToken")
        if (!token) {
            navigate("/login")
        }

        const fetchGameData = async () => {
            try {
                const res = await fetch("https://api.pandascore.co/videogames", {
                    method: 'GET',
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${APIkey}`
                    }
                })

                if (!res.ok) {
                    throw new Error("Error en la respuesta del servidor")
                }

                const data = await res.json()
                setGameData(data)
            } catch (err) {
                setError("No se pudo cargar la información del usuario")
                console.error(err)
            }
        }
        console.log(gameData)
        fetchGameData()
    }, [navigate])

    const handleLogin = () => {
        localStorage.removeItem("authToken")
        localStorage.removeItem("userName")
        navigate("/login")
    }

    return (
        <div className="dashboard-container">
            <h2>Dashboard</h2>
            {error && <p className="error">{error}</p>}
            <div className="games-container">
                {gameData.map((game) => (
                    <div key={game.id} className="game-item">
                        <h3>{game.name}</h3>
                        <img src={game.leagues[0].image_url} alt={game.name} className="game-image" />
                    </div>
                ))}
            </div>
            <button onClick={handleLogin}>Cerrar sesión</button>
        </div>
    )
}

export default Dashboard