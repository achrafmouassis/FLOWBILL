import React, { useState } from 'react';
import { X, Plus, Trash2, CheckSquare, Save } from 'lucide-react';
import type { Task, TaskRequest } from '../../types';
import { projectService } from '../../api/projectService';
import Spinner from '../Spinner';

interface DecomposeStoryModalProps {
    projectId: number;
    story: Task | null;
    onClose: () => void;
    onSuccess: () => void;
}

const DecomposeStoryModal: React.FC<DecomposeStoryModalProps> = ({ projectId, story, onClose, onSuccess }) => {
    const [tasks, setTasks] = useState<Partial<TaskRequest>[]>([
        { title: '', estimation: 1, priority: 'MEDIUM', type: 'TASK', status: 'TODO' }
    ]);
    const [loading, setLoading] = useState(false);

    if (!story) return null;

    const addTaskRow = () => {
        setTasks(prev => [...prev, { title: '', estimation: 1, priority: 'MEDIUM', type: 'TASK', status: 'TODO' }]);
    };

    const removeTaskRow = (index: number) => {
        setTasks(prev => prev.filter((_, i) => i !== index));
    };

    const updateTask = (index: number, field: string, value: any) => {
        const newTasks = [...tasks];
        newTasks[index] = { ...newTasks[index], [field]: value };
        setTasks(newTasks);
    };

    const totalHours = tasks.reduce((sum, t) => sum + (Number(t.estimation) || 0), 0);

    const handleSubmit = async () => {
        const validTasks = tasks.filter(t => t.title && t.title.trim() !== '');
        if (validTasks.length === 0) {
            alert('Ajoutez au moins une tâche valide');
            return;
        }

        setLoading(true);
        try {
            await projectService.decomposeStory(projectId, story.id, validTasks as TaskRequest[]);
            onSuccess();
            onClose();
        } catch (error) {
            console.error(error);
            alert('Erreur lors de la décomposition');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
            <div className="bg-white rounded-2xl w-full max-w-4xl max-h-[90vh] overflow-y-auto shadow-2xl flex flex-col">
                {/* Header */}
                <div className="p-6 border-b border-slate-100 flex justify-between items-center bg-slate-50">
                    <div>
                        <h2 className="text-xl font-black text-slate-800 flex items-center gap-2">
                            <CheckSquare className="text-blue-600" />
                            Décomposer la Story
                        </h2>
                        <div className="text-sm font-medium text-slate-500 mt-1">
                            US-{story.id}: {story.title} ({story.estimation} SP)
                        </div>
                    </div>
                    <button onClick={onClose} className="p-2 hover:bg-slate-200 rounded-full text-slate-400">
                        <X size={24} />
                    </button>
                </div>

                {/* Body */}
                <div className="p-6">
                    <div className="bg-blue-50 text-blue-800 text-sm p-4 rounded-xl mb-6 flex items-start gap-3">
                        <div className="mt-1">💡</div>
                        <div>
                            <strong>Conseil :</strong> Les story points ({story.estimation} SP) mesurent la complexité.
                            Les tâches techniques doivent être estimées en <strong>heures</strong>.
                            Votre vélocité moyenne suggère qu'environ {story.estimation ? story.estimation * 2 : 0} à {story.estimation ? story.estimation * 4 : 0} heures de travail technique sont attendues.
                            <br />
                            <strong>Total planifié pour l'instant : {totalHours} heures.</strong>
                        </div>
                    </div>

                    <div className="space-y-3">
                        {tasks.map((task, index) => (
                            <div key={index} className="flex gap-3 items-start p-3 bg-slate-50 rounded-xl border border-slate-200 group">
                                <div className="mt-3 text-slate-400 font-mono text-xs font-bold w-6">#{index + 1}</div>

                                <div className="flex-1 space-y-2">
                                    <input
                                        type="text"
                                        placeholder="Titre de la tâche technique"
                                        value={task.title}
                                        onChange={(e) => updateTask(index, 'title', e.target.value)}
                                        className="w-full p-2 bg-white border border-slate-200 rounded-lg text-sm font-bold focus:ring-2 focus:ring-blue-500 outline-none"
                                    />
                                    <input
                                        type="text"
                                        placeholder="Description (optionnelle)"
                                        value={task.description || ''}
                                        onChange={(e) => updateTask(index, 'description', e.target.value)}
                                        className="w-full p-2 bg-white border border-slate-200 rounded-lg text-xs text-slate-600 focus:ring-2 focus:ring-blue-500 outline-none"
                                    />
                                </div>

                                <div className="w-24">
                                    <label className="text-[10px] font-bold text-slate-400 uppercase">Heures</label>
                                    <input
                                        type="number" min="1"
                                        value={task.estimation}
                                        onChange={(e) => updateTask(index, 'estimation', parseInt(e.target.value))}
                                        className="w-full p-2 bg-white border border-slate-200 rounded-lg text-sm font-bold text-center"
                                    />
                                </div>

                                <div className="w-32">
                                    <label className="text-[10px] font-bold text-slate-400 uppercase">Compétence</label>
                                    <select
                                        className="w-full p-2 bg-white border border-slate-200 rounded-lg text-sm"
                                    // Mock values for now
                                    >
                                        <option>Backend</option>
                                        <option>Frontend</option>
                                        <option>DevOps</option>
                                    </select>
                                </div>

                                <button
                                    onClick={() => removeTaskRow(index)}
                                    className="mt-6 p-2 text-slate-300 hover:text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                                >
                                    <Trash2 size={18} />
                                </button>
                            </div>
                        ))}
                    </div>

                    <button
                        onClick={addTaskRow}
                        className="mt-4 w-full py-3 bg-slate-50 border-2 border-dashed border-slate-300 rounded-xl text-slate-500 font-bold hover:bg-slate-100 hover:border-slate-400 transition-all flex items-center justify-center gap-2"
                    >
                        <Plus size={20} /> Ajouter une tâche technique
                    </button>
                </div>

                {/* Footer */}
                <div className="p-6 border-t border-slate-100 bg-white rounded-b-2xl flex justify-end gap-3">
                    <button onClick={onClose} className="px-6 py-2 font-bold text-slate-500 hover:bg-slate-100 rounded-lg">
                        Annuler
                    </button>
                    <button
                        onClick={handleSubmit}
                        disabled={loading}
                        className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg font-bold flex items-center gap-2 shadow-lg shadow-blue-200 transition-all disabled:opacity-50"
                    >
                        {loading ? <Spinner /> : <><Save size={18} /> Créer {tasks.length} Tâches</>}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default DecomposeStoryModal;
