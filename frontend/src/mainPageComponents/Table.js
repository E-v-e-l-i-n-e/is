import React, { useState } from 'react';
import "../mainPageComponentsStyle/Table.css"
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTrashAlt } from '@fortawesome/free-solid-svg-icons';
import { faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import CreateHumanBeingForm from "./CreateHumanBeingForm";

const Table = ({ data , role} ) => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [deleteId, setDeleteId] = useState(null);
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [editData, setEditData] = useState(null);

    const userLogin = localStorage.getItem('login');

    const formatDate = (dateArray) => {
        const [year, month, day, hours, minutes, seconds] = dateArray;
        return `${year}.${month.toString().padStart(2, '0')}.${day.toString().padStart(2, '0')} ${hours}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
    };

    const handleEdit = (item) => {
        setEditData(item);
        setIsEditModalOpen(true);
    };

    const handleDelete = (id) => {
        setDeleteId(id);
        setIsModalOpen(true);
    };
    const handleCloseModal = (event) => {
        if (event.target.classList.contains('modal')) {
            setIsModalOpen(false);
        }
    };

    const handleConfirmDelete = async () => {
        setIsModalOpen(false);
        const responce = await fetch('http://localhost:8080/backend-1.0-SNAPSHOT/human/delete', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
            },
            body: JSON.stringify({ id: deleteId }),
        })
            if (responce.ok) {
                setIsModalOpen(false);
            } else {
                console.error('Ошибка:', await responce.json());
            }};


    const handleCancelDelete = () => {
        setIsModalOpen(false);
    };

    return (
        <div className="table-container">
            <table>
                <thead>
                <tr>
                    <th>Id</th>
                    <th>Name</th>
                    <th>Coordinates</th>
                    <th>Creation Date</th>
                    <th>Real Hero</th>
                    <th>Has Toothpick</th>
                    <th>Car Name</th>
                    <th>Mood</th>
                    <th>Impact Speed</th>
                    <th>Minutes of Waiting</th>
                    <th>Weapon Type</th>
                </tr>
                </thead>
                <tbody>
                {data.map((item, index) => (
                    <tr key={index}>
                        <td>{item.id}</td>
                        <td>{item.name}</td>
                        <td>{`x = ${item.coordinates.x}; y = ${item.coordinates.y}`}</td>
                        <td>{formatDate(item.creationDate)}</td>
                        <td>{item.realHero ? 'Yes' : 'No'}</td>
                        <td>{item.hasToothpick ? 'Yes' : 'No'}</td>
                        <td>{item.car ? item.car.name : 'No Car'}</td>
                        <td>{item.mood}</td>
                        <td>{item.impactSpeed}</td>
                        <td>{item.minutesOfWaiting}</td>
                        <td className="lastColumn">
                            {item.weaponType}
                            {item.user.login === userLogin && (
                                <>
                                    <button onClick={() => handleEdit(item)} className="EditButton">
                                        <FontAwesomeIcon icon={faPencilAlt} />
                                    </button>
                                    </>
                                    )}
                                    {(role === 'ADMIN' || item.user.login === userLogin) && (
                                        <>
                                    <button onClick={() => handleDelete(item.id)} className="DeleteButton">
                                        <FontAwesomeIcon icon={faTrashAlt} />
                                    </button>
                                </>
                            )}

                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            {isModalOpen && (
                <div className="modal" onClick={handleCloseModal}>
                    <div className="modal-content">
                        <p>Удалить объект?</p>
                        <button onClick={handleConfirmDelete} className="modal-button">Да</button>
                        <button onClick={handleCancelDelete} className="modal-button">Нет</button>
                    </div>
                </div>
            )}
            {isEditModalOpen && (
                <div className="modal" onClick={() => setIsEditModalOpen(false)}>
                    <div className="modal-content" onClick={e => e.stopPropagation()}>
                        <CreateHumanBeingForm
                            onClose={() => setIsEditModalOpen(false)}
                            initialData={editData}
                            title="Редактировать человеческое существо" // Передаем заголовок
                        />
                    </div>
                </div>
            )}
        </div>
    );

}

export default Table;
