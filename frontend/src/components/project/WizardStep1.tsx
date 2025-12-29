import React from 'react';
import type { CreateProjectRequest } from '../../types';

interface Props {
    data: CreateProjectRequest;
    onChange: (data: Partial<CreateProjectRequest>) => void;
}

const WizardStep1: React.FC<Props> = ({ data, onChange }) => {
    return (
        <div className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-2">
                    <label className="text-sm font-bold text-slate-700">Nom du Projet *</label>
                    <input
                        type="text"
                        className="w-full px-4 py-3 rounded-xl border border-slate-200 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all"
                        placeholder="Ex: Refonte Site E-commerce"
                        value={data.name}
                        onChange={(e) => onChange({ name: e.target.value })}
                    />
                </div>
                <div className="space-y-2">
                    <label className="text-sm font-bold text-slate-700">Code Projet (Optionnel)</label>
                    <input
                        type="text"
                        className="w-full px-4 py-3 rounded-xl border border-slate-200 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all font-mono"
                        placeholder="Ex: PRJ-2024-001"
                        value={data.code}
                        onChange={(e) => onChange({ code: e.target.value.toUpperCase() })}
                    />
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-2">
                    <label className="text-sm font-bold text-slate-700">Client Final *</label>
                    <input
                        type="text"
                        className="w-full px-4 py-3 rounded-xl border border-slate-200 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all"
                        placeholder="Ex: Acme Corp"
                        value={data.clientName}
                        onChange={(e) => onChange({ clientName: e.target.value })}
                    />
                </div>
                <div className="space-y-2">
                    <label className="text-sm font-bold text-slate-700">Type de Projet</label>
                    <div className="grid grid-cols-3 gap-2">
                        {['WEB', 'MOBILE', 'API', 'DESKTOP', 'MAINTENANCE'].map(type => (
                            <button
                                key={type}
                                onClick={() => onChange({ type })}
                                className={`py-2 px-1 rounded-lg text-[10px] font-bold border transition-all ${data.type === type
                                        ? 'bg-blue-50 border-blue-500 text-blue-700 shadow-sm'
                                        : 'bg-white border-slate-200 text-slate-500 hover:border-slate-300'
                                    }`}
                            >
                                {type}
                            </button>
                        ))}
                    </div>
                </div>
            </div>

            <div className="space-y-2">
                <label className="text-sm font-bold text-slate-700">Description Courte *</label>
                <textarea
                    rows={3}
                    className="w-full px-4 py-3 rounded-xl border border-slate-200 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all"
                    placeholder="Résumez le projet en quelques lignes..."
                    value={data.description}
                    onChange={(e) => onChange({ description: e.target.value })}
                />
            </div>

            <div className="space-y-2">
                <label className="text-sm font-bold text-slate-700">Détails supplémentaires (Vision, Objectifs)</label>
                <textarea
                    rows={5}
                    className="w-full px-4 py-3 rounded-xl border border-slate-200 focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all"
                    placeholder="Détaillez le scope, les livrables attendus..."
                    value={data.descriptionDetail}
                    onChange={(e) => onChange({ descriptionDetail: e.target.value })}
                />
            </div>
        </div>
    );
};

export default WizardStep1;
