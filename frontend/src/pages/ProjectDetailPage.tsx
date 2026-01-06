import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { projectService } from '../api/projectService';
import type { ProjectDetailed } from '../types';
import ProjectOverview from '../components/project/ProjectOverview';
import ProjectStatusBadge from '../components/project/ProjectStatusBadge';
import ProjectTypeIcon from '../components/project/ProjectTypeIcon';
import Spinner from '../components/Spinner';
import BacklogView from '../components/backlog/BacklogView';
import SprintPlanningBoard from '../components/backlog/SprintPlanningBoard';
import ActiveSprintBoard from '../components/project/ActiveSprintBoard';
import ReportingTab from '../components/dashboard/ReportingTab';
import BillingView from '../components/billing/BillingView';
import ProjectTeam from '../components/project/ProjectTeam';
import { jwtDecode } from 'jwt-decode';
import { ChevronLeft, Layout, ListTodo, Layers, Users, BarChart3, Settings, Receipt } from 'lucide-react';

const ProjectDetailPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [project, setProject] = useState<ProjectDetailed | null>(null);
    const [loading, setLoading] = useState(true);
    const [activeTab, setActiveTab] = useState('APERÇU');

    const token = localStorage.getItem('token');
    const decoded: any = token ? jwtDecode(token) : {};
    const roles = decoded.roles || [];

    useEffect(() => {
        const fetchProject = async () => {
            if (!id) return;
            try {
                const data = await projectService.getProjectDetail(parseInt(id));
                setProject(data);
            } catch (err) {
                console.error("Failed to fetch project detail", err);
            } finally {
                setLoading(false);
            }
        };
        fetchProject();
    }, [id]);

    if (loading) return (
        <div className="min-h-screen flex items-center justify-center bg-slate-50">
            <Spinner />
        </div>
    );

    if (!project) return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-slate-50">
            <p className="text-slate-500 font-bold mb-4">Projet introuvable</p>
            <button onClick={() => navigate(-1)} className="text-blue-600 font-bold">Retour</button>
        </div>
    );

    const allTabs = [
        { name: 'APERÇU', icon: Layout, roles: ['ROLE_USER', 'ROLE_ADMIN_ENTREPRISE'] },
        { name: 'Backlog', icon: ListTodo, roles: ['ROLE_USER', 'ROLE_ADMIN_ENTREPRISE'] },
        { name: 'Sprints', icon: Layers, roles: ['ROLE_USER', 'ROLE_ADMIN_ENTREPRISE'] },
        { name: 'Kanban', icon: Layout, roles: ['ROLE_USER', 'ROLE_ADMIN_ENTREPRISE'] },
        { name: 'Équipe', icon: Users, roles: ['ROLE_USER', 'ROLE_ADMIN_ENTREPRISE'] },
        { name: 'Rapports', icon: BarChart3, roles: ['ROLE_ADMIN_ENTREPRISE'] },
        { name: 'Facturation', icon: Receipt, roles: ['ROLE_ADMIN_ENTREPRISE'] },
        { name: 'Paramètres', icon: Settings, roles: ['ROLE_ADMIN_ENTREPRISE'] },
    ];

    const tabs = allTabs.filter(tab => tab.roles.some(role => roles.includes(role)));

    const handleBack = () => {
        if (roles.includes('ROLE_ADMIN_ENTREPRISE')) {
            navigate('/tenant-admin');
        } else {
            navigate('/user');
        }
    };

    return (
        <div className="min-h-screen bg-slate-50 flex flex-col">
            {/* Header Sticky */}
            <header className="sticky top-0 z-40 bg-white/80 backdrop-blur-md border-b border-slate-200">
                <div className="max-w-7xl mx-auto px-6 py-4">
                    <div className="flex items-center gap-4 mb-4">
                        <button
                            onClick={handleBack}
                            className="p-1.5 hover:bg-slate-100 rounded-lg text-slate-400 transition-colors"
                        >
                            <ChevronLeft size={20} />
                        </button>
                        <div className="flex items-center gap-2 text-xs font-bold text-slate-400 uppercase tracking-widest">
                            Projets <span className="text-slate-300">/</span> {project.code || 'PRJ'}
                        </div>
                    </div>

                    <div className="flex flex-col md:flex-row justify-between items-start md:items-end gap-4">
                        <div className="space-y-1">
                            <div className="flex items-center gap-3">
                                <ProjectTypeIcon type={project.type} size={24} />
                                <h1 className="text-3xl font-black text-slate-900">{project.name}</h1>
                            </div>
                            <div className="flex items-center gap-3 ml-1">
                                <ProjectStatusBadge status={project.status} />
                                <span className="text-sm font-medium text-slate-500">
                                    Client: <span className="font-bold text-slate-700">{project.clientName}</span>
                                </span>
                            </div>
                        </div>

                        {/* Tabs */}
                        <nav className="flex gap-1 bg-slate-100 p-1 rounded-2xl border border-slate-200 overflow-x-auto max-w-full">
                            {tabs.map(tab => (
                                <button
                                    key={tab.name}
                                    onClick={() => setActiveTab(tab.name)}
                                    className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition-all whitespace-nowrap ${activeTab === tab.name
                                        ? 'bg-white text-blue-600 shadow-sm'
                                        : 'text-slate-500 hover:text-slate-700'
                                        }`}
                                >
                                    <tab.icon size={14} />
                                    {tab.name}
                                </button>
                            ))}
                        </nav>
                    </div>
                </div>
            </header>

            {/* Content Area */}
            <main className="flex-1 max-w-7xl mx-auto px-6 py-8 w-full">
                {activeTab === 'APERÇU' && <ProjectOverview project={project} />}

                {activeTab === 'Backlog' && <BacklogView projectId={project.id} />}

                {activeTab === 'Sprints' && <SprintPlanningBoard projectId={project.id} />}

                {(activeTab === 'Kanban' || activeTab === 'Board') && <ActiveSprintBoard projectId={project.id} />}

                {activeTab === 'Rapports' && <ReportingTab projectId={project.id} />}

                {activeTab === 'Facturation' && <BillingView projectId={project.id} />}

                {activeTab === 'Équipe' && <ProjectTeam members={project.teamMembers || []} projectId={project.id} />}

                {activeTab !== 'APERÇU' && activeTab !== 'Backlog' && activeTab !== 'Sprints' && activeTab !== 'Kanban' && activeTab !== 'Board' && activeTab !== 'Rapports' && activeTab !== 'Facturation' && activeTab !== 'Équipe' && (
                    <div className="bg-white rounded-[2.5rem] p-20 text-center border border-slate-200 border-dashed">
                        <div className="w-16 h-16 bg-slate-100 rounded-2xl flex items-center justify-center mx-auto mb-4 text-slate-300">
                            <Layout size={32} />
                        </div>
                        <h3 className="text-xl font-bold text-slate-800 mb-2">Onglet {activeTab} en cours d'édition</h3>
                        <p className="text-slate-500 max-w-md mx-auto">
                            Cette fonctionnalité sera déployée dans le cadre des prochains sprints de développement (Prompts 4-10).
                        </p>
                    </div>
                )}
            </main>
        </div>
    );
};

export default ProjectDetailPage;
