import api from './api';
import type {
    GlobalMetrics,
    Alert,
    VelocityReturn,
    TeamCapacity,
    ActivityLog,
    UserDashboardPreferences
} from '../types';

export const reportsService = {
    getGlobalMetrics: async () => {
        const response = await api.get<GlobalMetrics>('/reports/global-metrics');
        return response.data;
    },
    getAlerts: async () => {
        const response = await api.get<Alert[]>('/reports/alerts');
        return response.data;
    },
    getVelocityChart: async () => {
        const response = await api.get<VelocityReturn[]>('/reports/velocity-chart');
        return response.data;
    },
    getTeamCapacity: async () => {
        const response = await api.get<TeamCapacity[]>('/reports/team-capacity');
        return response.data;
    },
    getActivityTimeline: async () => {
        const response = await api.get<ActivityLog[]>('/activity/timeline');
        return response.data;
    },
    getActiveProjects: async () => {
        const response = await api.get<any[]>('/projects/active-dashboard');
        return response.data;
    },
    getActiveSprints: async () => {
        const response = await api.get<any[]>('/sprints/active-dashboard');
        return response.data;
    },
    completeSprint: async (sprintId: number, data: any) => {
        const response = await api.post(`/sprints/${sprintId}/complete`, data);
        return response.data;
    }
};

export const preferencesService = {
    getPreferences: async () => {
        const response = await api.get<UserDashboardPreferences>('/preferences');
        return response.data;
    },
    savePreferences: async (prefs: UserDashboardPreferences) => {
        await api.put('/preferences', prefs);
    }
};
