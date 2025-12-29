import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { projectService } from '../api/projectService';
import type { Sprint, Task } from '../types';
import KanbanBoard from '../components/KanbanBoard';
import TaskForm from '../components/TaskForm';

const ProjectDetailsPage: React.FC = () => {
    const { projectId } = useParams<{ projectId: string }>();
    const pid = Number(projectId);

    const [sprints, setSprints] = useState<Sprint[]>([]);
    const [backlogTasks, setBacklogTasks] = useState<Task[]>([]);
    const [selectedSprintId, setSelectedSprintId] = useState<number | null>(null);
    const [sprintTasks, setSprintTasks] = useState<Task[]>([]);
    const [viewMode, setViewMode] = useState<'BACKLOG' | 'SPRINT'>('SPRINT');

    const loadData = () => {
        if (!pid) return;

        projectService.getSprints(pid).then(data => {
            setSprints(data);
            if (data.length > 0 && !selectedSprintId) {
                setSelectedSprintId(data[0].id);
            }
        });

        projectService.getBacklogTasks(pid).then(setBacklogTasks);
    };

    useEffect(() => {
        loadData();
    }, [pid]);

    useEffect(() => {
        if (selectedSprintId && viewMode === 'SPRINT') {
            projectService.getSprintTasks(pid, selectedSprintId).then(setSprintTasks);
        }
    }, [selectedSprintId, viewMode, pid]);

    return (
        <div className="p-6 bg-gray-50 min-h-screen">
            <h1 className="text-3xl font-bold text-gray-900 mb-6">Project Board</h1>

            <div className="flex gap-4 mb-6">
                <button
                    onClick={() => setViewMode('SPRINT')}
                    className={`px-4 py-2 rounded font-medium ${viewMode === 'SPRINT' ? 'bg-indigo-600 text-white' : 'bg-white text-gray-700 hover:bg-gray-100'}`}
                >
                    Active Sprints
                </button>
                <button
                    onClick={() => setViewMode('BACKLOG')}
                    className={`px-4 py-2 rounded font-medium ${viewMode === 'BACKLOG' ? 'bg-indigo-600 text-white' : 'bg-white text-gray-700 hover:bg-gray-100'}`}
                >
                    Backlog & Kanban
                </button>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
                <div className="lg:col-span-1">
                    <TaskForm projectId={pid} onTaskCreated={() => { loadData(); if (selectedSprintId && viewMode === 'SPRINT') projectService.getSprintTasks(pid, selectedSprintId).then(setSprintTasks); }} />
                </div>

                <div className="lg:col-span-3">
                    {viewMode === 'SPRINT' && sprints.length > 0 ? (
                        <div>
                            <div className="mb-4">
                                <label className="mr-2 font-semibold">Select Sprint:</label>
                                <select
                                    className="p-2 border rounded"
                                    value={selectedSprintId || ''}
                                    onChange={(e) => setSelectedSprintId(Number(e.target.value))}
                                >
                                    {sprints.map(s => <option key={s.id} value={s.id}>{s.name} ({s.status})</option>)}
                                </select>
                            </div>
                            <KanbanBoard
                                title={`Sprint: ${sprints.find(s => s.id === selectedSprintId)?.name}`}
                                tasks={sprintTasks}
                            />
                        </div>
                    ) : viewMode === 'SPRINT' ? (
                        <div className="bg-white p-8 text-center rounded text-gray-500">
                            No sprints found. Create a sprint to get started.
                        </div>
                    ) : (
                        <KanbanBoard
                            title="Backlog / General Kanban"
                            tasks={backlogTasks}
                        />
                    )}
                </div>
            </div>
        </div>
    );
};

export default ProjectDetailsPage;
