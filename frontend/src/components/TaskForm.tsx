import React, { useState, useEffect } from 'react';
import type { Sprint, TaskRequest } from '../types';
import { projectService } from '../api/projectService';

interface TaskFormProps {
    projectId: number;
    onTaskCreated: () => void;
}

const TaskForm: React.FC<TaskFormProps> = ({ projectId, onTaskCreated }) => {
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [priority, setPriority] = useState('MEDIUM');
    const [estimation, setEstimation] = useState<number>(0);
    const [sprintId, setSprintId] = useState<number | null>(null);
    const [sprints, setSprints] = useState<Sprint[]>([]);

    useEffect(() => {
        projectService.getSprints(projectId).then(setSprints).catch(console.error);
    }, [projectId]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        const task: TaskRequest = {
            title,
            description,
            priority,
            estimation,
            projectId,
            sprintId: sprintId || undefined, // Send undefined if null
        };
        try {
            await projectService.createTask(task);
            setTitle('');
            setDescription('');
            setEstimation(0);
            onTaskCreated();
        } catch (err) {
            console.error(err);
            alert('Failed to create task');
        }
    };

    return (
        <form onSubmit={handleSubmit} className="bg-white p-4 rounded shadow-md mb-6 border border-gray-200">
            <h3 className="font-bold text-gray-700 mb-4">Create New Task</h3>
            <div className="grid grid-cols-2 gap-4">
                <div className="col-span-2">
                    <label className="block text-sm font-medium text-gray-700">Title</label>
                    <input type="text" required value={title} onChange={e => setTitle(e.target.value)} className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm p-2 border" />
                </div>
                <div className="col-span-2">
                    <label className="block text-sm font-medium text-gray-700">Description</label>
                    <textarea value={description} onChange={e => setDescription(e.target.value)} className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm p-2 border" />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">Priority</label>
                    <select value={priority} onChange={e => setPriority(e.target.value)} className="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border">
                        <option value="LOW">Low</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="HIGH">High</option>
                        <option value="URGENT">Urgent</option>
                    </select>
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">Points</label>
                    <input type="number" value={estimation} onChange={e => setEstimation(Number(e.target.value))} className="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border" />
                </div>
                <div className="col-span-2">
                    <label className="block text-sm font-medium text-gray-700">Sprint (Optional)</label>
                    <select value={sprintId || ''} onChange={e => setSprintId(e.target.value ? Number(e.target.value) : null)} className="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border">
                        <option value="">-- No Sprint (Backlog) --</option>
                        {sprints.map(s => (
                            <option key={s.id} value={s.id}>
                                {s.name} ({s.status})
                            </option>
                        ))}
                    </select>
                </div>
            </div>
            <button type="submit" className="mt-4 bg-indigo-600 text-white px-4 py-2 rounded hover:bg-indigo-700">Create Task</button>
        </form>
    );
};

export default TaskForm;
