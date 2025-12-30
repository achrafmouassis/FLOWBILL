import React, { useEffect, useState } from 'react';
import VelocityChart from './VelocityChart';
import TeamLoadWidget from './TeamLoadWidget';
import { projectService } from '../../api/projectService';
import { Clock, ListTodo, Layers } from 'lucide-react';

interface ReportingTabProps {
    projectId: number;
}

const ReportingTab: React.FC<ReportingTabProps> = ({ projectId }) => {
    const [metrics, setMetrics] = useState<any>(null);

    useEffect(() => {
        projectService.getGlobalMetrics(projectId).then(setMetrics).catch(console.error);
    }, [projectId]);

    return (
        <div className="space-y-6">
            {/* KPI Cards */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                <KpiCard
                    label="Projets Actifs"
                    value={metrics?.activeProjects || '-'}
                    icon={Layers}
                    color="bg-blue-50 text-blue-600"
                />
                <KpiCard
                    label="Sprints Actifs"
                    value={metrics?.activeSprintsCount || '-'}
                    icon={Clock}
                    color="bg-purple-50 text-purple-600"
                />
                <KpiCard
                    label="Tâches en Cours"
                    value={metrics?.activeTasks?.inProgress || '-'}
                    subValue={`sur ${metrics?.activeTasks?.total || 0} actives`}
                    icon={ListTodo}
                    color="bg-orange-50 text-orange-600"
                />
                <KpiCard
                    label="Stories Backlog"
                    value={metrics?.backlogStories || '-'}
                    icon={Layers}
                    color="bg-slate-100 text-slate-600"
                />
            </div>

            {/* Charts Row */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                <div className="lg:col-span-2">
                    <VelocityChart projectId={projectId} />
                </div>
                <div className="lg:col-span-1">
                    <TeamLoadWidget projectId={projectId} />
                </div>
            </div>
        </div>
    );
};

const KpiCard = ({ label, value, subValue, icon: Icon, color }: any) => (
    <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-start justify-between">
        <div>
            <div className="text-slate-400 text-xs font-bold uppercase mb-1">{label}</div>
            <div className="text-3xl font-black text-slate-800">{value}</div>
            {subValue && <div className="text-slate-400 text-xs font-medium mt-1">{subValue}</div>}
        </div>
        <div className={`p-3 rounded-xl ${color}`}>
            <Icon size={20} />
        </div>
    </div>
);

export default ReportingTab;
