import React, { useState } from "react";
import "../assets/Profile.css";
import { useNavigate } from "react-router-dom";

const Profile = () => {
    const navigate = useNavigate()
    const [profileImage, setProfileImage] = useState(null)
    const [menuVisible, setMenuVisible] = useState(false)

    const handleImageUpload = (e) => {
        const file = e.target.files[0]
        if (file) {
            const reader = new FileReader()
            reader.onloadend = () => {
                setProfileImage(reader.result)
            }
            reader.readAsDataURL(file)
        }
    }

    const toggleMenu = () => {
        setMenuVisible(!menuVisible)
    }

    const handleLogin = () => {
        localStorage.removeItem("authToken")
        localStorage.removeItem("userName")
        localStorage.removeItem("userId")
        navigate("/login")
    }

    return (
        <div className="profile-container">
            <div className="profile-image-container" onClick={toggleMenu}>
                <img
                    src={profileImage || "default-profile.png"} // Imagen por defecto si no hay una cargada
                    alt="Foto de perfil"
                    className="profile-image"
                />
            </div>
            <input
                type="file"
                accept="image/*"
                id="imageUpload"
                style={{ display: "none" }}
                onChange={handleImageUpload}
            />
            <label htmlFor="imageUpload" className="upload-button">
                Subir imagen
            </label>

            {menuVisible && (
                <div className="dropdown-menu">
                    <button onClick={() => navigate("/verPerfil")}>Ver perfil</button>
                    <button onClick={() => alert("Editar perfil")}>Editar perfil</button>
                    <button onClick={handleLogin}>Cerrar sesión</button>
                </div>
            )}
        </div>
    )
}

export default Profile