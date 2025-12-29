import React from 'react';
import type { Project } from '../../types';
import ProjectStatusBadge from './ProjectStatusBadge';
import ProjectTypeIcon from './ProjectTypeIcon';
import TeamAvatarStack from './TeamAvatarStack';
import { Calendar, Layers, CheckCircle2 } from 'lucide-react';

interface Props {
    project: Project;
    onClick: (id: number) => void;
}

const ProjectCard: React.FC<Props> = ({ project, onClick }) => {
    return (
        <div
            onClick={() => onClick(project.id)}
            className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm hover:shadow-md hover:scale-[1.01] transition-all cursor-pointer group"
        >
            <div className="flex justify-between items-start mb-4">
                <ProjectTypeIcon type={project.type} />
                <ProjectStatusBadge status={project.status} />
            </div>

            <h3 className="text-xl font-bold text-slate-900 mb-1 group-hover:text-blue-600 transition-colors">
                {project.name}
            </h3>
            <p className="text-sm text-slate-500 mb-4 line-clamp-2 min-h-[40px]">
                {project.clientName ? `Client: ${project.clientName}` : 'No client assigned'}
            </p>

            <div className="space-y-3 mb-6">
                <div className="flex justify-between text-xs font-medium text-slate-400">
                    <span>Progression</span>
                    <span>{Math.round(project.progress * 100)}%</span>
                </div>
                <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden">
                    <div
                        className={`h-full transition-all duration-500 ${project.progress > 0.7 ? 'bg-green-500' :
                                project.progress > 0.3 ? 'bg-blue-500' : 'bg-slate-300'
                            }`}
                        style={{ width: `${project.progress * 100}%` }}
                    />
                </div>
                <div className="flex justify-between text-[11px] text-slate-500">
                    <div className="flex items-center gap-1">
                        <CheckCircle2 size={12} />
                        {project.completedTaskCount || 0}/{project.taskCount || 0} tâches
                    </div>
                    {project.activeSprintsCount ? (
                        <div className="flex items-center gap-1 text-blue-600 font-medium">
                            <Layers size={12} />
                            Sprint actif
                        </div>
                    ) : null}
                </div>
            </div>

            <div className="pt-4 border-t border-slate-100 flex justify-between items-center">
                <TeamAvatarStack membersCount={project.teamSize || 0} />
                <div className="flex items-center gap-1.5 text-xs text-slate-400 font-medium">
                    <Calendar size={14} />
                    {project.targetDate ? new Date(project.targetDate).toLocaleDateString() : 'Pas de date'}
                </div>
            </div>
        </div>
    );
};

export default ProjectCard;
