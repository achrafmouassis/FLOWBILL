import { useEffect, useState } from 'react';
import { reportsService } from '../../api/reportsService';
import type { ActivityLog } from '../../types';

const ActivityTimeline = () => {
    const [activities, setActivities] = useState<ActivityLog[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchActivity = async () => {
            try {
                const data = await reportsService.getActivityTimeline();
                setActivities(data);
            } catch (error) {
                console.error("Failed to fetch activity", error);
            } finally {
                setLoading(false);
            }
        };

        fetchActivity();
    }, []);

    if (loading) return <div className="bg-white p-6 rounded-lg shadow h-64 animate-pulse"></div>;

    const formatDate = (dateStr: string) => {
        const d = new Date(dateStr);
        return d.toLocaleDateString() + ' ' + d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    };

    return (
        <div className="bg-white p-6 rounded-lg shadow h-full">
            <h3 className="text-lg font-bold text-gray-800 mb-4 flex items-center">
                <span className="mr-2">⏱️</span> Activité Récente
            </h3>

            <div className="relative border-l-2 border-gray-200 ml-3 space-y-6 overflow-y-auto max-h-96 pl-6">
                {activities.length === 0 ? (
                    <p className="text-gray-500 text-sm italic">Aucune activité récente.</p>
                ) : (
                    activities.map((activity) => (
                        <div key={activity.id} className="relative">
                            <span className="absolute -left-[31px] top-1 bg-white border-2 border-blue-500 rounded-full w-4 h-4"></span>
                            <div>
                                <p className="text-sm font-semibold text-gray-800">{activity.eventType}</p>
                                <p className="text-sm text-gray-600">{activity.message}</p>
                                <p className="text-xs text-gray-400 mt-1">{formatDate(activity.createdAt)}</p>
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default ActivityTimeline;
