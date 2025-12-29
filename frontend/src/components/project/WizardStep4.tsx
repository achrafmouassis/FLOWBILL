import React from 'react';
import type { CreateProjectRequest } from '../../types';
import { Calendar, Clock, AlertCircle } from 'lucide-react';

interface Props {
    data: CreateProjectRequest;
    onChange: (data: Partial<CreateProjectRequest>) => void;
}

const WizardStep4: React.FC<Props> = ({ data, onChange }) => {

    const calculateDuration = () => {
        if (!data.startDate || !data.targetDate) return null;
        const start = new Date(data.startDate);
        const end = new Date(data.targetDate);
        const diffTime = Math.abs(end.getTime() - start.getTime());
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        const diffWeeks = (diffDays / 7).toFixed(1);
        return { days: diffDays, weeks: diffWeeks };
    };

    const duration = calculateDuration();
    const sprintsCount = duration ? Math.ceil(duration.days / (data.sprintDurationWeeks * 7)) : 0;

    return (
        <div className="space-y-8 max-w-2xl mx-auto">
            <div className="bg-blue-50 border border-blue-100 rounded-2xl p-4 flex gap-3 text-blue-800">
                <AlertCircle className="flex-none mt-0.5" size={18} />
                <p className="text-sm">
                    La planification temporelle permet au système de calculer automatiquement la progression et les alertes de retard dans votre dashboard.
                </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                <div className="space-y-2">
                    <label className="text-sm font-bold text-slate-700 flex items-center gap-2">
                        <Calendar size={16} className="text-slate-400" />
                        Date de début
                    </label>
                    <input
                        type="date"
                        className="w-full px-4 py-3 rounded-xl border border-slate-200 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none"
                        value={data.startDate}
                        onChange={(e) => onChange({ startDate: e.target.value })}
                    />
                </div>
                <div className="space-y-2">
                    <label className="text-sm font-bold text-slate-700 flex items-center gap-2">
                        <Calendar size={16} className="text-slate-400" />
                        Date de livraison cible
                    </label>
                    <input
                        type="date"
                        className="w-full px-4 py-3 rounded-xl border border-slate-200 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none"
                        value={data.targetDate}
                        onChange={(e) => onChange({ targetDate: e.target.value })}
                    />
                </div>
            </div>

            <div className="space-y-4">
                <label className="text-sm font-bold text-slate-700 flex items-center gap-2">
                    <Clock size={16} className="text-slate-400" />
                    Durée standard des Sprints
                </label>
                <div className="grid grid-cols-4 gap-4">
                    {[1, 2, 3, 4].map(w => (
                        <button
                            key={w}
                            onClick={() => onChange({ sprintDurationWeeks: w })}
                            className={`py-4 rounded-2xl border-2 transition-all flex flex-col items-center gap-1 ${data.sprintDurationWeeks === w
                                    ? 'border-blue-500 bg-blue-50 text-blue-700'
                                    : 'border-slate-100 bg-white text-slate-500 hover:border-slate-200'
                                }`}
                        >
                            <span className="text-xl font-bold">{w}</span>
                            <span className="text-[10px] uppercase tracking-tighter">Semaine{w > 1 ? 's' : ''}</span>
                        </button>
                    ))}
                </div>
                <p className="text-xs text-slate-400 italic">La recommandation standard Scrum est de 2 semaines.</p>
            </div>

            {duration && (
                <div className="bg-slate-50 rounded-3xl p-8 border border-slate-100 flex flex-col md:flex-row justify-around items-center gap-8 text-center">
                    <div>
                        <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1">Durée Totale</p>
                        <p className="text-3xl font-black text-slate-800">{duration.weeks} <span className="text-lg font-bold text-slate-400">semaines</span></p>
                    </div>
                    <div className="w-px h-12 bg-slate-200 hidden md:block" />
                    <div>
                        <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1">Nombre estimé de Sprints</p>
                        <p className="text-3xl font-black text-blue-600">{sprintsCount}</p>
                    </div>
                </div>
            )}
        </div>
    );
};

export default WizardStep4;
