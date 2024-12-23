import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTimes, faCheck } from '@fortawesome/free-solid-svg-icons';
import '../mainPage/MainPage.css';

const RequestModal = ({ closeRequestModal, waitingUsers, setWaitingUsers }) => {
    const handleApprove = async (login) => {
        const response = await fetch('http://localhost:8080/backend-1.0-SNAPSHOT/admin/accept-admin', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ login })
        });

        if (response.ok) {
            console.log(`Заявка от ${login} принята`);
            setWaitingUsers(prev => prev.filter(user => user !== login));
        } else {
            console.error('Ошибка:', await response.json());
        }
    };

    const handleReject = async (login) => {
        const response = await fetch('http://localhost:8080/backend-1.0-SNAPSHOT/admin/reject-admin', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ login })
        });

        if (response.ok) {
            console.log(`Заявка от ${login} отклонена`);
            setWaitingUsers(prev => prev.filter(user => user !== login));
        } else {
            console.error('Ошибка:', await response.json());
        }
    };

    return (
        <div className="request-modal">
            <button className="close-button" onClick={closeRequestModal}>
                <FontAwesomeIcon icon={faTimes} style={{ color: 'black', fontSize: '35px' }} />
            </button>
            {waitingUsers.length === 0 ? (
                <p>Нет заявок</p>
            ) : (
                <ul>
                    {waitingUsers.map((login, index) => (
                        <li key={index} style={{ display: 'flex', alignItems: 'center' }}>
                            {login}
                            <button className="accept-button" onClick={() => handleApprove(login)}>
                                <FontAwesomeIcon icon={faCheck} style={{ color: 'black', fontSize: '20px' }} />
                            </button>
                            <button className="reject-button" onClick={() => handleReject(login)} style={{ marginLeft: '5px', color: 'white' }}>
                                <FontAwesomeIcon icon={faTimes} style={{ color: 'white', fontSize: '20px' }} />
                            </button>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default RequestModal;
