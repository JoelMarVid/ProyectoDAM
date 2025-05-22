import React, {useEffect, useState} from "react";
import { useNavigate, useParams } from "react-router-dom";
import io from "socket.io-client"

const socket = io("http://localhost:3000")

const ChatTorneo = ({ usuario_id, nombre_usuario }) => {
    const { torneo_id } = useParams()
    const navigate = useNavigate()
    const [mensaje, setMensaje] = useState("")
    const [mensajes, setMensajes] = useState([])

    useEffect(() => {
        socket.emit("join_tournament", { torneo_id, usuario_id, nombre_usuario})

        socket.on("chat_message", (msg) => {
            setMensajes((prev) => [...prev, msg])
        })

        return () => {
            socket.off("chat_message")
        }
    }, [torneo_id, usuario_id, nombre_usuario])

    const enviarMensaje = (e) => {
        e.preventDefault()
        if (mensaje.trim() === "") return
        socket.emit("chat_message", { torneo_id, usuario_id, nombre_usuario, mensaje})
        setMensaje("")
    }

    return (
        <div>
            <div style={{ height: 300, overflowY: "auto", border: "1px solid #ccc", marginBottom: 10 }}>
                {mensajes.map((msg, idx) => (
                    <div key={idx}>
                        <b>{msg.nombre_usuario}:</b> {msg.mensaje}
                        <span style={{ fontSize: "0.8em", color: "#888", marginLeft: 8 }}>
                            {new Date(msg.fecha).toLocaleTimeString()}
                        </span>
                    </div>
                ))}
            </div>
            <form onSubmit={enviarMensaje}>
                <input
                    value={mensaje}
                    onChange={(e) => setMensaje(e.target.value)}
                    placeholder="Escribe tu mensaje..."
                />
                <button type="submit">Enviar</button>
                <button onClick={() => navigate("/dashboard")}>Volver</button>
            </form>
        </div>
    )
}

export default ChatTorneo