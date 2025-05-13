import express from "express"
import db from "../config/db.js"
import bcrypt from "bcryptjs"
import jwr from "jsonwebtoken"
import admin from "../config/firebase.js"

const router = express.Router()

router.post("/register", async (req, res) => {
    const { email, password, nombre, rol = "user" } = req.body

    try {
        const [userExists] = await db.query("SELECT * FROM usuarios WHERE email = ?", [email])

        if (userExists.length > 0) {
            return res.status(400).json({ error: "El usuario ya está registrado" })
        }

        const hashedPassword = await bcrypt.hash(password, 10)

        await db.query("INSERT INTO usuarios (email, password, nombre, rol) VALUES (?, ?, ?, ?)", [email, hashedPassword, nombre, rol])

        res.json({ message: "Usuario registrado correctamente" })
    } catch (error) {
        res.status(500).json({ error: "Error en el servidor" })
    }
})

router.post("/login", async (req, res) => {
    const { email, password } = req.body

    try {
        const [rows] = await db.query("SELECT * FROM usuarios WHERE email = ?", [email])

        if (rows.length === 0) {
            return res.status(400).json({ error: "Usuario o contraseña incorrectos" })
        }

        const usuario = rows[0]

        const match = await bcrypt.compare(password, usuario.password)
        if (!match) {
            return res.status(400).json({ error: "Usuario o contraseña incorrectos" })
        }

        const token = jwr.sign({ id: usuario.id, email: usuario.email, rol: usuario.rol }, process.env.JWT_SECRET, { expiresIn: "1h" })

        res.json({ message: "Inicio de sesión exitoso", token, usuario, rol: usuario.rol })
    } catch (error) {
        res.status(500).json({ error: "Error en el servidor" })
    }
})

router.get("/profile/:email", async (req, res) => {
    const { email } = req.params
    try {
        const [rows] = await db.query("SELECT * FROM usuarios WHERE email = ?", [email])
        if (rows.length > 0) {
            res.json({
                nombre: rows[0].nombre,
                imagen_perfil: rows[0].imagen_perfil || null
            })
        } else {
            res.status(404).json({ error: "Usuario no encontrado" })
        }
    } catch (error) {
        console.error(error)
        res.status(500).json({ error: "Error en el servidor" })
    }
})

router.post("/tournaments", async (req, res) => {
    const { nombre, nombre_juego, fecha_ini, fecha_fin, dia_torn } = req.body
    db.query("INSERT INTO tournaments (nombre, nombre_juego, fecha_ini, fecha_fin, dia_torn) VALUES (?, ?, ?, ?, ?)", [nombre, nombre_juego, fecha_ini, fecha_fin, dia_torn], (err, result) => {
        if (err) {
            console.error(err)
            return res.status(500).json({ error: "Error al crear el torneo" })
        }

        res.json({ message: "Torneo creado correctamente", tournamentId: result.insertId })
    })
})

router.get("/tournaments/:game", async (req, res) => {
    const { game } = req.params;
    console.log("Solicitud recibida para el juego:", game);

    try {
        const [rows] = await db.query("SELECT * FROM tournaments WHERE nombre_juego = ?", [game]);

        if (rows.length === 0) {
            console.log("No se encontraron torneos para el juego:", game);
            return res.status(404).json({ error: "No se encontraron torneos para este juego" });
        }

        console.log("Torneos encontrados:", rows);
        res.json(rows); // Solo se envía una respuesta aquí
    } catch (error) {
        console.error("Error al obtener torneos:", error);
        res.status(500).json({ error: "Error en el servidor al obtener torneos" });
    }
})

router.delete("/tournaments/:id", async (req, res) => {
    const { id } = req.params

    const [tournamentsExist] = await db.query("SELECT * FROM tournaments WHERE id = ?", [id])

    if (tournamentsExist.length === 0) {
        return res.status(404).json({ error: "No se encontró el torneo" })
    }

    db.query("DELETE FROM tournamentsaccept WHERE torneo_id = ?", [id])
    db.query("DELETE FROM tournaments WHERE id = ?", [id])
    db.query("DELETE FROM reportes WHERE torneo_id = ?", [id])
})

router.post("/acceptTournament", async (req, res) => {
    const { torneo_id, nombre, nombre_juego, fecha_ini, fecha_fin, dia_torn, usuario_id, nombre_usuario } = req.body

    const [tournamentsExist] = await db.query("SELECT * FROM tournamentsaccept WHERE torneo_id = ? AND usuario_id = ?", [torneo_id, usuario_id])

    if (tournamentsExist.length > 0) {
        return res.status(400).json({ error: "Ya has aceptado este torneo" })
    }

    db.query("INSERT INTO tournamentsaccept (torneo_id, nombre_torneo, nombre_juego, fecha_ini, fecha_fin, dia_torn, usuario_id, nombre_usuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", [torneo_id, nombre, nombre_juego, fecha_ini, fecha_fin, dia_torn, usuario_id, nombre_usuario])
})

router.get("/acceptTournament/:userId", async (req, res) => {
    const { userId } = req.params;
    console.log("User ID recibido:", userId);
    try {
        const [rows] = await db.query("SELECT * FROM tournamentsaccept WHERE usuario_id = ?", [userId]);

        if (rows.length === 0) {
            return res.status(404).json({ error: "No se encontraron torneos aceptados para este usuario" });
        }

        res.json(rows);
    } catch (error) {
        console.error("Error al obtener torneos aceptados:", error);
        res.status(500).json({ error: "Error en el servidor" });
    }
})

router.get("/tournamentuser/:torneo_id", async (req, res) => {
    const { torneo_id } = req.params
    try {
        const [rows] = await db.query("SELECT * FROM tournamentsaccept WHERE torneo_id = ?", [torneo_id])
        if (rows.length === 0) {
            return res.status(404).json({ error: "No se encontraron torneos" })
        }
        res.json(rows);
    } catch (error) {
        res.status(500).json({ error: "Error en el servidor" })
    }
})

router.post("/report/:torneoId", async (req, res) => {
    const { torneoId } = req.params
    const { usuario_name, motivo } = req.body
    const [reportExists] = await db.query("SELECT * FROM reportes WHERE torneo_id = ? AND nombre_usuario = ?", [torneoId, usuario_name])
    if (reportExists.length > 0) {
        return res.status(400).json({ error: "Ya has reportado este torneo" })
    }
    db.query("INSERT INTO reportes (torneo_id, nombre_usuario, motivo) VALUES (?, ?, ?)", [torneoId, usuario_name, motivo])
})

router.delete("/report/:id", async (req, res) => {
    const { id } = req.params

    db.query("DELETE FROM reportes WHERE id = ?", [id])
})

router.get("/report", async (req, res) => {
    const [rows] = await db.query("SELECT * FROM reportes")
    if (rows.length === 0) {
        return res.status(404).json({ error: "No se encontraron reportes" })
    }
    res.json(rows)
})

router.delete("/eliminarJugador/:jugadorId", async (req, res) => {
    const { jugadorId } = req.params
    try {
        await db.query("DELETE FROM tournamentsaccept WHERE usuario_id = ?", [jugadorId])
        await db.query("DELETE FROM emparejamientos WHERE participante1_id = ? OR participante2_id = ?", [jugadorId, jugadorId])
        res.status(200).json({ message: "Jugador eliminado correctamente" })
    } catch (error) {
        console.error("Error al eliminar el jugador:", error)
        res.status(500).json({ error: "Error al eliminar el jugador" })
    }
})

router.get('/emparejamientos/:torneo_id', async (req, res) => {
    const { torneo_id } = req.params;

    try {
        const [rows] = await db.query(
            `SELECT 
                e.id,
                e.torneo_id,
                u1.nombre AS participante1_nombre,
                u2.nombre AS participante2_nombre
            FROM emparejamientos e
            JOIN usuarios u1 ON e.participante1_id = u1.id
            JOIN usuarios u2 ON e.participante2_id = u2.id
            WHERE e.torneo_id = ?`,
            [torneo_id]
        );
        res.status(200).json(rows);
    } catch (error) {
        console.error('Error al obtener los emparejamientos:', error);
        res.status(500).send('Error al obtener los emparejamientos.');
    }
})

router.post('/emparejamientos/:torneo_id', async (req, res) => {
    const { torneo_id } = req.params;
    const emparejamientos = req.body;

    console.log('Datos recibidos para insertar:', emparejamientos);
    try {
        for (const emparejamiento of emparejamientos) {
            await db.query(
                'INSERT INTO emparejamientos (torneo_id, participante1_id, participante2_id) VALUES (?, ?, ?)',
                [torneo_id, emparejamiento.participante1_id, emparejamiento.participante2_id]
            );
        }
        res.status(201).send('Emparejamientos guardados correctamente.');
    } catch (error) {
        console.error('Error al guardar los emparejamientos:', error);
        res.status(500).send('Error al guardar los emparejamientos.');
    }
});



export default router