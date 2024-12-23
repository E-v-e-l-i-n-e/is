// import React, { useState, useEffect } from 'react'
// import "../mainPage/MainPage";
// import MainPage from "../mainPage/MainPage";
//
// const WaitNewRole = () => {
//
//     const isWaitingAdmin = localStorage.getItem('isWaitingAdmin');
//     if (isWaitingAdmin === true) {
//         const token = localStorage.getItem('token');
//         const ws = new WebSocket(`ws://localhost:8080/backend-1.0-SNAPSHOT/chat?token=${token}`);
//
//         ws.onmessage = (event) => {
//             const message = JSON.parse(event.data);
//             localStorage.setItem('token', message['token']);
//             localStorage.setItem('isWaitingAdmin', message['isWaitingAdmin']);
//             localStorage.setItem('role', message['role']);
//             if (localStorage.getItem('role') === "ADMIN") {
//
//             }
//             ws.close();
//         }
//
//     }
//
//     return null;
// }
//
// export default WaitNewRole;