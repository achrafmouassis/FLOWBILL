import { useEffect, useState } from 'react';
import api from '../api/api';
import { useNavigate } from 'react-router-dom';

interface Tenant {
    id: string;
    name: string;
    schemaName: string;
    active: boolean;
    createdAt: string;
}

const SuperAdminDashboard = () => {
    const [tenants, setTenants] = useState<Tenant[]>([]);
    const [newTenant, setNewTenant] = useState({ id: '', name: '', schemaName: '', active: true });
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        fetchTenants();
    }, []);

    const fetchTenants = async () => {
        try {
            const response = await api.get('/tenants');
            setTenants(response.data);
        } catch (error) {
            console.error('Failed to fetch tenants', error);
            if (axios.isAxiosError(error) && error.response?.status === 401) {
                localStorage.removeItem('token');
                navigate('/login');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleCreateTenant = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await api.post('/tenants', newTenant);
            setNewTenant({ id: '', name: '', schemaName: '', active: true });
            fetchTenants(); // Refresh list
        } catch (error) {
            console.error('Failed to create tenant', error);
            alert('Failed to create tenant');
        }
    };

    const doLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    }

    // Need axios for type check above, but imported api. 
    // Workaround: simple check or import axios. 
    // Let's import axios just for types or use api instance.
    // Actually simplest is just to handle error generically.

    return (
        <div className="min-h-screen bg-gray-50">
            <nav className="bg-white shadow">
                <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
                    <h1 className="text-xl font-bold text-gray-800">Super Admin Dashboard</h1>
                    <button onClick={doLogout} className="text-red-500 hover:text-red-700">Logout</button>
                </div>
            </nav>

            <div className="max-w-7xl mx-auto px-4 py-8">

                {/* Create Entreprise Form */}
                <div className="bg-white p-6 rounded-lg shadow mb-8">
                    <h2 className="text-lg font-semibold mb-4">Create New Entreprise</h2>
                    <form onSubmit={handleCreateTenant} className="grid grid-cols-1 md:grid-cols-4 gap-4">
                        <input
                            type="text"
                            placeholder="Entreprise ID (e.g. beta)"
                            className="border p-2 rounded"
                            value={newTenant.id}
                            onChange={(e) => setNewTenant({ ...newTenant, id: e.target.value })}
                            required
                        />
                        <input
                            type="text"
                            placeholder="Name (e.g. Beta Corp)"
                            className="border p-2 rounded"
                            value={newTenant.name}
                            onChange={(e) => setNewTenant({ ...newTenant, name: e.target.value })}
                            required
                        />
                        <input
                            type="text"
                            placeholder="Schema Name (e.g. tenant_beta)"
                            className="border p-2 rounded"
                            value={newTenant.schemaName}
                            onChange={(e) => setNewTenant({ ...newTenant, schemaName: e.target.value })}
                            required
                        />
                        <button type="submit" className="bg-green-600 text-white p-2 rounded hover:bg-green-700">
                            Create Entreprise
                        </button>
                    </form>
                </div>

                {/* Tenant List */}
                <div className="bg-white rounded-lg shadow overflow-hidden">
                    <h2 className="text-lg font-semibold p-6 border-b">Existing Entreprises</h2>
                    {loading ? (
                        <div className="p-6 text-center">Loading...</div>
                    ) : (
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Schema</th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Active</th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Created At</th>
                                </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                                {tenants.map((tenant) => (
                                    <tr key={tenant.id}>
                                        <td className="px-6 py-4 whitespace-nowrap">{tenant.id}</td>
                                        <td className="px-6 py-4 whitespace-nowrap">{tenant.name}</td>
                                        <td className="px-6 py-4 whitespace-nowrap">{tenant.schemaName}</td>
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${tenant.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                                                {tenant.active ? 'Active' : 'Inactive'}
                                            </span>
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{new Date(tenant.createdAt).toLocaleDateString()}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )}
                </div>
            </div>
        </div>
    );
};

import axios from 'axios'; // For isAxiosError check
export default SuperAdminDashboard;
