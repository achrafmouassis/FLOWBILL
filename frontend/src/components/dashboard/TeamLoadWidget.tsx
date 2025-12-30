import React, { useEffect, useState } from 'react';
import { projectService } from '../../api/projectService';
import Spinner from '../Spinner';

interface TeamLoadWidgetProps {
    projectId: number;
}

const TeamLoadWidget: React.FC<TeamLoadWidgetProps> = ({ projectId }) => {
    const [members, setMembers] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadData = async () => {
            try {
                const data = await projectService.getTeamLoad(projectId);
                setMembers(data);
            } catch (err) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
        loadData();
    }, [projectId]);

    if (loading) return <Spinner />;

    return (
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
            <h3 className="font-bold text-slate-700 mb-6">Charge Équipe</h3>
            <div className="space-y-6">
                {members.map((member) => (
                    <div key={member.userId}>
                        <div className="flex justify-between items-center mb-2">
                            <div className="flex items-center gap-3">
                                <div className="w-8 h-8 rounded-full bg-slate-100 flex items-center justify-center text-slate-500 font-bold text-xs">
                                    {member.name.charAt(0)}
                                </div>
                                <div>
                                    <div className="text-sm font-bold text-slate-800">{member.name}</div>
                                    <div className="text-xs text-slate-400">{member.assignedTaskCount} tâches assignées</div>
                                </div>
                            </div>
                            <div className={`px-2 py-1 rounded text-[10px] font-bold uppercase ${member.status === 'LOADED' ? 'bg-orange-100 text-orange-700' :
                                member.status === 'OVERLOADED' ? 'bg-red-100 text-red-700' :
                                    'bg-green-100 text-green-700'
                                }`}>
                                {member.status}
                            </div>
                        </div>
                        <div className="h-2 w-full bg-slate-100 rounded-full overflow-hidden">
                            <div
                                className={`h-full rounded-full transition-all duration-500 ${member.status === 'OVERLOADED' ? 'bg-red-500' :
                                    member.status === 'LOADED' ? 'bg-orange-400' :
                                        'bg-green-500'
                                    }`}
                                style={{ width: `${Math.min(member.loadPercent, 100)}%` }}
                            />
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default TeamLoadWidget;
