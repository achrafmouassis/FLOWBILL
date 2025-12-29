import React, { useState } from 'react';
import { X, ChevronRight, ChevronLeft, Check } from 'lucide-react';
import type { CreateProjectRequest } from '../../types';
import { projectService } from '../../api/projectService';

import WizardStep1 from './WizardStep1';
import WizardStep2 from './WizardStep2';
import WizardStep3 from './WizardStep3';
import WizardStep4 from './WizardStep4';
import WizardStep5 from './WizardStep5';
import Spinner from '../Spinner';

interface Props {
    isOpen: boolean;
    onClose: () => void;
    onSuccess: () => void;
}

const ProjectWizard: React.FC<Props> = ({ isOpen, onClose, onSuccess }) => {
    const [step, setStep] = useState(1);
    const [loading, setLoading] = useState(false);
    const [formData, setFormData] = useState<CreateProjectRequest>({
        name: '',
        code: '',
        description: '',
        descriptionDetail: '',
        type: 'WEB',
        clientName: '',
        teamMembers: [],
        workflowConfig: '',
        startDate: new Date().toISOString().split('T')[0],
        targetDate: '',
        sprintDurationWeeks: 2
    });

    if (!isOpen) return null;

    const totalSteps = 5;
    const progress = (step / totalSteps) * 100;

    const handleDataChange = (newData: Partial<CreateProjectRequest>) => {
        setFormData(prev => ({ ...prev, ...newData }));
    };

    const nextStep = () => {
        // Simple validation
        if (step === 1 && (!formData.name || !formData.description || !formData.clientName)) {
            alert("Veuillez remplir les champs obligatoires (Nom, Description, Client)");
            return;
        }
        if (step === 2 && formData.teamMembers.length === 0) {
            alert("Veuillez assigner au moins un membre à l'équipe");
            return;
        }
        if (step === 4 && (!formData.startDate || !formData.targetDate)) {
            alert("Veuillez définir les dates du projet");
            return;
        }
        setStep(s => Math.min(s + 1, totalSteps));
    };

    const prevStep = () => setStep(s => Math.max(s - 1, 1));

    const handleSubmit = async () => {
        setLoading(true);
        try {
            await projectService.createProjectWizard(formData);
            onSuccess();
            onClose();
        } catch (error) {
            console.error('Failed to create project:', error);
            alert("Erreur lors de la création du projet. Vérifiez la console.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 z-[60] flex items-center justify-center p-4 bg-slate-900/70 backdrop-blur-md animate-in fade-in duration-300">
            <div className="bg-white w-full max-w-4xl rounded-[2.5rem] shadow-2xl overflow-hidden flex flex-col max-h-[90vh] border border-white/20">
                {/* Header */}
                <div className="px-10 py-8 border-b border-slate-100 flex justify-between items-center bg-slate-50/50">
                    <div>
                        <h2 className="text-3xl font-black text-slate-900 tracking-tight">Configuration du Projet</h2>
                        <div className="flex items-center gap-2 mt-2">
                            <span className="px-2 py-0.5 bg-blue-100 text-blue-700 text-[10px] font-bold rounded-full uppercase tracking-wider">Étape {step}/{totalSteps}</span>
                            <span className="text-sm font-bold text-slate-400">{getStepTitle(step)}</span>
                        </div>
                    </div>
                    <button onClick={onClose} className="p-3 hover:bg-slate-200 rounded-full transition-all text-slate-400 hover:rotate-90">
                        <X size={24} />
                    </button>
                </div>

                {/* Progress Bar */}
                <div className="h-1.5 w-full bg-slate-100">
                    <div
                        className="h-full bg-blue-600 transition-all duration-700 ease-[cubic-bezier(0.34,1.56,0.64,1)]"
                        style={{ width: `${progress}%` }}
                    />
                </div>

                {/* Content */}
                <div className="flex-1 overflow-y-auto p-10">
                    {step === 1 && <WizardStep1 data={formData} onChange={handleDataChange} />}
                    {step === 2 && <WizardStep2 data={formData} onChange={handleDataChange} />}
                    {step === 3 && <WizardStep3 data={formData} onChange={handleDataChange} />}
                    {step === 4 && <WizardStep4 data={formData} onChange={handleDataChange} />}
                    {step === 5 && <WizardStep5 data={formData} />}
                </div>

                {/* Footer Actions */}
                <div className="px-10 py-8 border-t border-slate-100 flex justify-between items-center bg-slate-50/80 backdrop-blur-sm">
                    <button
                        onClick={prevStep}
                        disabled={step === 1 || loading}
                        className={`flex items-center gap-2 px-6 py-3 rounded-2xl font-bold transition-all ${step === 1
                                ? 'text-slate-300 pointer-events-none'
                                : 'text-slate-600 hover:bg-white hover:shadow-sm active:scale-95'
                            }`}
                    >
                        <ChevronLeft size={20} />
                        Précédent
                    </button>

                    <div className="flex gap-4">
                        <button className="px-6 py-3 text-slate-400 font-bold hover:text-slate-600 transition-colors">
                            Brouillon
                        </button>

                        {step < totalSteps ? (
                            <button
                                onClick={nextStep}
                                className="flex items-center gap-2 px-10 py-3 bg-blue-600 text-white rounded-2xl font-bold hover:bg-blue-700 transition-all shadow-xl shadow-blue-500/25 active:scale-95 disabled:bg-slate-300"
                                disabled={loading}
                            >
                                Suivant
                                <ChevronRight size={20} />
                            </button>
                        ) : (
                            <button
                                onClick={handleSubmit}
                                disabled={loading}
                                className="flex items-center gap-2 px-10 py-3 bg-green-600 text-white rounded-2xl font-bold hover:bg-green-700 transition-all shadow-xl shadow-green-500/25 active:scale-95 disabled:bg-slate-300"
                            >
                                {loading ? <Spinner /> : <><Check size={20} /> Créer le Projet</>}
                            </button>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

function getStepTitle(step: number) {
    switch (step) {
        case 1: return 'Informations Générales';
        case 2: return 'Équipe';
        case 3: return 'Workflow';
        case 4: return 'Planning';
        case 5: return 'Recap';
        default: return '';
    }
}

export default ProjectWizard;
