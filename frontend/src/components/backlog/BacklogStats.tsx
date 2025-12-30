import React from 'react';
import { Layers, AlertCircle, CheckCircle, TrendingUp } from 'lucide-react';

interface BacklogStatsProps {
    stats: any; // Using any for MVP simplicity, should match BacklogStatistics
}

const StatCard = ({ label, value, color, icon: Icon }: any) => (
    <div className="bg-white p-3 rounded-xl border border-slate-200 shadow-sm flex flex-col items-center justify-center min-w-[100px]">
        <div className={`text-2xl font-black ${color}`}>{value}</div>
        <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider mt-1 flex items-center gap-1">
            {Icon && <Icon size={10} />}
            {label}
        </div>
    </div>
);

const BacklogStats: React.FC<BacklogStatsProps> = ({ stats }) => {
    if (!stats) return null;

    return (
        <div className="flex flex-wrap gap-4 mb-6">
            <StatCard label="Total" value={stats.totalStories} color="text-slate-800" icon={Layers} />
            <StatCard label="Must Have" value={stats.mustHaveCount} color="text-red-500" icon={AlertCircle} />
            <StatCard label="Should" value={stats.shouldHaveCount} color="text-orange-500" icon={CheckCircle} />
            <StatCard label="Could" value={stats.couldHaveCount} color="text-blue-500" />

            <div className="flex-1 bg-gradient-to-r from-slate-800 to-slate-700 rounded-xl p-4 text-white flex justify-between items-center shadow-lg">
                <div>
                    <div className="text-xs text-slate-300 font-medium mb-1">Score WSJF Moyen</div>
                    <div className="text-2xl font-bold flex items-center gap-2">
                        <TrendingUp size={20} className="text-green-400" />
                        {stats.averageWsjfScore?.toFixed(2) || '0.00'}
                    </div>
                </div>
                <div className="text-right">
                    <div className="text-xs text-slate-300 font-medium mb-1">Total Effort</div>
                    <div className="text-2xl font-bold">{stats.totalStoryPoints || 0} <span className="text-sm font-normal text-slate-400">SP</span></div>
                </div>
            </div>
        </div>
    );
};

export default BacklogStats;
