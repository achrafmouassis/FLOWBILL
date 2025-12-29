import api from './api';
import type {
    Project,
    Sprint,
    Task,
    TaskRequest,
    ProjectDetailed,
    ProjectDashboardStats,
    CreateProjectRequest
} from '../types';

export const projectService = {
    getProjects: async () => {
        const response = await api.get<Project[]>('/projects');
        return response.data;
    },
    // New Search / Filter List
    searchProjects: async (query?: string, status?: string, type?: string) => {
        const response = await api.get<Project[]>('/projects/search', {
            params: { query, status, type }
        });
        return response.data;
    },
    getProjectStats: async () => {
        const response = await api.get<ProjectDashboardStats>('/projects/stats');
        return response.data;
    },
    getProjectDetail: async (id: number) => {
        const response = await api.get<ProjectDetailed>(`/projects/${id}`);
        return response.data;
    },
    createProjectWizard: async (request: CreateProjectRequest) => {
        const response = await api.post<ProjectDetailed>('/projects/wizard', request);
        return response.data;
    },

    // Sprints & Tasks
    getSprints: async (projectId: number) => {
        const response = await api.get<Sprint[]>(`/projects/${projectId}/sprints`);
        return response.data;
    },
    createProject: async (project: { name: string; description: string }) => {
        const response = await api.post<Project>('/projects', project);
        return response.data;
    },
    createSprint: async (projectId: number, sprint: any) => {
        const response = await api.post<Sprint>(`/projects/${projectId}/sprints`, sprint);
        return response.data;
    },
    getSprintTasks: async (projectId: number, sprintId: number) => {
        const response = await api.get<Task[]>(`/projects/${projectId}/sprints/${sprintId}/tasks`);
        return response.data;
    },
    getBacklogTasks: async (projectId: number) => {
        const response = await api.get<Task[]>(`/tasks/backlog?projectId=${projectId}`);
        return response.data;
    },
    createTask: async (task: TaskRequest) => {
        const response = await api.post<Task>('/tasks', task);
        return response.data;
    },
    updateTask: async (taskId: number, task: Partial<TaskRequest>) => {
        const response = await api.put<Task>(`/tasks/${taskId}`, task);
        return response.data;
    }
};
