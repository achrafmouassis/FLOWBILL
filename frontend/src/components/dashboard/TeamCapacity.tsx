import { useEffect, useState } from 'react';
import { reportsService } from '../../api/reportsService';
import type { TeamCapacity } from '../../types';

const TeamCapacityWidget = () => {
    const [capacity, setCapacity] = useState<TeamCapacity[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchCapacity = async () => {
            try {
                const data = await reportsService.getTeamCapacity();
                setCapacity(data);
            } catch (error) {
                console.error("Failed to fetch capacity", error);
            } finally {
                setLoading(false);
            }
        };

        fetchCapacity();
    }, []);

    if (loading) return <div className="h-40 animate-pulse bg-gray-100 rounded"></div>;

    return (
        <div className="bg-white p-6 rounded-lg shadow mt-6">
            <h3 className="text-lg font-bold text-gray-800 mb-4">Capacité de l'Équipe (Sprint Actif)</h3>
            <div className="space-y-4">
                {capacity.map((member) => (
                    <div key={member.userId}>
                        <div className="flex justify-between text-sm mb-1">
                            <span className="font-medium text-gray-700">{member.userName}</span>
                            <span className="text-gray-500">{member.taskCount} tâches · {member.totalPoints} pts</span>
                        </div>
                        <div className="w-full bg-gray-200 rounded-full h-2.5">
                            <div
                                className={`h-2.5 rounded-full ${member.capacityPercentage > 100 ? 'bg-red-500' :
                                    member.capacityPercentage > 80 ? 'bg-yellow-500' : 'bg-blue-500'
                                    }`}
                                style={{ width: `${Math.min(member.capacityPercentage, 100)}%` }}
                            ></div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default TeamCapacityWidget;
