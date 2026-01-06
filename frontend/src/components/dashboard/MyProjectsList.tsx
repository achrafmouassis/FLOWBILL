import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { projectService } from '../../api/projectService';
import type { Project } from '../../types';
import Spinner from '../Spinner';
import ProjectStatusBadge from '../project/ProjectStatusBadge';
import ProjectTypeIcon from '../project/ProjectTypeIcon';
import { ArrowRight, Users, CheckCircle2, Clock } from 'lucide-react';

const DUMMY_PROJECTS: Project[] = [
    { id: 101, name: 'Alpha Banking', clientName: 'Alpha Corp', type: 'FIXED_PRICE', status: 'IN_PROGRESS', progress: 0.75, completedTaskCount: 15, taskCount: 20, teamSize: 5, description: 'Banking App' },
    { id: 102, name: 'Beta Logistics', clientName: 'Beta Inc', type: 'TIME_MATERIAL', status: 'ON_HOLD', progress: 0.30, completedTaskCount: 3, taskCount: 10, teamSize: 3, description: 'Logistics Dashboard' },
];

export const MyProjectsList = () => {
    const [projects, setProjects] = useState<Project[]>(DUMMY_PROJECTS); // Initialize with dummy data

    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchProjects = async () => {
            try {
                // For now, we fetch all tenant projects. 
                // In a future phase, we could filter by project membership on the backend.
                const data = await projectService.getProjects();
                if (data && data.length > 0) {
                    setProjects(data);
                }
                // Else keep DUMMY_PROJECTS
            } catch (error) {
                console.error("Failed to fetch projects", error);
            } finally {
                setLoading(false);
            }
        };
        fetchProjects();
    }, []);

    if (loading) return <Spinner />;

    if (projects.length === 0) {
        return (
            <div className="bg-white p-8 rounded-2xl shadow-sm border border-slate-200 text-center">
                <div className="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center mx-auto mb-4">
                    <Clock className="text-slate-300" size={32} />
                </div>
                <h3 className="text-lg font-bold text-slate-800">Aucun projet trouvé</h3>
                <p className="text-slate-500 mt-1">Vous n'êtes assigné à aucun projet pour le moment.</p>
            </div>
        );
    }

    return (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {projects.map((project) => (
                <Link
                    key={project.id}
                    to={`/projects/${project.id}`}
                    className="group bg-white rounded-2xl p-6 shadow-sm border border-slate-200 hover:shadow-md hover:border-blue-200 transition-all duration-300 flex flex-col"
                >
                    <div className="flex justify-between items-start mb-4">
                        <div className="p-3 bg-slate-50 rounded-xl group-hover:bg-blue-50 transition-colors">
                            <ProjectTypeIcon type={project.type} size={24} />
                        </div>
                        <ProjectStatusBadge status={project.status} />
                    </div>

                    <div className="mb-4">
                        <h3 className="text-lg font-bold text-slate-900 group-hover:text-blue-600 transition-colors uppercase tracking-tight">
                            {project.name}
                        </h3>
                        <p className="text-sm text-slate-500 font-medium">{project.clientName || 'Interne'}</p>
                    </div>

                    <div className="mt-auto space-y-4">
                        <div>
                            <div className="flex justify-between text-xs font-bold text-slate-400 uppercase tracking-widest mb-2">
                                <span>Progression</span>
                                <span>{Math.round(project.progress * 100)}%</span>
                            </div>
                            <div className="w-full bg-slate-100 rounded-full h-1.5 overflow-hidden">
                                <div
                                    className="bg-blue-500 h-full rounded-full transition-all duration-500"
                                    style={{ width: `${project.progress * 100}%` }}
                                ></div>
                            </div>
                        </div>

                        <div className="flex items-center justify-between pt-4 border-t border-slate-50">
                            <div className="flex gap-4">
                                <div className="flex items-center gap-1.5 text-slate-500">
                                    <CheckCircle2 size={16} className="text-emerald-500" />
                                    <span className="text-xs font-bold">{project.completedTaskCount}/{project.taskCount}</span>
                                </div>
                                <div className="flex items-center gap-1.5 text-slate-500">
                                    <Users size={16} className="text-blue-400" />
                                    <span className="text-xs font-bold">{project.teamSize || 0}</span>
                                </div>
                            </div>
                            <ArrowRight size={18} className="text-slate-300 group-hover:text-blue-500 group-hover:translate-x-1 transition-all" />
                        </div>
                    </div>
                </Link>
            ))}
        </div>
    );
};
