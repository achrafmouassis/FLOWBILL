import React from 'react';
import { Search, LayoutGrid, List, SlidersHorizontal } from 'lucide-react';

interface Props {
    searchTerm: string;
    onSearchChange: (value: string) => void;
    viewMode: 'grid' | 'list';
    onViewModeChange: (mode: 'grid' | 'list') => void;
    onAddProject: () => void;
}

const ProjectFilters: React.FC<Props> = ({
    searchTerm,
    onSearchChange,
    viewMode,
    onViewModeChange,
    onAddProject
}) => {
    return (
        <div className="flex flex-col md:flex-row gap-4 mb-6 justify-between items-center">
            <div className="relative w-full md:w-96">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
                <input
                    type="text"
                    placeholder="Rechercher un projet, client, code..."
                    className="w-full pl-10 pr-4 py-2 bg-white border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all shadow-sm"
                    value={searchTerm}
                    onChange={(e) => onSearchChange(e.target.value)}
                />
            </div>

            <div className="flex items-center gap-4 w-full md:w-auto">
                <div className="bg-slate-100 p-1 rounded-xl flex gap-1 border border-slate-200">
                    <button
                        onClick={() => onViewModeChange('grid')}
                        className={`p-1.5 rounded-lg transition-all ${viewMode === 'grid' ? 'bg-white text-blue-600 shadow-sm' : 'text-slate-500 hover:text-slate-700'}`}
                    >
                        <LayoutGrid size={18} />
                    </button>
                    <button
                        onClick={() => onViewModeChange('list')}
                        className={`p-1.5 rounded-lg transition-all ${viewMode === 'list' ? 'bg-white text-blue-600 shadow-sm' : 'text-slate-500 hover:text-slate-700'}`}
                    >
                        <List size={18} />
                    </button>
                </div>

                <button className="flex items-center gap-2 px-3 py-2 bg-white border border-slate-200 rounded-xl text-slate-600 hover:bg-slate-50 transition-colors shadow-sm">
                    <SlidersHorizontal size={16} />
                    <span className="text-sm font-medium">Filtres</span>
                </button>

                <button
                    onClick={onAddProject}
                    className="flex-1 md:flex-none px-4 py-2 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition-all shadow-md shadow-blue-500/20"
                >
                    Nouveau Projet
                </button>
            </div>
        </div>
    );
};

export default ProjectFilters;
