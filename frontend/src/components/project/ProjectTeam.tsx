import React from 'react';
import type { ProjectMember } from '../../types';
import { User, Shield, Clock } from 'lucide-react';

interface ProjectTeamProps {
    members: ProjectMember[];
    projectId: number;
}

const DUMMY_MEMBERS: ProjectMember[] = [
    { userId: 101, fullName: 'Sarah Connor', role: 'Lead Developer', capacityHours: 40 },
    { userId: 102, fullName: 'John Reese', role: 'Frontend Engineer', capacityHours: 35 },
    { userId: 103, fullName: 'Harold Finch', role: 'System Architect', capacityHours: 20 },
    { userId: 104, fullName: 'Sameen Shaw', role: 'DevOps Engineer', capacityHours: 40 },
];

const ProjectTeam: React.FC<ProjectTeamProps> = ({ members }) => {
    // TODO: Remove this hardcoded fallback once backend data is fully populated
    const displayMembers = members.length > 0 ? members : DUMMY_MEMBERS;

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center bg-white p-6 rounded-3xl border border-slate-100 shadow-sm">
                <div>
                    <h2 className="text-xl font-bold text-slate-800">Équipe du Projet</h2>
                    <p className="text-slate-500 text-sm">
                        {displayMembers.length} membre{displayMembers.length !== 1 ? 's' : ''} assigné{displayMembers.length !== 1 ? 's' : ''}
                    </p>
                </div>
            </div>

            {displayMembers.length === 0 ? (
                <div className="bg-white rounded-[2.5rem] p-20 text-center border border-slate-200 border-dashed">
                    <div className="w-16 h-16 bg-slate-100 rounded-2xl flex items-center justify-center mx-auto mb-4 text-slate-300">
                        <User size={32} />
                    </div>
                    <h3 className="text-xl font-bold text-slate-800 mb-2">Aucun membre assigné</h3>
                    <p className="text-slate-500 max-w-md mx-auto">
                        Ce projet n'a pas encore d'équipe constituée.
                    </p>
                </div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {displayMembers.map((member) => (
                        <div key={member.userId} className="bg-white p-6 rounded-3xl border border-slate-100 shadow-sm hover:shadow-md transition-shadow group">
                            <div className="flex items-start justify-between mb-6">
                                <div className="flex items-center gap-4">
                                    <div className="w-12 h-12 rounded-2xl bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center text-white font-bold text-lg shadow-blue-200 shadow-lg">
                                        {member.fullName.charAt(0).toUpperCase()}
                                    </div>
                                    <div>
                                        <h3 className="font-bold text-slate-900">{member.fullName}</h3>
                                        <div className="flex items-center gap-1.5 ">
                                            <span className="inline-block w-2 h-2 rounded-full bg-green-500"></span>
                                            <span className="text-xs font-bold text-slate-400">Actif</span>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div className="space-y-3">
                                <div className="flex items-center justify-between p-3 bg-slate-50 rounded-2xl group-hover:bg-slate-100 transition-colors">
                                    <div className="flex items-center gap-2 text-slate-500">
                                        <Shield size={16} />
                                        <span className="text-xs font-bold uppercase tracking-wide">Rôle</span>
                                    </div>
                                    <span className="text-sm font-bold text-slate-700">{member.role || 'Développeur'}</span>
                                </div>

                                <div className="flex items-center justify-between p-3 bg-slate-50 rounded-2xl group-hover:bg-slate-100 transition-colors">
                                    <div className="flex items-center gap-2 text-slate-500">
                                        <Clock size={16} />
                                        <span className="text-xs font-bold uppercase tracking-wide">Capacité</span>
                                    </div>
                                    <span className="text-sm font-bold text-slate-700">{member.capacityHours} h/sem</span>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default ProjectTeam;
