import React from 'react';
import type { Project } from '../../types';
import ProjectStatusBadge from './ProjectStatusBadge';
import ProjectTypeIcon from './ProjectTypeIcon';
import TeamAvatarStack from './TeamAvatarStack';
import { MoreVertical, ExternalLink } from 'lucide-react';

interface Props {
    projects: Project[];
    onOpen: (id: number) => void;
}

const ProjectListView: React.FC<Props> = ({ projects, onOpen }) => {
    return (
        <div className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-sm">
            <table className="w-full text-left border-collapse">
                <thead>
                    <tr className="bg-slate-50 border-b border-slate-200">
                        <th className="px-6 py-4 text-xs font-semibold text-slate-500 uppercase tracking-wider w-16">Type</th>
                        <th className="px-6 py-4 text-xs font-semibold text-slate-500 uppercase tracking-wider">Projet</th>
                        <th className="px-6 py-4 text-xs font-semibold text-slate-500 uppercase tracking-wider">Client</th>
                        <th className="px-6 py-4 text-xs font-semibold text-slate-500 uppercase tracking-wider">Statut</th>
                        <th className="px-6 py-4 text-xs font-semibold text-slate-500 uppercase tracking-wider">Progression</th>
                        <th className="px-6 py-4 text-xs font-semibold text-slate-500 uppercase tracking-wider">Équipe</th>
                        <th className="px-6 py-4 text-xs font-semibold text-slate-500 uppercase tracking-wider text-right">Actions</th>
                    </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                    {projects.map((project) => (
                        <tr
                            key={project.id}
                            className="hover:bg-slate-50 transition-colors group cursor-pointer"
                            onClick={() => onOpen(project.id)}
                        >
                            <td className="px-6 py-4">
                                <ProjectTypeIcon type={project.type} size={16} />
                            </td>
                            <td className="px-6 py-4">
                                <div className="font-bold text-slate-900">{project.name}</div>
                                <div className="text-[10px] text-slate-400 font-mono tracking-tighter">{project.code}</div>
                            </td>
                            <td className="px-6 py-4 text-sm text-slate-500">
                                {project.clientName || '-'}
                            </td>
                            <td className="px-6 py-4">
                                <ProjectStatusBadge status={project.status} />
                            </td>
                            <td className="px-6 py-4">
                                <div className="flex items-center gap-3">
                                    <div className="flex-1 bg-slate-100 h-1.5 rounded-full w-24">
                                        <div
                                            className="bg-blue-500 h-full rounded-full"
                                            style={{ width: `${project.progress * 100}%` }}
                                        />
                                    </div>
                                    <span className="text-xs font-medium text-slate-600">{Math.round(project.progress * 100)}%</span>
                                </div>
                            </td>
                            <td className="px-6 py-4">
                                <TeamAvatarStack membersCount={project.teamSize || 0} />
                            </td>
                            <td className="px-6 py-4 text-right">
                                <div className="flex justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                                    <button
                                        className="p-1.5 hover:bg-white rounded-md border border-slate-200 text-slate-400 hover:text-blue-600 shadow-sm"
                                        onClick={(e) => { e.stopPropagation(); onOpen(project.id); }}
                                    >
                                        <ExternalLink size={14} />
                                    </button>
                                    <button className="p-1.5 hover:bg-white rounded-md border border-slate-200 text-slate-400 shadow-sm">
                                        <MoreVertical size={14} />
                                    </button>
                                </div>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
            {projects.length === 0 && (
                <div className="py-20 text-center">
                    <p className="text-slate-400 italic">Aucun projet trouvé</p>
                </div>
            )}
        </div>
    );
};

export default ProjectListView;
