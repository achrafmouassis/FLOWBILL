import api from './api';

export interface User {
    id: number;
    email: string;
    role: string;
    tenantId: string;
}

export const userService = {
    getUsersByTenant: async (tenantId: string) => {
        const response = await api.get<User[]>(`/auth/users/${tenantId}`);
        return response.data;
    }
};
