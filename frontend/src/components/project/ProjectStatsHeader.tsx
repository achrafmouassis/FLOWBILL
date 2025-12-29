import React from 'react';
import type { ProjectDashboardStats } from '../../types';

interface Props {
    stats: ProjectDashboardStats | null;
}

const ProjectStatsHeader: React.FC<Props> = ({ stats }) => {
    if (!stats) return null;

    const cards = [
        { label: 'Actifs', value: stats.activeCount, color: 'text-green-600', borderColor: 'border-green-200' },
        { label: 'Planifiés', value: stats.plannedCount, color: 'text-blue-600', borderColor: 'border-blue-200' },
        { label: 'En Pause', value: stats.onHoldCount, color: 'text-orange-600', borderColor: 'border-orange-200' },
        { label: 'Terminés', value: stats.completedCount, color: 'text-gray-600', borderColor: 'border-gray-200' },
    ];

    return (
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
            {cards.map((card, i) => (
                <div key={i} className={`bg-white p-4 rounded-xl border-b-4 ${card.borderColor} shadow-sm`}>
                    <p className="text-xs font-medium text-slate-500 uppercase tracking-wider">{card.label}</p>
                    <p className={`text-3xl font-bold mt-1 ${card.color}`}>{card.value}</p>
                </div>
            ))}
        </div>
    );
};

export default ProjectStatsHeader;
