import express from "express"
import db from "../config/db.js"
import bcrypt from "bcryptjs"
import jwr from "jsonwebtoken"
import admin from "../config/firebase.js"

const router = express.Router()

router.post("/register", async (req, res) => {
    const {email, password, nombre} = req.body

    try {
        const [userExists] = await db.query("SELECT * FROM usuarios WHERE email = ?", [email])

        if (userExists.length > 0) {
            return res.status(400).json({error: "El usuario ya está registrado"})
        }

        const hashedPassword = await bcrypt.hash(password, 10)

        await db.query("INSERT INTO usuarios (email, password, nombre) VALUES (?, ?, ?)", [email, hashedPassword, nombre])

        res.json({message: "Usuario registrado correctamente"})
    } catch (error) {
       res.status(500).json({error: "Error en el servidor"}) 
    }
})

router.post("/login", async (req, res) => {
    const {email, password} = req.body

    try {
        const [rows] = await db.query("SELECT * FROM usuarios WHERE email = ?", [email])

        if (rows.length === 0) {
            return res.status(400).json({error: "Usuario o contraseña incorrectos"})
        }

        const usuario = rows[0]

        const match = await bcrypt.compare(password, usuario.password)
        if (!match) {
            return res.status(400).json({error: "Usuario o contraseña incorrectos"})
        }

        const token = jwr.sign({id: usuario.id, email: usuario.email}, process.env.JWT_SECRET, {expiresIn: "1h"})

        res.json({message: "Inicio de sesión exitoso", token, usuario})
    } catch (error) {
        res.status(500).json({error: "Error en el servidor"})
    }
})

export default router