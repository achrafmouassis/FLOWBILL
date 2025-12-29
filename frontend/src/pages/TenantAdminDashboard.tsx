import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import ChangePasswordModal from '../components/ChangePasswordModal';
import TeamManagement from '../components/TeamManagement';
import Overview from '../components/dashboard/Overview';
import ActiveSprints from '../components/dashboard/ActiveSprints';
import ProjectSection from '../components/project/ProjectSection';

const TenantAdminDashboard = () => {
    const [isPasswordModalOpen, setIsPasswordModalOpen] = useState(false);
    const [activeTab, setActiveTab] = useState('overview');
    const [currentUserEmail, setCurrentUserEmail] = useState('');

    const token = localStorage.getItem('token');
    const decoded: any = token ? jwtDecode(token) : {};
    const tenantId = decoded.tenantId || decoded.tenant;

    const navigate = useNavigate();

    useEffect(() => {
        if (decoded.sub) {
            setCurrentUserEmail(decoded.sub);
        }
    }, [decoded.sub]);

    const doLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <nav className="bg-white shadow">
                <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
                    <div className="flex items-center space-x-4">
                        <h1 className="text-xl font-bold text-gray-800">FLOWBILL - {tenantId}</h1>
                    </div>
                    <div className="flex items-center space-x-4">
                        <button onClick={() => setIsPasswordModalOpen(true)} className="text-blue-600 hover:text-blue-800 text-sm">
                            Changer mot de passe
                        </button>
                        <button onClick={doLogout} className="text-red-500 hover:text-red-700 text-sm font-medium">
                            Déconnexion
                        </button>
                    </div>
                </div>
            </nav>

            {/* Tabs Header */}
            <div className="bg-white border-b border-gray-200">
                <div className="max-w-7xl mx-auto px-4">
                    <div className="flex -mb-px space-x-8 overflow-x-auto">
                        {['overview', 'projects', 'sprints', 'team'].map((tab) => (
                            <button
                                key={tab}
                                onClick={() => setActiveTab(tab)}
                                className={`
                            whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm capitalize
                            ${activeTab === tab
                                        ? 'border-blue-500 text-blue-600'
                                        : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                                    }
                        `}
                            >
                                {tab}
                            </button>
                        ))}
                    </div>
                </div>
            </div>

            <div className="max-w-7xl mx-auto px-4 py-6">
                {activeTab === 'overview' && <Overview />}
                {activeTab === 'projects' && <ProjectSection />}
                {activeTab === 'sprints' && <ActiveSprints />}
                {activeTab === 'team' && <TeamManagement />}
            </div>

            <ChangePasswordModal
                isOpen={isPasswordModalOpen}
                onClose={() => setIsPasswordModalOpen(false)}
                email={currentUserEmail}
            />
        </div>
    );
};

export default TenantAdminDashboard;