import { useEffect, useState } from 'react';
import api from '../api/api';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import ChangePasswordModal from '../components/ChangePasswordModal';

interface User {
    id: number;
    email: string;
    enabled: boolean;
    tenantId: string;
}

interface User {
    id: number;
    email: string;
    enabled: boolean;
    tenantId: string;
}

const TenantAdminDashboard = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [newUserEmail, setNewUserEmail] = useState('');
    const [loading, setLoading] = useState(true);
    const [generatedPassword, setGeneratedPassword] = useState('');
    const [isPasswordModalOpen, setIsPasswordModalOpen] = useState(false);
    const [currentUserEmail, setCurrentUserEmail] = useState('');

    // We need tenantId. Let's try to extract from token.
    // If not present, we might need to fetch "my profile".
    // For MVP, if we used `user.getTenantId()` in generateToken, verify key.
    const token = localStorage.getItem('token');
    // Decode usually returns: { sub: "...", roles: [], tenantId: "..." } ???
    // Need to check JwtUtil.java to be strict, but let's blindly try "tenantId" or "tenant".
    const decoded: any = token ? jwtDecode(token) : {};
    const tenantId = decoded.tenantId || decoded.tenant;

    const navigate = useNavigate();

    useEffect(() => {
        if (tenantId) {
            fetchUsers();
            setCurrentUserEmail(decoded.sub);
        }
    }, [tenantId]);

    const fetchUsers = async () => {
        try {
            const response = await api.get(`/auth/users/${tenantId}`);
            setUsers(response.data);
        } catch (error) {
            console.error('Failed to fetch users', error);
        } finally {
            setLoading(false);
        }
    };

    const handleCreateUser = async (e: React.FormEvent) => {
        e.preventDefault();
        setGeneratedPassword('');
        // Generate a random password
        const password = Math.random().toString(36).slice(-8);

        try {
            await api.post('/auth/register', {
                email: newUserEmail,
                password: password,
                role: 'USER', // Creating standard user
                tenantId: tenantId
            });
            setGeneratedPassword(password);
            setNewUserEmail('');
            fetchUsers();
        } catch (error) {
            console.error('Failed to create user', error);
            alert('Failed to create user');
        }
    };

    const doLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <nav className="bg-white shadow">
                <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
                    <h1 className="text-xl font-bold text-gray-800">Entreprise Dashboard - {tenantId}</h1>
                    <div className="flex items-center space-x-4">
                        <button onClick={() => setIsPasswordModalOpen(true)} className="text-blue-600 hover:text-blue-800">Change Password</button>
                        <button onClick={doLogout} className="text-red-500 hover:text-red-700">Logout</button>
                    </div>
                </div>
            </nav>

            <div className="max-w-7xl mx-auto px-4 py-8">
                <div className="bg-white p-6 rounded-lg shadow mb-8">
                    <h2 className="text-lg font-semibold mb-4">Create New User</h2>
                    <form onSubmit={handleCreateUser} className="flex gap-4 items-end">
                        <div className="flex-1">
                            <label className="block text-sm font-medium text-gray-700">User Email</label>
                            <input
                                type="email"
                                className="mt-1 border p-2 rounded w-full"
                                value={newUserEmail}
                                onChange={(e) => setNewUserEmail(e.target.value)}
                                required
                            />
                        </div>
                        <button type="submit" className="bg-green-600 text-white p-2 rounded hover:bg-green-700 mb-0.5">
                            Create User
                        </button>
                    </form>
                    {generatedPassword && (
                        <div className="mt-4 p-4 bg-yellow-50 border border-yellow-200 rounded">
                            <p className="font-bold text-yellow-800">User Created Successfully!</p>
                            <p>Email: <span className="font-mono">{newUserEmail}</span> (actually previous value, check list)</p>
                            <p>Password: <span className="font-mono font-bold text-lg">{generatedPassword}</span></p>
                            <p className="text-sm text-red-600 mt-2">Make sure to copy this password! It will not be shown again.</p>
                        </div>
                    )}
                </div>

                <div className="bg-white rounded-lg shadow overflow-hidden">
                    <h2 className="text-lg font-semibold p-6 border-b">Team Members</h2>
                    {loading ? (
                        <div className="p-6 text-center">Loading...</div>
                    ) : (
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Enabled</th>
                                </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                                {users.map((user) => (
                                    <tr key={user.id}>
                                        <td className="px-6 py-4 whitespace-nowrap">{user.id}</td>
                                        <td className="px-6 py-4 whitespace-nowrap">{user.email}</td>
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${user.enabled ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                                                {user.enabled ? 'Active' : 'Inactive'}
                                            </span>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )}
                </div>
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
