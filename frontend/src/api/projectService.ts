import api from './api';
import type {
    Project,
    Sprint,
    Task,
    TaskRequest,
    ProjectDetailed,
    ProjectDashboardStats,
    CreateProjectRequest,
    Page
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
    startSprint: async (sprintId: number) => {
        const response = await api.post<Sprint>(`/sprints/${sprintId}/start`);
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
    },
    addTaskDependency: async (taskId: number, blockerId: number) => {
        await api.post(`/tasks/${taskId}/dependencies`, null, { params: { blockerId } });
    },
    removeTaskDependency: async (taskId: number, blockerId: number) => {
        await api.delete(`/tasks/${taskId}/dependencies/${blockerId}`);
    },

    // Backlog & Agile
    searchBacklogStories: async (projectId: number, search?: string, moscow?: string, sprintId?: number, unplanned?: boolean, page = 0, size = 10) => {
        const response = await api.get<Page<Task>>(`/projects/${projectId}/backlog/stories`, {
            params: { search, moscow, sprintId, unplanned, page, size }
        });
        return response.data;
    },
    getBacklogStatistics: async (projectId: number) => {
        const response = await api.get<any>(`/projects/${projectId}/backlog/statistics`);
        return response.data;
    },
    createStory: async (projectId: number, request: any) => {
        const response = await api.post<Task>(`/projects/${projectId}/backlog/stories`, request);
        return response.data;
    },
    decomposeStory: async (projectId: number, storyId: number, tasks: TaskRequest[]) => {
        await api.post(`/projects/${projectId}/backlog/stories/${storyId}/tasks/batch`, tasks);
    },

    // Reporting & Dashboard
    getGlobalMetrics: async (projectId: number) => {
        const response = await api.get<any>(`/reports/global-metrics`, { params: { projectId } });
        return response.data;
    },
    getVelocityChart: async (projectId: number) => {
        const response = await api.get<any>(`/reports/velocity`, { params: { projectId } });
        return response.data;
    },
    getTeamLoad: async (projectId: number) => {
        const response = await api.get<any[]>(`/reports/team-load`, { params: { projectId } });
        return response.data;
    }
};
