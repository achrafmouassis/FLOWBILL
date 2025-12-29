import React from 'react';
import type { ProjectDetailed } from '../../types';
import {
    Info,
    Calendar,
    TrendingUp,
    Zap,
    Users,
    Clock,
    AlertCircle,
    Activity
} from 'lucide-react';

interface Props {
    project: ProjectDetailed;
}

const ProjectOverview: React.FC<Props> = ({ project }) => {
    return (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 animate-in fade-in slide-in-from-bottom-4 duration-500">
            {/* Left Column - 2/3 */}
            <div className="lg:col-span-2 space-y-6">
                {/* Info Block */}
                <div className="bg-white rounded-3xl p-6 border border-slate-100 shadow-sm">
                    <div className="flex items-center gap-2 text-slate-400 mb-4">
                        <Info size={18} />
                        <h3 className="text-sm font-bold uppercase tracking-wider">Description</h3>
                    </div>
                    <p className="text-slate-600 leading-relaxed">
                        {project.description}
                    </p>
                    {project.descriptionDetail && (
                        <div className="mt-4 pt-4 border-t border-slate-50 text-sm text-slate-500 italic">
                            {project.descriptionDetail}
                        </div>
                    )}
                </div>

                {/* Progression Block */}
                <div className="bg-white rounded-3xl p-6 border border-slate-100 shadow-sm">
                    <div className="flex items-center gap-2 text-slate-400 mb-6">
                        <TrendingUp size={18} />
                        <h3 className="text-sm font-bold uppercase tracking-wider">Progression du Scope</h3>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                        <div className="md:col-span-1 flex flex-col items-center justify-center border-r border-slate-50">
                            <div className="relative w-24 h-24 flex items-center justify-center">
                                <svg className="w-full h-full transform -rotate-90">
                                    <circle cx="48" cy="48" r="40" fill="transparent" stroke="#f1f5f9" strokeWidth="8" />
                                    <circle
                                        cx="48" cy="48" r="40" fill="transparent"
                                        stroke="#3b82f6" strokeWidth="8"
                                        strokeDasharray={251.2}
                                        strokeDashoffset={251.2 * (1 - project.progress)}
                                        strokeLinecap="round"
                                        className="transition-all duration-1000"
                                    />
                                </svg>
                                <span className="absolute text-xl font-black text-slate-800">{Math.round(project.progress * 100)}%</span>
                            </div>
                            <p className="text-[10px] font-bold text-slate-400 uppercase mt-4">Total Complété</p>
                        </div>
                        <div className="md:col-span-2 grid grid-cols-2 gap-4">
                            <StatsCard label="Tâches Terminées" value={project.completedTaskCount || 0} icon={Zap} color="text-yellow-600" />
                            <StatsCard label="Tâches Totales" value={project.taskCount || 0} icon={Layers} color="text-blue-600" />
                            <StatsCard label="Sprints" value={project.activeSprintsCount || 0} icon={Activity} color="text-purple-600" />
                            <StatsCard label="Équipe" value={project.teamMembers.length} icon={Users} color="text-green-600" />
                        </div>
                    </div>
                </div>

                {/* Team Block */}
                <div className="bg-white rounded-3xl p-6 border border-slate-100 shadow-sm">
                    <div className="flex items-center gap-2 text-slate-400 mb-6">
                        <Users size={18} />
                        <h3 className="text-sm font-bold uppercase tracking-wider">Équipe du Projet</h3>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        {project.teamMembers.map((member, i) => (
                            <div key={i} className="flex items-center gap-4 p-3 rounded-2xl bg-slate-50 border border-slate-100">
                                <div className="w-10 h-10 rounded-full bg-slate-200 flex items-center justify-center font-bold text-slate-500">
                                    {member.fullName.charAt(0)}
                                </div>
                                <div>
                                    <p className="text-sm font-bold text-slate-800">{member.fullName}</p>
                                    <p className="text-[10px] text-slate-400 uppercase font-medium">{member.role} • {member.capacityHours}h/sem</p>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* Right Column - 1/3 */}
            <div className="space-y-6">
                {/* Dates Block */}
                <div className="bg-slate-900 rounded-3xl p-6 text-white shadow-xl">
                    <div className="flex items-center gap-2 text-slate-400 mb-6 font-bold uppercase text-[10px] tracking-widest">
                        <Calendar size={14} /> Calendrier
                    </div>
                    <div className="space-y-6">
                        <div>
                            <p className="text-xs text-slate-400">Date de début</p>
                            <p className="text-lg font-bold">{project.startDate ? new Date(project.startDate).toLocaleDateString() : '-'}</p>
                        </div>
                        <div className="relative py-2">
                            <div className="absolute left-0 top-1/2 w-full h-0.5 bg-slate-700" />
                            <div className="absolute left-0 top-1/2 -translate-y-1/2 w-3 h-3 rounded-full bg-blue-500" />
                            <div className="absolute right-0 top-1/2 -translate-y-1/2 w-3 h-3 rounded-full bg-slate-500" />
                        </div>
                        <div>
                            <p className="text-xs text-slate-400">Échéance cible</p>
                            <p className="text-lg font-bold text-blue-400">{project.targetDate ? new Date(project.targetDate).toLocaleDateString() : '-'}</p>
                        </div>
                    </div>
                </div>

                {/* Alerts Block */}
                <div className="bg-white rounded-3xl p-6 border border-slate-100 shadow-sm">
                    <div className="flex items-center gap-2 text-slate-400 mb-4 font-bold uppercase text-[10px] tracking-widest">
                        <AlertCircle size={14} /> Alertes & Risques
                    </div>
                    <div className="space-y-3">
                        <div className="p-3 bg-red-50 text-red-700 rounded-xl text-xs border border-red-100 flex gap-2">
                            <AlertCircle size={14} className="flex-none" />
                            <span>Sprint actuel en retard de 2 jours sur les prévisions.</span>
                        </div>
                        <div className="p-3 bg-orange-50 text-orange-700 rounded-xl text-xs border border-orange-100 flex gap-2">
                            <Clock size={14} className="flex-none" />
                            <span>Surcharge détectée pour le Tech Lead (45h planifiées).</span>
                        </div>
                    </div>
                </div>

                {/* Activity Mini */}
                <div className="bg-white rounded-3xl p-6 border border-slate-100 shadow-sm">
                    <div className="flex items-center justify-between mb-4 font-bold uppercase text-[10px] tracking-widest text-slate-400">
                        <div className="flex items-center gap-2"><Activity size={14} /> Activité</div>
                        <button className="text-blue-600 hover:underline">Voir tout</button>
                    </div>
                    <div className="space-y-4">
                        <ActivityItem text="Sprint 3 démarré" time="Il y a 2h" />
                        <ActivityItem text="Story 'Login API' terminée" time="Il y a 5h" />
                        <ActivityItem text="Nouveau membre: Alice" time="Hier" />
                    </div>
                </div>
            </div>
        </div>
    );
};

// Internal Helpers
const StatsCard = ({ label, value, icon: Icon, color }: any) => (
    <div className="bg-slate-50 p-4 rounded-2xl border border-slate-100">
        <Icon size={16} className={`${color} mb-1`} />
        <p className="text-lg font-black text-slate-800">{value}</p>
        <p className="text-[9px] uppercase font-bold text-slate-400">{label}</p>
    </div>
);

const ActivityItem = ({ text, time }: any) => (
    <div className="flex gap-3 items-start">
        <div className="w-1.5 h-1.5 rounded-full bg-blue-500 mt-1.5 flex-none" />
        <div>
            <p className="text-xs font-bold text-slate-700 leading-none">{text}</p>
            <p className="text-[10px] text-slate-400 mt-1">{time}</p>
        </div>
    </div>
);

const Layers = ({ size, className }: any) => (
    <svg
        xmlns="http://www.w3.org/2000/svg"
        width={size} height={size}
        viewBox="0 0 24 24" fill="none"
        stroke="currentColor" strokeWidth="2"
        strokeLinecap="round" strokeLinejoin="round"
        className={className}
    >
        <polygon points="12 2 2 7 12 12 22 7 12 2" />
        <polyline points="2 17 12 22 22 17" />
        <polyline points="2 12 12 17 22 12" />
    </svg>
);

export default ProjectOverview;
