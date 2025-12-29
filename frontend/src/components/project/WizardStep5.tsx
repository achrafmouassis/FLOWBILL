import React from 'react';
import type { CreateProjectRequest } from '../../types';
import { BadgeCheck, Users, Workflow, CalendarDays, AlertTriangle } from 'lucide-react';

interface Props {
    data: CreateProjectRequest;
}

const WizardStep5: React.FC<Props> = ({ data }) => {
    const workflow = data.workflowConfig ? JSON.parse(data.workflowConfig) : { columns: [] };

    const sections = [
        {
            icon: BadgeCheck,
            title: 'Informations de base',
            content: (
                <div className="space-y-1">
                    <p className="text-sm"><span className="font-bold">Nom:</span> {data.name}</p>
                    <p className="text-sm"><span className="font-bold">Code:</span> {data.code || 'Auto-généré'}</p>
                    <p className="text-sm"><span className="font-bold">Client:</span> {data.clientName}</p>
                    <p className="text-sm"><span className="font-bold">Type:</span> {data.type}</p>
                </div>
            )
        },
        {
            icon: Users,
            title: 'Équipe constituée',
            content: (
                <div className="flex flex-wrap gap-2">
                    {data.teamMembers.map((m, i) => (
                        <div key={i} className="px-3 py-1 bg-white border border-slate-100 rounded-lg text-xs shadow-sm">
                            <span className="font-bold">{m.fullName}</span> ({m.role})
                        </div>
                    ))}
                    {data.teamMembers.length === 0 && <p className="text-xs italic text-red-500">Aucun membre sélectionné</p>}
                </div>
            )
        },
        {
            icon: Workflow,
            title: 'Configuration Workflow',
            content: (
                <div className="flex gap-2">
                    {workflow.columns.map((col: string, i: number) => (
                        <div key={i} className="px-2 py-1 bg-slate-100 rounded text-[10px] uppercase font-bold text-slate-500">
                            {col}
                        </div>
                    ))}
                </div>
            )
        },
        {
            icon: CalendarDays,
            title: 'Planning prévisionnel',
            content: (
                <div className="space-y-1">
                    <p className="text-sm"><span className="font-bold">Dates:</span> {data.startDate} au {data.targetDate || '???'}</p>
                    <p className="text-sm"><span className="font-bold">Sprints:</span> {data.sprintDurationWeeks} semaines / sprint</p>
                </div>
            )
        }
    ];

    const alerts = [];
    if (!data.name) alerts.push("Le nom du projet est requis.");
    if (!data.clientName) alerts.push("Le client est requis.");
    if (data.teamMembers.length === 0) alerts.push("Au moins un membre doit être assigné.");
    if (!data.targetDate) alerts.push("Une date de livraison cible est requise.");

    return (
        <div className="space-y-6">
            <div className="text-center mb-8">
                <h3 className="text-xl font-bold text-slate-800">Prêt à lancer le projet ?</h3>
                <p className="text-sm text-slate-500">Vérifiez les informations ci-dessous avant de valider.</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {sections.map((sec, i) => (
                    <div key={i} className="p-5 bg-slate-50 rounded-2xl border border-slate-100 space-y-3">
                        <div className="flex items-center gap-2 text-blue-600">
                            <sec.icon size={20} />
                            <h4 className="font-bold text-sm uppercase tracking-wider">{sec.title}</h4>
                        </div>
                        {sec.content}
                    </div>
                ))}
            </div>

            {alerts.length > 0 && (
                <div className="p-4 bg-red-50 border border-red-100 rounded-2xl space-y-2">
                    <div className="flex items-center gap-2 text-red-700 font-bold text-sm">
                        <AlertTriangle size={18} />
                        Des erreurs bloquent la création :
                    </div>
                    <ul className="text-xs text-red-600 list-disc list-inside space-y-1">
                        {alerts.map((a, i) => <li key={i}>{a}</li>)}
                    </ul>
                </div>
            )}

            <div className="p-6 bg-green-50 border border-green-100 rounded-3xl text-center">
                <p className="text-sm text-green-800 font-medium">
                    Une fois créé, vous pourrez configurer les détails avancés (backlog, sprints) dans les paramètres du projet.
                </p>
            </div>
        </div>
    );
};

export default WizardStep5;
