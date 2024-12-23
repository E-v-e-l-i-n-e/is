import React, { useEffect, useState } from 'react';
import "../mainPageComponentsStyle/CreateHumanBeingForm.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faTimes } from "@fortawesome/free-solid-svg-icons";

const CreateHumanBeingForm = ({ onClose, initialData, title }) => {
    const [name, setName] = useState(initialData ? initialData.name : '');
    const [coordinates, setCoordinates] = useState(initialData ? { x: initialData.coordinates.x, y: initialData.coordinates.y } : { x: '', y: '' });
    const [car, setCar] = useState(initialData?.car?.name || '');
    const [realHero, setRealHero] = useState(initialData ? initialData.realHero : false);
    const [hasToothpick, setHasToothpick] = useState(initialData ? initialData.hasToothpick : false);
    const [mood, setMood] = useState(initialData ? initialData.mood : 'SORROW');
    const [weaponType, setWeaponType] = useState(initialData ? initialData.weaponType : 'HAMMER');
    const [impactSpeed, setImpactSpeed] = useState(initialData ? initialData.impactSpeed : '');
    const [minutesOfWaiting, setMinutesOfWaiting] = useState(initialData ? initialData.minutesOfWaiting : '');
    const [carList, setCarList] = useState([]);
    const [selectedCar, setSelectedCar] = useState('');
    const [errors, setErrors] = useState({});

    useEffect(() => {
        const fetchCars = async () => {
            const response = await fetch('http://localhost:8080/backend-1.0-SNAPSHOT/human/cars', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + localStorage.getItem("token"),
                }
            });

            if (!response.ok) {
                const errorData = await response.json();
                console.log("error ", errorData.error);
                return;
            }

            const data = await response.json();

            if (data.cars && Array.isArray(data.cars)) {
                setCarList(data.cars);
            } else {
                console.error("Неверный формат ответа: ", data);
            }
        };

        fetchCars();
    }, []);


    const handleSelectChange = (event) => {
        const carValue = event.target.value;
        setSelectedCar(carValue);
        setCar(carValue);
    };

    const handleInputChange = (event) => {
        setCar(event.target.value);
        setSelectedCar('');
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        const humanBeingData = {
            name,
            coordinates,
            car: car ? { name: car } : null,
            realHero,
            hasToothpick,
            mood,
            weaponType,
            impactSpeed: parseFloat(impactSpeed),
            minutesOfWaiting: parseInt(minutesOfWaiting, 10),
        };

        let response;
        if (initialData) {
            const updatedData = {...humanBeingData, id: initialData.id};
            response = await fetch('http://localhost:8080/backend-1.0-SNAPSHOT/human/update', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + localStorage.getItem("token"),
                },
                body: JSON.stringify(updatedData),
            });
        } else {
            response = await fetch('http://localhost:8080/backend-1.0-SNAPSHOT/human/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + localStorage.getItem("token"),
                },
                body: JSON.stringify(humanBeingData),
            });
        }

        if (response.status === 409) {
            const errorData = await response.json();
            console.log("Ошибка: ", errorData.error);
            setErrors(errorData);
            return
        }
        if (!response.ok) {
            const errorData = await response.json();
            console.log("Ошибка при создании существа: ", errorData.error);
            setErrors(errorData);
            return;
        }

        setErrors({});
        onClose();
    };

    return (
        <div className="form-container">
            <h2>{title || "Создать человеческое существо"}</h2>
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label htmlFor="name">Имя:</label>
                    {errors.name && <div className="error">{errors.name}</div>}
                    <input
                        type="text"
                        id="name"
                        name="name"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="coordinateX">Координата X:</label>
                    {errors.coordinatesX && <div className="error">{errors.coordinatesX}</div>}
                    <input
                        type="number"
                        id="coordinateX"
                        name="coordinateX"
                        value={coordinates.x}
                        onChange={(e) => setCoordinates({ ...coordinates, x: e.target.value })}
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="coordinateY">Координата Y:</label>
                    <input
                        type="number"
                        id="coordinateY"
                        name="coordinateY"
                        value={coordinates.y}
                        onChange={(e) => setCoordinates({ ...coordinates, y: e.target.value })}
                        required
                    />
                </div>
                <div className="form-group" id="car">
                    <label htmlFor="carSelect">Машина:</label>
                    <select
                        id="carSelect"
                        name="carSelect"
                        value="Выберите машину"
                        onChange={handleSelectChange}
                        className="car-select"
                    >
                        <option value="">Выберите машину</option>
                        {carList.map((existingCar, index) => (
                            <option key={index} value={existingCar}>{existingCar}</option>
                        ))}
                    </select>
                    {errors.carName && <div className="error">{errors.carName}</div>}
                    <input
                        type="text"
                        id="newCar"
                        name="newCar"
                        placeholder="Новая машина"
                        value={car}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="realHero">Реальный герой:</label>
                    {errors.realHero && <div className="error">{errors.realHero}</div>}
                    <input
                        type="checkbox"
                        id="realHero"
                        name="realHero"
                        checked={realHero}
                        onChange={(e) => setRealHero(e.target.checked)}
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="hasToothpick">Наличие зубочистки:</label>
                    {errors.hasToothpick && <div className="error">{errors.hasToothpick}</div>}
                    <input
                        type="checkbox"
                        id="hasToothpick"
                        name="hasToothpick"
                        checked={hasToothpick}
                        onChange={(e) => setHasToothpick(e.target.checked)}
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="mood">Настроение:</label>
                    {errors.mood && <div className="error">{errors.mood}</div>}
                    <select
                        id="mood"
                        name="mood"
                        value={mood}
                        className="option"
                        onChange={(e) => setMood(e.target.value)}
                        required
                    >
                        <option value="SORROW">SORROW</option>
                        <option value="LONGING">LONGING</option>
                        <option value="GLOOM">GLOOM</option>
                        <option value="FRENZY">FRENZY</option>
                    </select>
                </div>
                <div className="form-group">
                    <label htmlFor="weaponType">Тип оружия:</label>
                    {errors.weaponType && <div className="error">{errors.weaponType}</div>}
                    <select
                        id="weaponType"
                        name="weaponType"
                        value={weaponType}
                        onChange={(e) => setWeaponType(e.target.value)}
                        required
                    >
                        <option value="HAMMER">HAMMER</option>
                        <option value="PISTOL">PISTOL</option>
                        <option value="SHOTGUN">SHOTGUN</option>
                    </select>
                </div>
                <div className="form-group">
                    <label htmlFor="impactSpeed">Скорость удара:</label>
                    {errors.impactSpeed && <div className="error">{errors.impactSpeed}</div>}
                    <input
                        type="number"
                        id="impactSpeed"
                        name="impactSpeed"
                        step="0.1"
                        value={impactSpeed}
                        onChange={(e) => setImpactSpeed(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="minutesOfWaiting">Минуты ожидания:</label>
                    {errors.minutesOfWaiting && <div className="error">{errors.minutesOfWaiting}</div>}
                    <input
                        type="number"
                        id="minutesOfWaiting"
                        name="minutesOfWaiting"
                        value={minutesOfWaiting}
                        onChange={(e) => setMinutesOfWaiting(e.target.value)}
                        required
                    />
                </div>
                <div className="submit">
                    <button type="submit" className="submit-button">Готово</button>
                </div>
                <button type="button" className="close-button" onClick={onClose}>
                    <FontAwesomeIcon icon={faTimes} style={{ color: '#0600c0', fontSize: '30px' }} />
                </button>
            </form>
        </div>
    );
};

export default CreateHumanBeingForm;
