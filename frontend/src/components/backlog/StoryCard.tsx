import React from 'react';
import type { Task } from '../../types';
import { MoreVertical, CheckSquare, Layers, Calendar, AlertCircle } from 'lucide-react';

interface StoryCardProps {
    story: Task;
    onEdit?: (story: Task) => void;
    onDecompose?: (story: Task) => void;
}

const StoryCard: React.FC<StoryCardProps> = ({ story, onDecompose }) => {

    const getMoscowColor = (m?: string) => {
        switch (m) {
            case 'MUST_HAVE': return 'bg-red-500';
            case 'SHOULD_HAVE': return 'bg-orange-500';
            case 'COULD_HAVE': return 'bg-blue-500';
            case 'WONT_HAVE': return 'bg-gray-500';
            default: return 'bg-gray-400';
        }
    };

    const getWsjfColor = (score?: number) => {
        if (!score) return 'bg-gray-200 text-gray-500';
        if (score >= 3.0) return 'bg-green-100 text-green-700';
        if (score >= 1.5) return 'bg-orange-100 text-orange-700';
        return 'bg-gray-100 text-gray-700';
    };

    return (
        <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm hover:shadow-md transition-all relative group">
            <div className="flex justify-between items-start mb-3">
                <div className="flex items-start gap-3">
                    <span className={`px-2 py-1 rounded-lg text-[10px] font-bold text-white ${getMoscowColor(story.moscowPriority)}`}>
                        {story.moscowPriority?.replace('_', ' ') || 'NO PRIORITY'}
                    </span>
                    <div>
                        <h4 className="font-bold text-slate-800 text-sm leading-snug">{story.title}</h4>
                        <div className="flex items-center gap-2 mt-1">
                            <span className="text-xs font-mono text-slate-400">US-{story.id}</span>
                            {story.wsjfScore && (
                                <span className={`text-[10px] font-bold px-1.5 py-0.5 rounded-full ${getWsjfColor(story.wsjfScore)}`}>
                                    WSJF: {story.wsjfScore.toFixed(1)}
                                </span>
                            )}
                        </div>
                    </div>
                </div>
                <button className="text-slate-400 hover:text-slate-600 p-1">
                    <MoreVertical size={16} />
                </button>
            </div>

            <p className="text-xs text-slate-500 line-clamp-2 mb-4">
                {story.description || 'No description provided.'}
            </p>

            <div className="flex items-center justify-between border-t border-slate-100 pt-3">
                <div className="flex items-center gap-3">
                    <div className="flex items-center gap-1 text-slate-500" title="Story Points">
                        <Layers size={14} />
                        <span className="text-xs font-bold">{story.estimation || '-'} SP</span>
                    </div>
                    {story.sprintName ? (
                        <div className="flex items-center gap-1 text-blue-600 bg-blue-50 px-2 py-0.5 rounded-md" title={`Sprint: ${story.sprintName}`}>
                            <Calendar size={12} />
                            <span className="text-[10px] font-bold truncate max-w-[80px]">{story.sprintName}</span>
                        </div>
                    ) : (
                        <div className="flex items-center gap-1 text-slate-400" title="Unplanned">
                            <Calendar size={12} />
                            <span className="text-[10px]">Unplanned</span>
                        </div>
                    )}
                </div>

                <div className="flex items-center gap-2">
                    {/* Dependencies */}
                    {(story.blockerIds && story.blockerIds.length > 0) && (
                        <div className="flex items-center gap-1 text-red-500 bg-red-50 px-2 py-0.5 rounded-md" title={`Blocked by ${story.blockerIds.length} tasks`}>
                            <AlertCircle size={12} />
                            <span className="text-[10px] font-bold">Bloqué ({story.blockerIds.length})</span>
                        </div>
                    )}
                    {(story.blockingIds && story.blockingIds.length > 0) && (
                        <div className="flex items-center gap-1 text-orange-500 bg-orange-50 px-2 py-0.5 rounded-md" title={`Blocking ${story.blockingIds.length} tasks`}>
                            <AlertCircle size={12} />
                            <span className="text-[10px] font-bold">Bloque ({story.blockingIds.length})</span>
                        </div>
                    )}

                    <button
                        onClick={() => onDecompose && onDecompose(story)}
                        className="text-xs font-medium text-slate-500 hover:text-blue-600 flex items-center gap-1 px-2 py-1 hover:bg-slate-50 rounded-md transition-colors"
                    >
                        <CheckSquare size={12} />
                        Tasks
                    </button>
                </div>
            </div>
        </div>
    );
};

export default StoryCard;
