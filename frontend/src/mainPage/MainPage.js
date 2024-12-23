import React, { useState, useEffect } from 'react';
import BurgerMenu from '../mainPageComponents/BurgerMenu';
import RequestModal from '../mainPageComponents/RequestModal';
import LogoutButton from '../mainPageComponents/LogoutButton';
import './MainPage.css';
import Table from '../mainPageComponents/Table';
import CreateHumanBeingForm from '../mainPageComponents/CreateHumanBeingForm';

const MainPage = () => {
    const [isOpen, setIsOpen] = useState(false);
    const [token, setToken] = useState(localStorage.getItem('token'));
    const [role, setRole] = useState(localStorage.getItem('role'));
    // const token = localStorage.getItem('token');
    // const role = localStorage.getItem('role');

    const [waitingUsers, setWaitingUsers] = useState([]);
    const [showRequests, setShowRequests] = useState(false);
    const [humanBeings, setHumanBeings] = useState([]);
    const [total, setTotal] = useState(0);
    const [page, setPage] = useState(0);
    const [size] = useState(10);



    const [nameFilter, setNameFilter] = useState('');
    const [carNameFilter, setCarNameFilter] = useState('');
    const [moodFilter, setMoodFilter] = useState('');
    const [weaponTypeFilter, setWeaponTypeFilter] = useState('');



    const moods = ["SORROW", "LONGING", "GLOOM", "FRENZY"]; // Примеры задач настроить по вашему
    const weaponTypes = ["HAMMER", "PISTOL", "SHOTGUN"];

    const fetchHumanBeings = async () => {
        const response = await fetch(`http://localhost:8080/backend-1.0-SNAPSHOT/human/list?page=${page}&size=${size}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
        });
        if (response.ok) {
            const data = await response.json();
            setHumanBeings(data.data);
            setTotal(data.total);
        } else {
            console.error('Ошибка:', await response.json());
        }
    };

    const fetchHumanBeingsWithFilters = async () => {
        const response = await fetch(`http://localhost:8080/backend-1.0-SNAPSHOT/human/filtered-list`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                "name" : nameFilter,
                "carName": carNameFilter,
                "mood": moodFilter,
                "weaponType": weaponTypeFilter,
                "page": page,
                "size": size,
            })
        });
        if (response.ok) {
            const data = await response.json();
            setHumanBeings(data.data);
            setTotal(data.total);
        } else {
            console.error('Ошибка:', await response.json());
        }
    };

    useEffect(() => {
        fetchHumanBeingsWithFilters();
    }, [page]);

    useEffect(() => {
        const ws = new WebSocket(`http://localhost:8080/backend-1.0-SNAPSHOT/chat?token=${token}`);
        ws.onopen = () => {
            console.log("WebSocket Connected");
        };

        ws.onmessage = (event) => {
            console.log("WebSocket get message");
            const message = JSON.parse(event.data);
            localStorage.setItem('token', message['token']);
            localStorage.setItem('isWaitingAdmin', message['isWaitingAdmin']);
            localStorage.setItem('role', message['role']);
            console.log(localStorage.getItem('token'));
            console.log(localStorage.getItem('role'));
            console.log(localStorage.getItem('isWaitingAdmin'));
            setToken(localStorage.getItem('token'));
            setRole(localStorage.getItem('role'))
            ws.close();
        };

        ws.onclose = () => {
            console.log('websocket was closed');
        };

        setInterval(() => {
            if (ws.readyState === WebSocket.OPEN) {
                ws.send(JSON.stringify({ type: 'ping' }));
            }
        }, 30000);

        ws.onerror = (error) => {
            console.error("WebSocket error: ", error);
        };

    }, []);

    useEffect(() => {
        const wsMovie = new WebSocket(`http://localhost:8080/backend-1.0-SNAPSHOT/update-humans`);
        wsMovie.onopen = () => {
            console.log("WebSocket Connected");
        };

        wsMovie.onmessage = (event) => {
            console.log(`WebSocket get message: ${event.data}`);
            fetchHumanBeingsWithFilters();
        };

        wsMovie.onclose = () => {
            console.log('websocket was closed');
        };

        setInterval(() => {
            if (wsMovie.readyState === WebSocket.OPEN) {
                wsMovie.send(JSON.stringify({ type: 'ping' }));
            }
        }, 30000);

        wsMovie.onerror = (error) => {
            console.error("WebSocket error: ", error);
        };
    }, []);

    const handlePageChange = (newPage) => {
        setPage(newPage);
    };
    const toggleMenu = () => {
        setIsOpen(prevState => !prevState);
    };

    const handleGetWaitingAdmins = async () => {
        const response = await fetch('http://localhost:8080/backend-1.0-SNAPSHOT/admin/waiting-admin', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const data = await response.json();
            setWaitingUsers(data.logins);
        } else {
            console.error('Ошибка:', await response.json());
            setWaitingUsers([]);
        }
    };

    const toggleRequestList = () => {
        setShowRequests(!showRequests);
        if (!showRequests) {
            handleGetWaitingAdmins();
        }
    };

    const closeRequestModal = () => {
        setShowRequests(false);
    };

    return (
        <div className="burger-menu">
            <BurgerMenu isOpen={isOpen} toggleMenu={toggleMenu} role={role} toggleRequestList={toggleRequestList} />
            <LogoutButton handleLogout={() => {
                localStorage.removeItem('token');
                localStorage.removeItem('role');
                window.location = '/registration-form';
            }} />
            {showRequests && <div className="overlay" onClick={closeRequestModal}></div>}
            {showRequests && (
                <RequestModal
                    closeRequestModal={closeRequestModal}
                    waitingUsers={waitingUsers}
                    setWaitingUsers={setWaitingUsers}
                />
            )}
            <div className="user_login">{localStorage.getItem("login")}</div>
            <div className="table-container">
                <div className="filter-container">
                    <div>
                        <input
                            type="text"
                            placeholder="Фильтр по имени"
                            value={nameFilter}
                            onChange={(e) => setNameFilter(e.target.value)}
                        />
                        <input
                            type="text"
                            placeholder="Фильтр по имени машины"
                            value={carNameFilter}
                            onChange={(e) => setCarNameFilter(e.target.value)}
                        />
                    </div>
                    <div>
                        <select value={moodFilter} onChange={(e) => setMoodFilter(e.target.value)}>
                            <option value="">Все настроения</option>
                            {moods.map((mood, index) => (
                                <option key={index} value={mood}>{mood}</option>
                            ))}
                        </select>
                        <select value={weaponTypeFilter} onChange={(e) => setWeaponTypeFilter(e.target.value)}>
                            <option value="">Все типы оружия</option>
                            {weaponTypes.map((weaponType, index) => (
                                <option key={index} value={weaponType}>{weaponType}</option>
                            ))}
                        </select>
                    </div>
                    <button onClick={() => {
                        fetchHumanBeingsWithFilters();
                    }} className="apply-button">Применить</button>
                </div>
            </div>
            <Table data={humanBeings} role={role} />
            <Pagination
                total={total}
                currentPage={page}
                pageSize={size}
                onPageChange={handlePageChange}
            />
        </div>
    );
};
const Pagination = ({ total, currentPage, pageSize, onPageChange }) => {

    const totalPages = Math.ceil(total / pageSize);

    const handleNext = () => {
        onPageChange(currentPage + 1);
    };

    const handlePrevious = () => {
        onPageChange(currentPage - 1);
    };

    return (
        <div className="nextPage">
            <button onClick={handlePrevious} disabled={currentPage === 0}>Назад</button>
            <span>{` Страница ${currentPage + 1} из ${totalPages} `}</span>
            <button onClick={handleNext} disabled={currentPage + 1 >= totalPages}>Вперед</button>
        </div>
    );
};

export default MainPage;
