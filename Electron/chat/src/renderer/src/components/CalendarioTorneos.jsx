import React, { useEffect, useState } from "react"
import Calendar from "react-calendar"
import "../assets/Calendar.css"
import axios from "axios"
import { useNavigate } from "react-router-dom"

const CalendarioTorneos = () => {
    const [tournamentsData, setTournamentsData] = useState([])
    const navigate = useNavigate()
    const [selectedDate, setSelectedDate] = useState(new Date())

    useEffect(() => {
        const fetchTournamentsData = async () => {
            const userId = localStorage.getItem("userId")
            try {
                const res = await axios.get(`http://localhost:3000/auth/acceptTournament/${userId}`)
                const data = res.data
                setTournamentsData(data)
            } catch (error) {
                console.error("Error al obtener los torneos:", error)
            }
        }

        fetchTournamentsData()
    }, [])

    const getTournamentForDate = (date) => {
        return tournamentsData.filter((tournament) => new Date(tournament.dia_torn).toDateString() === date.toDateString())
    }

    const handleDateClick = (date) => {
        setSelectedDate(date)
    }

    return (
        <div>
            <button onClick={() => navigate("/dashboard")}>Volver</button>
            <h2>Calendario</h2>
            <Calendar
                value={selectedDate}
                onClickDay={handleDateClick}
                tileContent={({ date }) => {
                    const tournaments = getTournamentForDate(date)
                    return tournaments.length > 0 ? (
                        <div className="calendar-tournament">
                            {tournaments.map((tournament, index) => (
                                <p key={index} className="tournament-name">
                                    {tournament.nombre || tournament.nombre_torneo}
                                </p>
                            ))}
                        </div>
                    ) : null
                }}
                tileClassName={({ date }) => {
                    const tournamentDates = getTournamentForDate(date)
                    return tournamentDates.length > 0
                        ? "highlight"
                        : null
                }}
            />
            <div>
                <h3>Detalles del Torneo</h3>
                {tournamentsData
                    .filter(
                        (tournament) => new Date(tournament.dia_torn).toDateString() === selectedDate.toDateString()
                    )
                    .map((tournament, index) => (
                        <div key={index}>
                            <h4>{tournament.nombre || tournament.nombre_torneo}</h4>
                            <p>Juego: {tournament.nombre_juego}</p>
                            <p>Fecha de inicio: {new Date(tournament.fecha_ini).toLocaleDateString()}</p>
                            <p>Fecha de fin: {new Date(tournament.fecha_fin).toLocaleDateString()}</p>
                        </div>
                    ))
                }
            </div>
        </div>
    )
}

export default CalendarioTorneos