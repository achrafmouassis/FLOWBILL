import { useState, useEffect } from 'react';
import { projectService } from '../../api/projectService';
import type { Task } from '../../types';
import Spinner from '../Spinner';
import { TaskStatusUpdater } from './TaskStatusUpdater';

const DUMMY_TASKS: Task[] = [
    { id: 1, title: 'Implement Auth', type: 'STORY', status: 'IN_PROGRESS', priority: 'HIGH', projectId: 1, project: { id: 1, name: 'Alpha Banking' }, sprint: { id: 1, name: 'Sprint 1' } },
    { id: 2, title: 'Design Database', type: 'TASK', status: 'DONE', priority: 'URGENT', projectId: 1, project: { id: 1, name: 'Alpha Banking' }, sprint: { id: 1, name: 'Sprint 1' } },
    { id: 3, title: 'Setup CI/CD', type: 'TASK', status: 'TODO', priority: 'MEDIUM', projectId: 2, project: { id: 2, name: 'Beta Logistics' }, sprint: { id: 1, name: 'Sprint 1' } },
];

export const MyTasksList = () => {
    const [tasks, setTasks] = useState<Task[]>(DUMMY_TASKS);
    const [loading, setLoading] = useState(true);
    const [filter, setFilter] = useState<string>('ALL');

    const fetchTasks = async () => {
        setLoading(true);
        try {
            const data = await projectService.getMyTasks();
            if (data && data.length > 0) {
                setTasks(data);
            }
        } catch (error) {
            console.error("Failed to fetch tasks", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchTasks();
    }, []);

    const filteredTasks = tasks.filter(t => {
        if (filter === 'ALL') return true;
        return t.status === filter;
    });

    if (loading) return <Spinner />;

    return (
        <div className="bg-white p-6 rounded-lg shadow mt-6">
            <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-bold text-gray-800">My Tasks</h3>
                <div className="flex space-x-2">
                    {['ALL', 'TODO', 'IN_PROGRESS', 'DONE'].map(status => (
                        <button
                            key={status}
                            onClick={() => setFilter(status)}
                            className={`px-3 py-1 rounded text-sm ${filter === status ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-700'}`}
                        >
                            {status.replace('_', ' ')}
                        </button>
                    ))}
                </div>
            </div>

            <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                        <tr>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Title</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Project</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Sprint</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Priority</th>
                        </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                        {filteredTasks.length === 0 ? (
                            <tr>
                                <td colSpan={5} className="px-6 py-4 text-center text-gray-500">No tasks found.</td>
                            </tr>
                        ) : (
                            filteredTasks.map(task => (
                                <tr key={task.id} className="hover:bg-gray-50">
                                    <td className="px-6 py-4">
                                        <div className="text-sm font-medium text-gray-900">{task.title}</div>
                                        <div className="text-sm text-gray-500">{task.id} - {task.type}</div>
                                    </td>
                                    <td className="px-6 py-4 text-sm text-gray-500">
                                        {task.project?.name || '-'}
                                    </td>
                                    <td className="px-6 py-4 text-sm text-gray-500">
                                        {task.sprint?.name || '-'}
                                    </td>
                                    <td className="px-6 py-4">
                                        <TaskStatusUpdater
                                            task={task}
                                            onStatusChange={(updated) => {
                                                setTasks(prev => prev.map(t => t.id === updated.id ? updated : t));
                                            }}
                                        />
                                    </td>
                                    <td className="px-6 py-4 text-sm text-gray-500">
                                        {task.priority}
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};
