import React, { useEffect, useState } from 'react';
import {
    BarChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer
} from 'recharts';
import { projectService } from '../../api/projectService';
import Spinner from '../Spinner';

interface VelocityChartProps {
    projectId: number;
}

const VelocityChart: React.FC<VelocityChartProps> = ({ projectId }) => {
    const [data, setData] = useState<any>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const result = await projectService.getVelocityChart(projectId);
                // Transform API data structure to Recharts format
                // API: { sprints: [], planned: [], completed: [] }
                // Recharts: [{ name: 'S1', planned: 20, completed: 18 }, ...]
                const chartData = result.sprints.map((sprint: string, index: number) => ({
                    name: sprint,
                    Planned: result.planned[index],
                    Completed: result.completed[index]
                }));
                setData(chartData);
            } catch (err) {
                console.error("Failed to load velocity", err);
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, [projectId]);

    if (loading) return <Spinner />;
    if (!data) return <div className="text-slate-400 text-sm">Aucune donnée disponible</div>;

    return (
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm h-96">
            <h3 className="font-bold text-slate-700 mb-4 flex items-center justify-between">
                <span>Vélocité</span>
                <span className="text-xs bg-green-100 text-green-700 px-2 py-1 rounded-full uppercase">Stable</span>
            </h3>
            <ResponsiveContainer width="100%" height="85%">
                <BarChart data={data}>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#E2E8F0" />
                    <XAxis dataKey="name" axisLine={false} tickLine={false} tick={{ fontSize: 12, fill: '#64748B' }} dy={10} />
                    <YAxis axisLine={false} tickLine={false} tick={{ fontSize: 12, fill: '#64748B' }} />
                    <Tooltip
                        cursor={{ fill: '#F1F5F9' }}
                        contentStyle={{ borderRadius: '12px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)' }}
                    />
                    <Legend iconType="circle" wrapperStyle={{ paddingTop: '20px' }} />
                    <Bar dataKey="Planned" fill="#CBD5E1" radius={[4, 4, 0, 0]} barSize={20} />
                    <Bar dataKey="Completed" fill="#3B82F6" radius={[4, 4, 0, 0]} barSize={20} />
                </BarChart>
            </ResponsiveContainer>
        </div>
    );
};

export default VelocityChart;
