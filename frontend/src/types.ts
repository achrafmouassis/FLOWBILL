export interface Project {
    id: number;
    name: string;
    description?: string;
    code?: string;
    clientName?: string;
    status: string;
    type?: string;
    progress: number;
    startDate?: string;
    targetDate?: string;
    activeSprintsCount?: number;
    taskCount?: number;
    completedTaskCount?: number;
    teamSize?: number;
}

export interface User {
    id: number;
    email: string;
    fullName: string;
    telephone?: string;
    enabled: boolean;
    tenantId: string;
    role: string;
    competenciesMap?: Record<string, string[]>;
}

export interface ProjectMember {
    userId: number;
    fullName: string;
    role: string;
    capacityHours: number;
}

export interface ProjectDetailed extends Project {
    descriptionDetail?: string;
    workflowConfig?: string;
    teamMembers: ProjectMember[];
}

export interface ProjectDashboardStats {
    activeCount: number;
    plannedCount: number;
    onHoldCount: number;
    completedCount: number;
    totalTasks: number;
    teamSize: number;
    activeSprints: number;
}

export interface CreateProjectRequest {
    name: string;
    code?: string;
    description: string;
    descriptionDetail?: string;
    type: string;
    clientName?: string;
    teamMembers: Partial<ProjectMember>[];
    workflowConfig: string;
    startDate: string;
    targetDate: string;
    sprintDurationWeeks: number;
}

export interface Sprint {
    id: number;
    name: string;
    startDate: string;
    endDate: string;
    goal: string;
    status: 'PLANNED' | 'ACTIVE' | 'COMPLETED';
    projectId: number;
}

export interface Task {
    id: number;
    title: string;
    description?: string;
    status: 'TODO' | 'IN_PROGRESS' | 'DONE';
    priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
    estimation?: number;
    dueDate?: string;
    projectId: number;
    sprintId?: number;
    sprintName?: string;
    assignedUserId?: number;
}

export interface TaskRequest {
    title: string;
    description?: string;
    priority?: string;
    estimation?: number;
    dueDate?: string;
    projectId: number;
    assignedUserId?: number;
}

export interface TaskRequest {
    title: string;
    description?: string;
    priority?: string;
    estimation?: number;
    dueDate?: string;
    projectId: number;
    sprintId?: number;
    assignedUserId?: number;
}

export interface GlobalMetrics {
    activeProjects: number;
    activeSprints: number;
    activeTasks: number;
    backlogStories: number;
    tasksCompletedThisWeek: number;
    teamSize: number;
    plannedStories: number;
}

export interface Alert {
    id: string; // alert_blocked_123
    type: 'BLOCKED_TASK' | 'SPRINT_AT_RISK';
    severity: 'CRITICAL' | 'WARNING' | 'INFO';
    message: string;
    entityId: number;
    actions: string[];
}

export interface VelocityReturn {
    sprintName: string;
    committedPoints: number;
    completedPoints: number;
}

export interface TeamCapacity {
    userId: number;
    userName: string;
    totalPoints: number;
    taskCount: number;
    capacityPercentage: number;
}

export interface ActivityLog {
    id: number;
    eventType: string;
    message: string;
    createdAt: string;
    actorUserId?: number;
}

export interface SprintDashboardResponse {
    id: number;
    name: string;
    startDate: string;
    endDate: string;
    status: string;
    progress: number; // 0.0 to 1.0
    completedPoints: number;
    totalPoints: number;
    daysRemaining: number;
    riskStatus: 'GREEN' | 'ORANGE' | 'RED';
}

export interface ProjectDashboardResponse {
    id: number;
    name: string;
    description?: string;
    status: string; // IN_PROGRESS
    progress: number;
    activeSprintsCount: number;
}

export interface UserDashboardPreferences {
    layoutConfig?: string;
    alertThresholds?: string;
    displaySettings?: string;
}
