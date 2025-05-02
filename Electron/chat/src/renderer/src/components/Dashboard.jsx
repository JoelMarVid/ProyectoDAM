import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../assets/Dashboard.css"
import Profile from "./Profile";

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

    return (
        <div className="dashboard-container">
            <div className="dashboard-header">
                <h2>Dashboard</h2>
                <Profile />
            </div>
            {error && <p className="error">{error}</p>}
            <div className="games-container">
                {gameData.map((game) => (
                    <div key={game.id} className="game-item">
                        <h3 onClick={() => navigate(`/torneos/${game.name}`)}>{game.name}</h3>
                        <img src={game.leagues[0].image_url} alt={game.name} className="game-image" />
                    </div>
                ))}
            </div>
            <button className="create-tournament-button" onClick={() => navigate("/crear-torneo")}>+</button>
        </div>
    )
}

export default Dashboard