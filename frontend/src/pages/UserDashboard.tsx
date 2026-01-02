import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ChangePasswordModal from '../components/ChangePasswordModal';
import { jwtDecode } from 'jwt-decode';
import { MyTasksList } from '../components/dashboard/MyTasksList';
import { MySprintsList } from '../components/dashboard/MySprintsList';

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
                    <div className="flex items-center space-x-4">
                        <div className="flex-shrink-0">
                            <span className="text-2xl font-bold text-indigo-600">FLOWBILL</span>
                        </div>
                        <h1 className="text-xl font-bold text-gray-800 border-l pl-4 border-gray-200">Developer Dashboard</h1>
                    </div>
                    <div className="flex items-center space-x-4">
                        <span className="text-sm text-gray-500 mr-4">{email}</span>
                        <button onClick={() => setIsPasswordModalOpen(true)} className="text-sm text-blue-600 hover:text-blue-800">Password</button>
                        <button onClick={doLogout} className="text-sm text-red-500 hover:text-red-700 bg-red-50 px-3 py-1 rounded">Logout</button>
                    </div>
                </div>
            </nav>

            <div className="max-w-7xl mx-auto px-4 py-8">
                <div className="mb-8">
                    <h2 className="text-2xl font-bold text-gray-900 mb-2">My Active Sprints</h2>
                    <p className="text-gray-600 mb-4">Track your progress in current sprints.</p>
                    <MySprintsList />
                </div>

                <div className="mb-8">
                    <MyTasksList />
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
