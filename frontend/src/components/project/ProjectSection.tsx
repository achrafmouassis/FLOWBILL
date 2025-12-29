import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { projectService } from '../../api/projectService';
import type { Project, ProjectDashboardStats } from '../../types';
import ProjectStatsHeader from './ProjectStatsHeader';
import ProjectFilters from './ProjectFilters';
import ProjectCard from './ProjectCard';
import ProjectListView from './ProjectListView';
import Spinner from '../Spinner';

import ProjectWizard from './ProjectWizard';

const ProjectSection: React.FC = () => {
    const navigate = useNavigate();
    const [projects, setProjects] = useState<Project[]>([]);
    const [stats, setStats] = useState<ProjectDashboardStats | null>(null);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');
    const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');
    const [isWizardOpen, setIsWizardOpen] = useState(false);

    const fetchData = async () => {
        setLoading(true);
        try {
            const [projData, statsData] = await Promise.all([
                projectService.searchProjects(searchTerm),
                projectService.getProjectStats()
            ]);
            setProjects(projData);
            setStats(statsData);
        } catch (error) {
            console.error('Error fetching projects:', error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        const delaySearch = setTimeout(() => {
            fetchData();
        }, 300);
        return () => clearTimeout(delaySearch);
    }, [searchTerm]);

    const handleOpenProject = (id: number) => {
        navigate(`/projects/${id}`);
    };

    return (
        <div className="animate-in fade-in duration-500">
            <ProjectStatsHeader stats={stats} />

            <ProjectFilters
                searchTerm={searchTerm}
                onSearchChange={setSearchTerm}
                viewMode={viewMode}
                onViewModeChange={setViewMode}
                onAddProject={() => setIsWizardOpen(true)}
            />

            {isWizardOpen && (
                <ProjectWizard
                    isOpen={isWizardOpen}
                    onClose={() => setIsWizardOpen(false)}
                    onSuccess={fetchData}
                />
            )}

            {loading ? (
                <div className="flex justify-center py-20">
                    <Spinner />
                </div>
            ) : (
                <>
                    {viewMode === 'grid' ? (
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                            {projects.map(project => (
                                <ProjectCard
                                    key={project.id}
                                    project={project}
                                    onClick={handleOpenProject}
                                />
                            ))}
                        </div>
                    ) : (
                        <ProjectListView
                            projects={projects}
                            onOpen={handleOpenProject}
                        />
                    )}

                    {!loading && projects.length === 0 && (
                        <div className="text-center py-20 bg-slate-50 rounded-2xl border-2 border-dashed border-slate-200">
                            <p className="text-slate-400 font-medium">Aucun projet ne correspond à votre recherche</p>
                        </div>
                    )}
                </>
            )}
        </div>
    );
};

export default ProjectSection;
