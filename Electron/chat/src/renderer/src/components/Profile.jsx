import React, { useState } from "react";
import "../assets/Profile.css";
import { useNavigate } from "react-router-dom";

const Profile = () => {
    const navigate = useNavigate()
    const [menuVisible, setMenuVisible] = useState(false)

    const toggleMenu = () => {
        setMenuVisible(!menuVisible)
    }

    const handleLogin = () => {
        localStorage.removeItem("authToken")
        localStorage.removeItem("userName")
        localStorage.removeItem("userId")
        toast.info("Sesión cerrada exitosamente")
        navigate("/login")
    }

    return (
        <div className="profile-container">
            <div className="profile-image-container" onClick={toggleMenu}>
                <svg width="32" height="32" viewBox="0 0 32 32">
                    <rect y="6" width="32" height="4" rx="2" fill="#fff"/>
                    <rect y="14" width="32" height="4" rx="2" fill="#fff"/>
                    <rect y="22" width="32" height="4" rx="2" fill="#fff"/>
                </svg>
            </div>
            
            {menuVisible && (
                <div className="dropdown-menu">
                    <button onClick={() => navigate("/verPerfil")}>Ver perfil</button>
                    <button onClick={handleLogin}>Cerrar sesión</button>
                </div>
            )}
        </div>
    )
}

export default Profile