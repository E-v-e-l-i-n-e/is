// import React, { createContext, useState, useEffect } from 'react';
//
// export const AuthContext = createContext();
//
// export const AuthProvider = ({ children }) => {
//     const [token, setToken] = useState(localStorage.getItem('token'));
//     const [role, setRole] = useState(localStorage.getItem('role'));
//     const [isWaitingAdmin, setIsWaitingAdmin] = useState(localStorage.getItem('isWaitingAdmin'));
//
//     useEffect(() => {
//         localStorage.setItem('token', token);
//         localStorage.setItem('role', role);
//         localStorage.setItem('isWaitingAdmin', isWaitingAdmin);
//     }, [token, role, isWaitingAdmin]);
//
//     return (
//         <AuthContext.Provider value={{ token, setToken, role, setRole, isWaitingAdmin, setIsWaitingAdmin }}>
//             {children}
//         </AuthContext.Provider>
//     );
// };
