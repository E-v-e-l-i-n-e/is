import React from 'react';
import '../mainPage/MainPage.css';

const LogoutButton = ({ handleLogout }) => {
    return (
        <button className="logout" onClick={handleLogout}>Выход</button>
    );
};

export default LogoutButton;
