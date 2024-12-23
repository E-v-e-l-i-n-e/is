import React, {useState} from 'react';
import './RegistrationForm.css';
import {useNavigate} from 'react-router-dom';

const RegistrationForm = () => {
    const navigate = useNavigate();
    const [isLogin, setIsLogin] = useState(true);
    const [formData, setFormData] = useState({
        username: '',
        password: '',
        confirmPassword: '',
        isWaitingAdmin: false
    });

    const handleChange = (e) => {
        const {name, value, type, checked} = e.target;
        setFormData((prevState) => ({
            ...prevState,
            [name]: type === 'checkbox' ? checked : value
        }));
        setErrorMessage('');
    };

    const handleValidation = () => {
        const {username, password, confirmPassword} = formData;
        if (username.length < 4) {
            return 'Длина логина меньше 4';
        }
        if (password.length < 5) {
            return 'Длина пароля меньше 5';
        }
        if (!isLogin && password !== confirmPassword) {
            return 'Пароли не совпадают';
        }
        return null;
    };
    const handleSubmit = async (e) => {
        e.preventDefault();
        const url = isLogin
            ? 'http://localhost:8080/backend-1.0-SNAPSHOT/auth/sign-up'
            : 'http://localhost:8080/backend-1.0-SNAPSHOT/auth/sign-in';
        const validationError = handleValidation();
        if (validationError) {
            setErrorMessage(validationError);
            return;
        }
        try {
            const dataToSend = {
                login: formData.username,
                password: formData.password,
                isWaitingAdmin: formData.isWaitingAdmin
            };
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(dataToSend),
            });
            console.log('Form submitted:', dataToSend);

            if (!response.ok) {
                const errorData = await response.json();
                const errorMessage = errorData.error;
                setErrorMessage(errorMessage);
                console.log(errorMessage);
            } else {
                setErrorMessage('')
                const data = await response.json();
                localStorage.setItem('token', data.token);
                localStorage.setItem('role', data.role);
                localStorage.setItem('login', formData.username)
                localStorage.setItem('isWaitingAdmin', data.isWaitingAdmin)
                console.log(data.token)
                console.log(data.role)
                console.log(formData.username)
                navigate('/main-page');
            }

        } catch (error) {
            console.error('Ошибка:', error);
        }
    };

    const errorMessageStyle = {
        top: isLogin ? '195px' : '155px'
    };

    const toggleForm = (isLogin) => {
        setIsLogin(isLogin);
        setErrorMessage('');
    };
    const [errorMessage, setErrorMessage] = useState('');

    return (
        <div className="FormContainer">
            <div className="button-container">
                <button
                    className={`ToggleButton ${isLogin ? 'active' : ''}`}
                    onClick={() => toggleForm(true)}
                    disabled={isLogin}
                >
                    Вход
                </button>
                <button
                    className={`ToggleButton ${!isLogin ? 'active' : ''}`}
                    onClick={() => toggleForm(false)}
                    disabled={!isLogin}
                >
                    Регистрация
                </button>
            </div>
            {errorMessage && <div className="error-message" style={errorMessageStyle}>{errorMessage}</div>}
            {isLogin ? (
                <form onSubmit={handleSubmit} id='form1'>
                    <div class="input-container">
                        <label>Логин</label>
                        <input
                            className='login'
                            type="text"
                            name="username"
                            value={formData.username}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div class="input-container">
                        <label>Пароль</label>
                        <input
                            className='password'
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <button type="submit">Отправить</button>
                </form>
            ) : (
                <form onSubmit={handleSubmit}>
                    <div className="input-container">
                        <label>Логин</label>
                        <input
                            className='login'
                            type="text"
                            name="username"
                            value={formData.username}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div className="input-container">
                        <label>Пароль</label>
                        <input
                            className='password'
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div className="input-container">
                        <label>Подтвердите пароль</label>
                        <input
                            className='password'
                            type="password"
                            name="confirmPassword"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div id="isAdmin">
                        <input
                            type="checkbox"
                            name="isWaitingAdmin"
                            checked={formData.isWaitingAdmin}
                            onChange={handleChange}
                        />
                        <label htmlFor="isAdmin">Зарегистрироваться как администратор</label>
                    </div>
                    <button type="submit">Отправить</button>
                </form>
            )}
        </div>
    );
};

export default RegistrationForm;
