import React from 'react';
import type { CreateProjectRequest } from '../../types';
import { Columns, Settings2, ShieldCheck, Check } from 'lucide-react';

interface Props {
    data: CreateProjectRequest;
    onChange: (data: Partial<CreateProjectRequest>) => void;
}

const WizardStep3: React.FC<Props> = ({ data, onChange }) => {
    const templates = [
        {
            id: 'standard',
            name: 'Standard Scrumban',
            desc: 'Le flux classique pour 90% des projets agiles.',
            columns: ['Backlog', 'À faire', 'En cours', 'En revue', 'Terminé'],
            icon: ShieldCheck,
            color: 'bg-blue-500'
        },
        {
            id: 'scrum',
            name: 'Pure Scrum',
            desc: 'Focus sur le backlog de sprint et les incréments.',
            columns: ['Backlog Sprint', 'En cours', 'Terminé'],
            icon: Columns,
            color: 'bg-purple-500'
        },
        {
            id: 'devops',
            name: 'DevOps Flow',
            desc: 'Inclut les étapes de déploiement et validation.',
            columns: ['Planification', 'Dev', 'Test', 'UAT', 'Prod'],
            icon: Settings2,
            color: 'bg-green-500'
        }
    ];

    // Initial setup if empty
    React.useEffect(() => {
        if (!data.workflowConfig) {
            handleSelect(templates[0]);
        }
    }, []);

    const handleSelect = (tpl: any) => {
        onChange({ workflowConfig: JSON.stringify({ template: tpl.id, columns: tpl.columns }) });
    };

    const currentTpl = data.workflowConfig ? JSON.parse(data.workflowConfig).template : 'standard';

    return (
        <div className="space-y-8">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                {templates.map((tpl) => (
                    <div
                        key={tpl.id}
                        onClick={() => handleSelect(tpl)}
                        className={`p-6 rounded-2xl border-2 transition-all cursor-pointer relative overflow-hidden ${currentTpl === tpl.id
                                ? 'border-blue-500 bg-blue-50/30 ring-4 ring-blue-500/10'
                                : 'border-slate-100 bg-white hover:border-slate-200 hover:shadow-md'
                            }`}
                    >
                        <div className={`w-12 h-12 rounded-xl mb-4 flex items-center justify-center text-white ${tpl.color}`}>
                            <tpl.icon size={24} />
                        </div>
                        <h4 className="font-bold text-slate-800 mb-2">{tpl.name}</h4>
                        <p className="text-xs text-slate-500 leading-relaxed">{tpl.desc}</p>

                        {currentTpl === tpl.id && (
                            <div className="absolute top-3 right-3 bg-blue-500 text-white rounded-full p-0.5">
                                <Check size={12} strokeWidth={4} />
                            </div>
                        )}
                    </div>
                ))}
            </div>

            <div className="bg-slate-50 rounded-3xl p-8">
                <h3 className="text-sm font-bold text-slate-500 uppercase tracking-widest mb-6 text-center">Aperçu du Workflow</h3>
                <div className="flex gap-4 overflow-x-auto pb-4 scrollbar-hide">
                    {data.workflowConfig && JSON.parse(data.workflowConfig).columns.map((col: string, i: number) => (
                        <div key={i} className="flex-none w-40">
                            <div className="bg-white border border-slate-200 rounded-xl p-3 shadow-sm">
                                <span className="text-[10px] font-bold text-slate-400 uppercase">Colonne {i + 1}</span>
                                <p className="text-sm font-bold text-slate-700 mt-1">{col}</p>
                            </div>
                            {i < JSON.parse(data.workflowConfig).columns.length - 1 && (
                                <div className="h-4 flex justify-center items-center">
                                    <div className="w-px h-full bg-slate-200" />
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default WizardStep3;
