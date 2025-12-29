import { useEffect, useState } from 'react';
import { reportsService } from '../../api/reportsService';
import type { VelocityReturn } from '../../types';

const VelocityChart = () => {
    const [data, setData] = useState<VelocityReturn[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchVelocity = async () => {
            try {
                const velocityData = await reportsService.getVelocityChart();
                setData(velocityData);
            } catch (error) {
                console.error("Failed to fetch velocity", error);
            } finally {
                setLoading(false);
            }
        };

        fetchVelocity();
    }, []);

    if (loading) return <div className="h-64 animate-pulse bg-gray-100 rounded"></div>;
    if (data.length === 0) return <div className="text-gray-500 italic">Pas assez de données pour afficher la vélocité.</div>;

    const maxPoints = Math.max(...data.flatMap(d => [d.committedPoints, d.completedPoints]), 10);

    return (
        <div className="bg-white p-6 rounded-lg shadow">
            <h3 className="text-lg font-bold text-gray-800 mb-6">Vélocité de l'Équipe</h3>
            <div className="flex items-end space-x-8 h-64 border-b border-gray-200 pb-2">
                {data.map((item) => (
                    <div key={item.sprintName} className="flex flex-col items-center flex-1 group">
                        <div className="w-full flex justify-center space-x-2 items-end h-full">
                            {/* Committed Bar */}
                            <div
                                style={{ height: `${(item.committedPoints / maxPoints) * 100}%` }}
                                className="w-6 bg-gray-300 rounded-t relative group-hover:opacity-80 transition-all"
                                title={`Engagé: ${item.committedPoints}`}
                            >
                                <span className="absolute -top-6 text-xs text-gray-600 w-full text-center">{item.committedPoints}</span>
                            </div>
                            {/* Completed Bar */}
                            <div
                                style={{ height: `${(item.completedPoints / maxPoints) * 100}%` }}
                                className="w-6 bg-green-500 rounded-t relative group-hover:opacity-80 transition-all"
                                title={`Complété: ${item.completedPoints}`}
                            >
                                <span className="absolute -top-6 text-xs text-green-700 font-bold w-full text-center">{item.completedPoints}</span>
                            </div>
                        </div>
                        <p className="mt-2 text-xs font-medium text-gray-600 truncate w-full text-center">{item.sprintName}</p>
                    </div>
                ))}
            </div>
            <div className="mt-4 flex justify-center space-x-6 text-sm">
                <div className="flex items-center">
                    <span className="w-3 h-3 bg-gray-300 rounded mr-2"></span>
                    <span className="text-gray-600">Points Engagés</span>
                </div>
                <div className="flex items-center">
                    <span className="w-3 h-3 bg-green-500 rounded mr-2"></span>
                    <span className="text-gray-600">Points Complétés</span>
                </div>
            </div>
        </div>
    );
};

export default VelocityChart;
