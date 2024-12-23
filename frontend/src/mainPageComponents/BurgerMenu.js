import React, { useEffect, useRef, useState } from 'react';
import '../mainPageComponentsStyle/BurgerMenu.css';
import CreateHumanBeingForm from '../mainPageComponents/CreateHumanBeingForm';

const BurgerMenu = ({ isOpen, toggleMenu, role, toggleRequestList }) => {
    const [isFormVisible, setFormVisible] = useState(false);
    const [isSpecialOperationsVisible, setSpecialOperationsVisible] = useState(false);
    const specialOperationsRef = useRef(null);
    const [avgSpeedModalVisible, setAvgSpeedModalVisible] = useState(false);
    const [impactSpeed, setAvgImpactSpeed] = useState(null);
    const modalRef = useRef(null);
    const [waitingMinutesModalVisible, setWaitingMinutesModalVisible] = useState(false);
    const [minutesOfWaitingInput, setMinutesOfWaitingInput] = useState('');
    const [waitingMinutesCount, setWaitingMinutesCount] = useState(null);
    const [carNameUpdateModalVisible, setCarNameUpdateModalVisible] = useState(false);
    const [deleteModalVisible, setDeleteModalVisible] = useState(false);
    const [userPrefix, setUserPrefix] = useState('');
    const [users, setUsers] = useState([]);
    const [isUserModalVisible, setIsUserModalVisible] = useState(false)

    const [isIdSearchModalVisible, setIsIdSearchModalVisible] = useState(false);
    const [humanBeingData, setHumanBeingData] = useState(null);
    const [idInput, setIdInput] = useState('');

    const handleCreateClick = () => {
        setFormVisible(true);
    };

    const avgImpactSpeed = async () => {
        const response = await fetch(`http://localhost:8080/backend-1.0-SNAPSHOT/human/avgImpactSpeed`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        if (response.ok) {
            const data = await response.json();
            setAvgImpactSpeed(data.avgImpactSpeed);
            setAvgSpeedModalVisible(true);
        } else {
            console.error('Ошибка:', await response.json());
        }
    };

    const countWaitingMinutesLessThan = async () => {
        const response = await fetch(`http://localhost:8080/backend-1.0-SNAPSHOT/human/waiting_minutes_less`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ waitingMinutes: parseInt(minutesOfWaitingInput) })
        });
        if (response.ok) {
            const data = await response.json();
            setWaitingMinutesCount(data.waitingMinutesLess);
            setWaitingMinutesModalVisible(true);
        } else {
            console.error('Ошибка:', await response.json());
        }
    };

    const updateCarName = async () => {
        const response = await fetch(`http://localhost:8080/backend-1.0-SNAPSHOT/human/update_car_name`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
        });
        if (response.ok) {
            setCarNameUpdateModalVisible(true);
        } else {
            console.error('Ошибка:', await response.json());
        }
    };

    const closeCarNameUpdateModal = () => {
        setCarNameUpdateModalVisible(false);
    };

    const closeWaitingMinutesModal = () => {
        setWaitingMinutesModalVisible(false);
        setWaitingMinutesCount(null);
        setMinutesOfWaitingInput('');
    };

    const handleCountWaitingClick = () => {
        setWaitingMinutesModalVisible(true);
    };

    const handleCloseForm = () => {
        setFormVisible(false);
    };
    const closeDeleteModal = () => {
        setDeleteModalVisible(false);
    };

    const handleSpecialOperationsClick = () => {
        setSpecialOperationsVisible(!isSpecialOperationsVisible);
    };
    const closeIdSearchModal = () => {
        setIsIdSearchModalVisible(false);
        setHumanBeingData(null);
        setIdInput('');
    };
    const searchHumanBeingById = async () => {
        const response = await fetch(`http://localhost:8080/backend-1.0-SNAPSHOT/human/get_by_id`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ id: parseInt(idInput) })
        });

        if (response.ok) {
            const data = await response.json();
            setHumanBeingData(data);
        } else {
            console.error('Ошибка:', await response.json());
        }
    };

    const handleSearchByIdClick = () => {
        setIsIdSearchModalVisible(true);
    };

    const handleClickOutsideModal = (event) => {
        if (modalRef.current && !modalRef.current.contains(event.target)) {
            if (avgSpeedModalVisible) closeAvgSpeedModal();
            if (waitingMinutesModalVisible) closeWaitingMinutesModal();
            if (carNameUpdateModalVisible) closeCarNameUpdateModal();
            if (deleteModalVisible) closeDeleteModal();
            if (isUserModalVisible) closeUserModal();
        }
    };

    const deleteNonToothpick = async () => {
        const response = await fetch(`http://localhost:8080/backend-1.0-SNAPSHOT/human/delete_non_toothpick`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            },
        });
        if (response.ok) {
            setDeleteModalVisible(false);
        } else {
            console.error('Ошибка:', await response.json());
        }
    };

    const handleDeleteClick = () => {
        setDeleteModalVisible(true);
    };
    const closeUserModal = () => {
        setIsUserModalVisible(false);
        setUsers([]);
    };


    const searchUsersByPrefix = async () => {
        const response = await fetch(`http://localhost:8080/backend-1.0-SNAPSHOT/human/name_start_from`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ prefix: userPrefix })
        });

        if (response.ok) {
            const data = await response.json();
            setUsers(data.data);
            setIsUserModalVisible(true);
        } else {
            console.error('Ошибка:', await response.json());
        }
    };


    useEffect(() => {
        document.addEventListener('mousedown', handleClickOutsideModal);

        return () => {
            document.removeEventListener('mousedown', handleClickOutsideModal);
        };
    }, [avgSpeedModalVisible, waitingMinutesModalVisible, carNameUpdateModalVisible]);

    const closeAvgSpeedModal = () => {
        setAvgSpeedModalVisible(false);
        setAvgImpactSpeed(null);
    };

    return (
        <>
            <button className="burger-button" onClick={toggleMenu}>
                <div className={`line ${isOpen ? 'active' : ''}`}></div>
                <div className={`line ${isOpen ? 'active' : ''}`}></div>
                <div className={`line ${isOpen ? 'active' : ''}`}></div>
            </button>
            {isOpen && (
                <div className="dropdown-menu">
                    <button className="dropdown-item" onClick={handleCreateClick}>Создать</button>
                    <button className="dropdown-item" onClick={handleSearchByIdClick}>Найти по ID</button>
                    <button className="dropdown-item" onClick={handleSpecialOperationsClick}>Специальные операции</button>
                    {isSpecialOperationsVisible && (
                        <div className="special-operations" ref={specialOperationsRef}>
                            <button className="dropdown-item" onClick={avgImpactSpeed}>Рассчитать среднее значение impact speed</button>
                            <button className="dropdown-item" onClick={handleCountWaitingClick}>Найти количество элементов waiting minutes, которое меньше заданного</button>
                            <button className="dropdown-item" onClick={updateCarName}>Пересадить всех человеческих существ, у которых нет машины на красную Lada Kalina</button>
                            <button className="dropdown-item" onClick={handleDeleteClick}>Удалить всех людей без зубочистки</button>
                            <button className="dropdown-item" onClick={() => setIsUserModalVisible(true)}>Найти всех пользователей, имя которых начинается с заданной подстроки</button>
                        </div>
                    )}
                    {role === "ADMIN" && (
                        <button className="dropdown-item" onClick={toggleRequestList}>
                            Список заявок
                        </button>
                    )}
                    {isFormVisible && <CreateHumanBeingForm onClose={handleCloseForm} />}
                </div>
            )}
            {isFormVisible && <div className="dropdown-item" onClick={handleCloseForm}></div>}

            {avgSpeedModalVisible && (
                <div className="modal">
                    <div className="modal-content" ref={modalRef}>
                        <h2>Среднее значение impact speed</h2>
                        <p>{impactSpeed}</p>
                        <button onClick={closeAvgSpeedModal}>Ок</button>
                    </div>
                </div>
            )}

            {waitingMinutesModalVisible && (
                <div className="modal">
                    <div className="modal-content" ref={modalRef}>
                        <h2>Найти количество элементов waiting minutes, которое меньше заданного</h2>
                        <input
                            type="number"
                            value={minutesOfWaitingInput}
                            onChange={(e) => setMinutesOfWaitingInput(e.target.value)}
                            placeholder="Введите целое число"
                        />
                        <button className="minutesOfWaitingSendButton" onClick={() => { countWaitingMinutesLessThan(); }}>Отправить</button>
                        {waitingMinutesCount !== null && <p>Количество объектов: {waitingMinutesCount}</p>}
                        <button onClick={closeWaitingMinutesModal}>Закрыть</button>
                    </div>
                </div>
            )}

            {carNameUpdateModalVisible && (
                <div className="modal">
                    <div className="modal-content" ref={modalRef}>
                        <h2>Обновление car_name завершено</h2>
                        <button onClick={closeCarNameUpdateModal}>Закрыть</button>
                    </div>
                </div>
            )}
            {deleteModalVisible && (
                <div className="modal">
                    <div className="modal-content" ref={modalRef}>
                        <h2>Удалить всех людей без зубочистки?</h2>
                        <button onClick={deleteNonToothpick}>Да</button>
                        <button onClick={closeDeleteModal}>Нет</button>
                    </div>
                </div>
            )}

            {isUserModalVisible && (
                <div className="modal">
                    <div className="modal-content" ref={modalRef}>
                        <h2>Найти пользователей</h2>
                        <input
                            type="text"
                            placeholder="Введите префикс"
                            value={userPrefix}
                            onChange={(e) => setUserPrefix(e.target.value)}
                        />
                        <button className="dropdown-item" id ="find-users" onClick={searchUsersByPrefix}>
                            Найти
                        </button>
                        <h3>Найденные пользователи:</h3>
                        <ul>
                            {users.length > 0 ? (
                                users.map((user) => (
                                    <li key={user.id}>{user.name}</li>
                                ))
                            ) : (
                                <li>Пользователи не найдены.</li>
                            )}
                        </ul>
                        <button onClick={closeUserModal}>Закрыть</button>
                    </div>
                </div>
            )}

            {isIdSearchModalVisible && (
                <div className="modal" >
                    <div className="modal-content" ref={modalRef}>
                        <h2>Найти по ID</h2>
                        <input
                            type="number"
                            placeholder="Введите ID"
                            value={idInput}
                            onChange={(e) => setIdInput(e.target.value)}
                        />
                        <button onClick={searchHumanBeingById}>Найти</button>
                        {humanBeingData && (
                            <div className="find-by-id">
                                <p>id: {humanBeingData.id}</p>
                                <p>name: {humanBeingData.name}</p>
                                <p>coordinates: ({humanBeingData.coordinates.x}, {humanBeingData.coordinates.y})</p>
                                <p>creation date: {`${humanBeingData.creationDate[0]}-${humanBeingData.creationDate[1]}-${humanBeingData.creationDate[2]} ${humanBeingData.creationDate[3]}:${humanBeingData.creationDate[4]}:${humanBeingData.creationDate[5]}`}</p>
                                <p>real hero: {humanBeingData.realHero ? 'Да' : 'Нет'}</p>
                                <p>has toothpick: {humanBeingData.hasToothpick ? 'Да' : 'Нет'}</p>
                                <p>car name: {humanBeingData.car ? humanBeingData.car.name : 'Нет'}</p>
                                <p>mood: {humanBeingData.mood}</p>
                                <p>impact speed: {humanBeingData.impactSpeed}</p>
                                <p>minutes of waiting: {humanBeingData.minutesOfWaiting}</p>
                                <p>weapon type: {humanBeingData.weaponType}</p>
                            </div>
                        )}
                        <button onClick={closeIdSearchModal}>Закрыть</button>
                    </div>
                </div>
            )}
        </>
    );
};

export default BurgerMenu;
