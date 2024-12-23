import './App.css';
import RegistrationForm from './authentication/RegistrationForm';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import MainPage from './mainPage/MainPage';
function App() {
    return (
        <Router>
            <Routes>
                <Route path="/registration-form" element={<RegistrationForm />} />
                <Route path="/main-page" element={<MainPage />} />
            </Routes>
        </Router>
    );
}

export default App;
