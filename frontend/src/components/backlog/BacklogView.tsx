import React, { useEffect, useState } from 'react';
import { projectService } from '../../api/projectService';
import type { Task } from '../../types';
import StoryCard from './StoryCard';
import BacklogStats from './BacklogStats';
import { Search, Plus, SlidersHorizontal } from 'lucide-react';
import Spinner from '../Spinner';

interface BacklogViewProps {
    projectId: number;
}

import CreateStoryModal from './CreateStoryModal';
import DecomposeStoryModal from './DecomposeStoryModal';

// ... (imports remain)

const BacklogView: React.FC<BacklogViewProps> = ({ projectId }) => {
    const [stories, setStories] = useState<Task[]>([]);
    const [stats, setStats] = useState<any>(null);
    const [loading, setLoading] = useState(true);
    const [search, setSearch] = useState('');
    const [filterMoscow, setFilterMoscow] = useState<string>('');

    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    // Modal States
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [storyToDecompose, setStoryToDecompose] = useState<Task | null>(null);

    const fetchBacklog = async () => {
        setLoading(true);
        try {
            const [storiesData, statsData] = await Promise.all([
                projectService.searchBacklogStories(projectId, search, filterMoscow || undefined, undefined, undefined, page, 10),
                projectService.getBacklogStatistics(projectId)
            ]);
            setStories(storiesData.content);
            setTotalPages(storiesData.totalPages);
            setStats(statsData);
        } catch (error) {
            console.error("Failed to load backlog", error);
        } finally {
            setLoading(false);
        }
    };

    const handleDecompose = (story: Task) => {
        setStoryToDecompose(story);
    };

    useEffect(() => {
        setPage(0); // Reset page on filter change
    }, [search, filterMoscow]);

    useEffect(() => {
        fetchBacklog();
    }, [projectId, page, search, filterMoscow]); // Re-fetch on params change


    return (
        <div className="p-1">
            {/* Header Controls */}
            <div className="flex flex-col md:flex-row gap-4 justify-between items-start md:items-center mb-6">
                <div>
                    <h2 className="text-xl font-bold text-slate-800">Backlog Produit</h2>
                    <p className="text-sm text-slate-500">Gérez, priorisez et planifiez vos user stories.</p>
                </div>
                <button
                    onClick={() => setIsCreateModalOpen(true)}
                    className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-bold flex items-center gap-2 shadow-sm transition-all hover:scale-105"
                >
                    <Plus size={18} />
                    Créer une Story
                </button>
            </div>

            {/* Stats */}
            <BacklogStats stats={stats} />

            {/* Filters */}
            <div className="bg-white p-3 rounded-xl border border-slate-200 shadow-sm mb-6 flex flex-col md:flex-row gap-3 sticky top-[80px] z-30">
                <div className="relative flex-1">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
                    <input
                        type="text"
                        placeholder="Rechercher une story..."
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        className="w-full pl-10 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm font-medium"
                    />
                </div>

                <div className="flex gap-2">
                    <select
                        value={filterMoscow}
                        onChange={(e) => setFilterMoscow(e.target.value)}
                        className="px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm font-bold text-slate-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    >
                        <option value="">Toutes Priorités</option>
                        <option value="MUST_HAVE">Must Have</option>
                        <option value="SHOULD_HAVE">Should Have</option>
                        <option value="COULD_HAVE">Could Have</option>
                        <option value="WONT_HAVE">Won't Have</option>
                    </select>

                    <button className="p-2 bg-slate-50 border border-slate-200 rounded-lg text-slate-500 hover:bg-slate-100">
                        <SlidersHorizontal size={18} />
                    </button>
                </div>
            </div>

            {/* Content */}
            {loading ? (
                <div className="flex justify-center py-20"><Spinner /></div>
            ) : stories.length === 0 ? (
                <div className="text-center py-20 bg-slate-50 rounded-2xl border border-slate-200 border-dashed">
                    <div className="text-slate-400 font-bold">Aucune story trouvée</div>
                    <p className="text-slate-400 text-sm">Essayez de modifier vos filtres ou créez une nouvelle story.</p>
                </div>
            ) : (
                <div className="space-y-3">
                    {stories.map(story => (
                        <StoryCard
                            key={story.id}
                            story={story}
                            onDecompose={() => handleDecompose(story)}
                        />
                    ))}

                    {/* Pagination Controls */}
                    <div className="flex justify-between items-center py-4 text-xs font-bold text-slate-400 uppercase tracking-widest border-t border-slate-100 mt-4">
                        <span className="text-slate-500">Page {page + 1} sur {totalPages}</span>
                        <div className="flex gap-2">
                            <button
                                disabled={page === 0}
                                onClick={() => setPage(p => Math.max(0, p - 1))}
                                className="px-3 py-1 bg-slate-100 rounded disabled:opacity-50 hover:bg-slate-200 transition-colors"
                            >
                                Précédent
                            </button>
                            <button
                                disabled={page >= totalPages - 1}
                                onClick={() => setPage(p => p + 1)}
                                className="px-3 py-1 bg-slate-100 rounded disabled:opacity-50 hover:bg-slate-200 transition-colors"
                            >
                                Suivant
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Modals */}
            <CreateStoryModal
                projectId={projectId}
                isOpen={isCreateModalOpen}
                onClose={() => setIsCreateModalOpen(false)}
                onSuccess={fetchBacklog}
            />

            <DecomposeStoryModal
                projectId={projectId}
                story={storyToDecompose}
                onClose={() => setStoryToDecompose(null)}
                onSuccess={fetchBacklog}
            />
        </div>
    );
};

export default BacklogView;
