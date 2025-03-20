import React, { useState, useEffect } from "react"
import { io } from "socket.io-client"

const socket = io("http://localhost:3000")

const Chat = () => {
    const [mensaje, setMensaje] = useState("")
    const [mensajes, setMensajes] = useState([])

    useEffect(() => {
        socket.on("mensajeChat", (mensajeRecibido) => {
            setMensajes((prevMensajes) => [...prevMensajes, mensajeRecibido])
        })

        return () => {
            socket.off("mensajeChat")
        }
    }, [])

    const enviarMensaje = () => {
        if (mensaje.trim()) {
            const mensajeJson = {
                usuario: "UsuarioDesktop",
                contenido: mensaje
            }
            socket.emit("mensajeChat", mensajeJson)
            setMensaje("")
        }
    }

    return (
        <div style={{ padding: '20px' }}>
            <div style={{ height: '300px', overflowY: 'auto', backgroundColor: '#e0e0e0', marginBottom: '20px' }}>
                <div>
                    {mensajes.map((msg, index) => (
                        <p key={index}>
                            <strong>{msg.usuario}: </strong>{msg.contenido}
                        </p>
                    ))}
                </div>
            </div>

            <input
                type="text"
                value={mensaje}
                onChange={(e) => setMensaje(e.target.value)}
                placeholder="Escribe un mensaje..."
                style={{ padding: '10px', width: '80%' }}
            />
            <button onClick={enviarMensaje} style={{ padding: '10px', marginLeft: '10px' }}>
                Enviar
            </button>
        </div>
    )
}

export default Chat