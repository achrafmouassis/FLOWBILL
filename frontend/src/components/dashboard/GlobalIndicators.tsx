import { useEffect, useState } from 'react';
import { reportsService } from '../../api/reportsService';
import type { GlobalMetrics } from '../../types';

const GlobalIndicators = () => {
    const [metrics, setMetrics] = useState<GlobalMetrics | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchMetrics = async () => {
            try {
                const data = await reportsService.getGlobalMetrics();
                setMetrics(data);
            } catch (error) {
                console.error("Failed to fetch global metrics", error);
            } finally {
                setLoading(false);
            }
        };

        fetchMetrics();
    }, []);

    if (loading) return <div className="animate-pulse h-32 bg-gray-200 rounded-lg"></div>;
    if (!metrics) return <div className="text-red-500">Erreur de chargement</div>;

    const cards = [
        { label: 'Projets Actifs', value: metrics.activeProjects, color: 'bg-blue-500' },
        { label: 'Sprints en Cours', value: metrics.activeSprints, color: 'bg-green-500' },
        { label: 'Tâches Actives', value: metrics.activeTasks, color: 'bg-purple-500' },
        { label: 'Backlog Stories', value: metrics.backlogStories, color: 'bg-yellow-500' },
        { label: 'Tâches Finies (Semaine)', value: metrics.tasksCompletedThisWeek, color: 'bg-indigo-500' },
        { label: 'Taille Équipe', value: metrics.teamSize, color: 'bg-pink-500' },
    ];

    return (
        <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-6 gap-4 mb-8">
            {cards.map((card) => (
                <div key={card.label} className="bg-white p-4 rounded-lg shadow border-l-4 border-l-transparent hover:border-l-blue-500 transition-all">
                    <p className="text-sm text-gray-500 font-medium">{card.label}</p>
                    <p className={`text-2xl font-bold mt-1 ${card.value === 0 ? 'text-gray-300' : 'text-gray-800'}`}>
                        {card.value}
                    </p>
                </div>
            ))}
        </div>
    );
};

export default GlobalIndicators;
