const express = require("express")
const http = require("http")
const { Server } = require("socket.io")
const cors = require("cors")

//Inicializamos la aplicacion Express
const app = express()
const server = http.createServer(app)

//Permitimos conexiones desde cualquier origen
const io = new Server(server, {
    cors: {
        origin: "*",
        methods: ["GET", "POST"]
    }
})

//Middleware para manejar JSON en las peticiones
app.use(express.json())
app.use(cors())

//Ruta de prueba
app.get("/", (req, res) => {
    res.send("Servidor de chat")
})

let usuariosConectados = {}

io.on("connection", (socket) => {
    console.log("Usuario conectado: " + socket.id)

    //Evento cuando un usuario se une al chat
    socket.on("joinChat", (usuario) =>{
        usuariosConectados[socket.id] = usuario
        io.emit("usuariosActivos", Object.values(usuariosConectados))
        console.log(usuario+" se ha unido al chat")
    })

    //Evento para recibir y transmitir mensajes en el chat
    socket.on("mensajeChat", (mensaje) => {
        console.log("Mensaje recibido: "+mensaje)
        io.emit("mensajeChat", mensaje)
    })

    //Evento cuando un usuario se desconecta
    socket.on("disconnect", () => {
        console.log("Usuario desconectado: " + usuariosConectados[socket.id])
        delete usuariosConectados[socket.id]
        io.emit("usuariosActivos", Object.values(usuariosConectados))
    })
})

//Ponemos el servidor a escuchar en el puerto 3000
const PORT = process.env.PORT || 3000
server.listen(PORT, () => {
    console.log("Servidor de chat en http://localhost:"+PORT)
})