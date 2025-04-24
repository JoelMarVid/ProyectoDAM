import express from "express"
import cors from "cors"
import dotenv from "dotenv"
import router from "./routes/authRoutes.js"

dotenv.config()

const app = express()

app.use(cors())
app.use(express.json())

app.use("/auth", router)

const PORT = process.env.PORT || 3000
app.listen(PORT, '0.0.0.0',() => console.log(`Servidor corriendo en http://localhost: ${PORT}`))