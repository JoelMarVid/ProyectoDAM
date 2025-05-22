import express from "express"
import cors from "cors"
import http from "http"
import { Server } from "socket.io"
import dotenv from "dotenv"
import router from "./routes/authRoutes.js"

dotenv.config()

const app = express()

const server = http.createServer(app)

const io = new Server(server, {
    cors: {
        origin: "*"
    }
})

io.on("connection", (socket) => {
    socket.on("join_tournament", ({torneo_id, usuario_id, nombre_usuario}) => {
        socket.join(`torneo_${torneo_id}`)
    })

    socket.on("chat_message", ({torneo_id, usuario_id, nombre_usuario, mensaje}) => {
        io.to(`torneo_${torneo_id}`).emit('chat_message', {
            usuario_id,
            nombre_usuario,
            mensaje,
            fecha: new Date().toISOString()
        })
    })

    socket.on("disconnect", () => {
        console.log("Usuario desconectado")
    })
})

app.use(cors())
app.use(express.json())

app.use("/auth", router)

const PORT = process.env.PORT || 3000
server.listen(PORT, '0.0.0.0',() => console.log(`Servidor corriendo en http://localhost: ${PORT}`))