import React, { useState } from 'react';
import { X, Calendar, Save } from 'lucide-react';
import { projectService } from '../../api/projectService';
import Spinner from '../Spinner';

interface CreateSprintModalProps {
    projectId: number;
    isOpen: boolean;
    onClose: () => void;
    onSuccess: () => void;
}

const CreateSprintModal: React.FC<CreateSprintModalProps> = ({ projectId, isOpen, onClose, onSuccess }) => {
    const [loading, setLoading] = useState(false);
    const [formData, setFormData] = useState({
        name: `Sprint ${new Date().getMonth() + 1}`,
        startDate: new Date().toISOString().split('T')[0],
        endDate: new Date(Date.now() + 14 * 24 * 60 * 60 * 1000).toISOString().split('T')[0], // +14 days
        goal: ''
    });

    if (!isOpen) return null;

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async () => {
        if (!formData.name || !formData.startDate || !formData.endDate) {
            alert('Veuillez remplir les champs obligatoires');
            return;
        }

        setLoading(true);
        try {
            await projectService.createSprint(projectId, {
                ...formData,
                status: 'PLANNED'
            });
            onSuccess();
            onClose();
        } catch (error) {
            console.error(error);
            alert('Erreur lors de la création du sprint');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
            <div className="bg-white rounded-2xl w-full max-w-md shadow-2xl flex flex-col">
                <div className="p-6 border-b border-slate-100 flex justify-between items-center">
                    <h2 className="text-xl font-black text-slate-800">Planifier un Sprint</h2>
                    <button onClick={onClose} className="p-2 hover:bg-slate-100 rounded-full text-slate-400">
                        <X size={24} />
                    </button>
                </div>

                <div className="p-6 space-y-4">
                    <div>
                        <label className="block text-sm font-bold text-slate-700 mb-2">Nom du Sprint *</label>
                        <input
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            className="w-full p-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none font-medium"
                        />
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-bold text-slate-700 mb-2">Début *</label>
                            <div className="relative">
                                <Calendar className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={16} />
                                <input
                                    type="date"
                                    name="startDate"
                                    value={formData.startDate}
                                    onChange={handleChange}
                                    className="w-full pl-10 pr-3 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none font-medium text-sm"
                                />
                            </div>
                        </div>
                        <div>
                            <label className="block text-sm font-bold text-slate-700 mb-2">Fin *</label>
                            <div className="relative">
                                <Calendar className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={16} />
                                <input
                                    type="date"
                                    name="endDate"
                                    value={formData.endDate}
                                    onChange={handleChange}
                                    className="w-full pl-10 pr-3 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none font-medium text-sm"
                                />
                            </div>
                        </div>
                    </div>

                    <div>
                        <label className="block text-sm font-bold text-slate-700 mb-2">Objectif du Sprint</label>
                        <textarea
                            name="goal"
                            value={formData.goal}
                            onChange={handleChange}
                            rows={3}
                            placeholder="Quelle est la valeur clé livrée ?"
                            className="w-full p-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none font-medium resize-none"
                        />
                    </div>
                </div>

                <div className="p-6 border-t border-slate-100 flex justify-end gap-3 rounded-b-2xl bg-slate-50">
                    <button onClick={onClose} className="px-5 py-2 font-bold text-slate-500 hover:bg-slate-200 rounded-lg">
                        Annuler
                    </button>
                    <button
                        onClick={handleSubmit}
                        disabled={loading}
                        className="bg-blue-600 hover:bg-blue-700 text-white px-5 py-2 rounded-lg font-bold flex items-center gap-2 shadow-lg shadow-blue-200 transition-all disabled:opacity-50"
                    >
                        {loading ? <Spinner /> : <><Save size={18} /> Créer le Sprint</>}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default CreateSprintModal;
