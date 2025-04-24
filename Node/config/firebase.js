import admin from "firebase-admin"
import dotenv from "dotenv"
import path from "path"
import fs from "fs"


dotenv.config();

const a = {}

admin.initializeApp({
    credential: admin.credential.cert(a),
});


export default admin