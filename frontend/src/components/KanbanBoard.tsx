import React from 'react';
import type { Task } from '../types';

interface KanbanBoardProps {
    title: string;
    tasks: Task[];
    onTaskUpdate?: (task: Task) => void;
}

const KanbanBoard: React.FC<KanbanBoardProps> = ({ title, tasks }) => {
    const columns = ['TODO', 'IN_PROGRESS', 'DONE'];

    return (
        <div className="mt-4">
            <h3 className="text-xl font-bold mb-4">{title}</h3>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                {columns.map(status => (
                    <div key={status} className="bg-gray-100 p-4 rounded-lg min-h-[200px]">
                        <h4 className="font-semibold mb-3 text-gray-700">{status.replace('_', ' ')}</h4>
                        <div className="space-y-3">
                            {tasks.filter(t => t.status === status).map(task => (
                                <div key={task.id} className="bg-white p-3 rounded shadow cursor-pointer hover:shadow-md transition">
                                    <div className="flex justify-between items-start">
                                        <h5 className="font-medium text-gray-900">{task.title}</h5>
                                        <span className={`text-xs px-2 py-1 rounded ${task.priority === 'URGENT' ? 'bg-red-100 text-red-800' :
                                            task.priority === 'HIGH' ? 'bg-orange-100 text-orange-800' :
                                                'bg-blue-100 text-blue-800'
                                            }`}>
                                            {task.priority}
                                        </span>
                                    </div>
                                    <p className="text-sm text-gray-500 mt-1 line-clamp-2">{task.description}</p>
                                    <div className="mt-2 flex justify-between items-center text-xs text-gray-400">
                                        <span>{task.estimation ? `${task.estimation} pts` : '-'}</span>
                                        <span>{task.assignedUserId ? `User #${task.assignedUserId}` : 'Unassigned'}</span>
                                    </div>
                                </div>
                            ))}
                            {tasks.filter(t => t.status === status).length === 0 && (
                                <p className="text-gray-400 text-sm italic text-center py-4">No tasks</p>
                            )}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default KanbanBoard;
