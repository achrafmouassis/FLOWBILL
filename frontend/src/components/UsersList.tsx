import React, { useEffect, useState } from 'react';
import { userService } from '../api/userService';
import type { User } from '../api/userService';
import Spinner from './Spinner';

interface UsersListProps {
    tenantId?: string; // We might need to extract this from token
}

const UsersList: React.FC<UsersListProps> = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Decode token to get tenantId? Or just assume current user's tenant context via API?
        // The API /auth/users/{tenantId} requires tenantId.
        // Let's decode token.
        const token = localStorage.getItem('token');
        if (token) {
            try {
                const payload = JSON.parse(atob(token.split('.')[1]));
                // Assuming payload has tenantId, sometimes it's 'tenant_id' or inside 'sub'. 
                // Based on standard JWT from Auth Service:
                // We need to check how Auth Service puts tenantId in token.
                // For MVP, we'll try to use a hardcoded or extracted value.
                // Actually, let's use a safe fallback or fetch "my profile".

                // HACK: For MVP, let's use the one from payload 'tenantId' claim
                const tenantId = payload.tenantId;
                if (tenantId) {
                    userService.getUsersByTenant(tenantId)
                        .then(setUsers)
                        .catch(err => console.error(err))
                        .finally(() => setLoading(false));
                } else {
                    console.error("No tenantId in token");
                    setLoading(false);
                }
            } catch (e) {
                console.error("Invalid token", e);
                setLoading(false);
            }
        }
    }, []);

    if (loading) return <Spinner />;

    return (
        <div className="bg-white shadow rounded-lg overflow-hidden">
            <div className="px-6 py-4 border-b border-gray-200">
                <h3 className="text-lg font-medium text-gray-900">Team Members</h3>
            </div>
            <ul className="divide-y divide-gray-200">
                {users.map(user => (
                    <li key={user.id} className="px-6 py-4 flex items-center justify-between hover:bg-gray-50">
                        <div>
                            <p className="text-sm font-medium text-indigo-600">{user.email}</p>
                            <p className="text-xs text-gray-500">{user.role}</p>
                        </div>
                        <span className="text-xs bg-green-100 text-green-800 px-2 py-1 rounded-full">Active</span>
                    </li>
                ))}
                {users.length === 0 && (
                    <li className="px-6 py-10 text-center text-gray-500">
                        No other users found.
                    </li>
                )}
            </ul>
        </div>
    );
};

export default UsersList;
