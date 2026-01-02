import { useState } from 'react';
import { projectService } from '../../api/projectService';
import type { Task } from '../../types';

interface Props {
    task: Task;
    onStatusChange: (task: Task) => void;
}

export const TaskStatusUpdater = ({ task, onStatusChange }: Props) => {
    const [updating, setUpdating] = useState(false);

    const handleStatusChange = async (newStatus: string) => {
        if (newStatus === task.status) return;

        setUpdating(true);
        try {
            // Need to verify if backend supports partial update with just status
            const updatedTask = await projectService.updateTask(task.id, { status: newStatus } as any);
            onStatusChange(updatedTask);
        } catch (error) {
            console.error("Failed to update status", error);
            alert("Failed to update task status");
        } finally {
            setUpdating(false);
        }
    };

    const statusColors: any = {
        'TODO': 'bg-gray-100 text-gray-800',
        'IN_PROGRESS': 'bg-blue-100 text-blue-800',
        'DONE': 'bg-green-100 text-green-800'
    };

    if (updating) return <span className="text-xs text-gray-500">Updating...</span>;

    return (
        <select
            value={task.status}
            onChange={(e) => handleStatusChange(e.target.value)}
            className={`text-xs font-semibold rounded-full px-2 py-1 border-0 cursor-pointer focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 ${statusColors[task.status] || 'bg-gray-100'}`}
        >
            <option value="TODO">TODO</option>
            <option value="IN_PROGRESS">IN PROGRESS</option>
            <option value="DONE">DONE</option>
        </select>
    );
};
