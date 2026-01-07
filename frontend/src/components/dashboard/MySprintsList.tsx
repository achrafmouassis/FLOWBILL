import { useState, useEffect } from 'react';
import { projectService } from '../../api/projectService';
import type { MySprint } from '../../types';
import Spinner from '../Spinner';

const DUMMY_SPRINTS: MySprint[] = [
    { id: 1, name: 'Sprint 1', status: 'ACTIVE', goal: 'Initial Setup', myCompletionRate: 65, myCompletedTasks: 5, myTotalTasks: 8, myRemainingHours: 12, project: { id: 1, name: 'Alpha Banking' } },
    { id: 2, name: 'Sprint 2', status: 'PLANNED', goal: 'Core Features', myCompletionRate: 0, myCompletedTasks: 0, myTotalTasks: 5, myRemainingHours: 20, project: { id: 2, name: 'Beta Logistics' } },
];

export const MySprintsList = () => {
    const [sprints, setSprints] = useState<MySprint[]>(DUMMY_SPRINTS);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchSprints = async () => {
            try {
                const data = await projectService.getMySprints();
                if (data && data.length > 0) {
                    setSprints(data);
                }
            } catch (error) {
                console.error("Failed to fetch sprints", error);
            } finally {
                setLoading(false);
            }
        };
        fetchSprints();
    }, []);

    if (loading) return <Spinner />;

    return (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-8">
            {sprints.map(sprint => (
                <div key={sprint.id} className="bg-white rounded-lg shadow p-4 border-l-4 border-indigo-500">
                    <div className="flex justify-between items-start mb-2">
                        <div>
                            <h4 className="text-lg font-bold text-gray-900">{sprint.name}</h4>
                            <p className="text-sm text-gray-500">{sprint.project?.name}</p>
                        </div>
                        <span className={`px-2 py-1 text-xs rounded-full ${sprint.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'}`}>
                            {sprint.status}
                        </span>
                    </div>

                    <div className="mt-3">
                        <div className="flex justify-between text-sm mb-1">
                            <span className="text-gray-600">My Progress</span>
                            <span className="font-medium">{sprint.myCompletionRate}%</span>
                        </div>
                        <div className="w-full bg-gray-200 rounded-full h-2">
                            <div className="bg-indigo-600 h-2 rounded-full" style={{ width: `${sprint.myCompletionRate}%` }}></div>
                        </div>
                    </div>

                    <div className="mt-4 grid grid-cols-2 gap-2 text-center">
                        <div className="bg-gray-50 p-2 rounded">
                            <div className="text-xl font-bold text-gray-800">{sprint.myCompletedTasks}/{sprint.myTotalTasks}</div>
                            <div className="text-xs text-gray-500">Tasks Done</div>
                        </div>
                        <div className="bg-gray-50 p-2 rounded">
                            <div className="text-xl font-bold text-gray-800">{sprint.myRemainingHours}</div>
                            <div className="text-xs text-gray-500">Rem. Points</div>
                        </div>
                    </div>
                </div>
            ))}
            {sprints.length === 0 && (
                <div className="col-span-full md:col-span-2 lg:col-span-3 bg-white p-6 rounded-lg shadow text-center text-gray-500">
                    No active sprints found.
                </div>
            )}
        </div>
    );
};
