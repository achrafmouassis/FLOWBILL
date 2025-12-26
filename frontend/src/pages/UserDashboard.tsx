import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ChangePasswordModal from '../components/ChangePasswordModal';
import { jwtDecode } from 'jwt-decode';

const UserDashboard = () => {
    const [isPasswordModalOpen, setIsPasswordModalOpen] = useState(false);
    const navigate = useNavigate();

    const token = localStorage.getItem('token');
    const decoded: any = token ? jwtDecode(token) : {};
    const email = decoded.sub || '';

    const doLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <nav className="bg-white shadow">
                <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
                    <h1 className="text-xl font-bold text-gray-800">User Dashboard - {email}</h1>
                    <div className="flex items-center space-x-4">
                        <button onClick={() => setIsPasswordModalOpen(true)} className="text-blue-600 hover:text-blue-800">Change Password</button>
                        <button onClick={doLogout} className="text-red-500 hover:text-red-700">Logout</button>
                    </div>
                </div>
            </nav>

            <div className="max-w-7xl mx-auto px-4 py-8">
                <div className="bg-white p-6 rounded-lg shadow">
                    <h2 className="text-xl font-semibold mb-4">Welcome to Flowbill</h2>
                    <p>Select a project or tasks to begin (Feature Coming Soon).</p>
                </div>
            </div>

            <ChangePasswordModal
                isOpen={isPasswordModalOpen}
                onClose={() => setIsPasswordModalOpen(false)}
                email={email}
            />
        </div>
    );
};

export default UserDashboard;
