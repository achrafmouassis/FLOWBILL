import React, { useState } from 'react';
import { X, ChevronRight, ChevronLeft, Save } from 'lucide-react';
import { projectService } from '../../api/projectService';
import Spinner from '../Spinner';

interface CreateStoryModalProps {
    projectId: number;
    isOpen: boolean;
    onClose: () => void;
    onSuccess: () => void;
}

const CreateStoryModal: React.FC<CreateStoryModalProps> = ({ projectId, isOpen, onClose, onSuccess }) => {
    const [step, setStep] = useState(1);
    const [loading, setLoading] = useState(false);
    const [formData, setFormData] = useState({
        title: '',
        description: '',
        estimation: 5,
        moscowPriority: 'SHOULD_HAVE',
        businessValue: 5,
        timeCriticality: 5,
        riskReduction: 5,
        criteria: [''],
        blockerIds: [] as number[]
    });

    // Subcomponent for selector (simplifies MVP)
    const DependencySelector = ({ projectId, selectedIds, onChange }: any) => {
        const [candidates, setCandidates] = useState<any[]>([]);
        const [search, setSearch] = useState('');

        React.useEffect(() => {
            const load = async () => {
                // Fetch potential blockers (stories)
                const data = await projectService.searchBacklogStories(projectId, search);
                setCandidates(data.content.slice(0, 10)); // Limit to 10
            };
            const timer = setTimeout(load, 300);
            return () => clearTimeout(timer);
        }, [projectId, search]);

        const toggle = (id: number) => {
            if (selectedIds.includes(id)) {
                onChange(selectedIds.filter((sid: number) => sid !== id));
            } else {
                onChange([...selectedIds, id]);
            }
        };

        return (
            <div className="border border-slate-200 rounded-xl overflow-hidden">
                <input
                    className="w-full p-3 bg-slate-50 border-b border-slate-200 outline-none text-sm"
                    placeholder="Rechercher une story à bloquer..."
                    value={search}
                    onChange={e => setSearch(e.target.value)}
                />
                <div className="max-h-[200px] overflow-y-auto p-2 space-y-1">
                    {candidates.map(c => (
                        <div key={c.id}
                            onClick={() => toggle(c.id)}
                            className={`p-2 rounded-lg cursor-pointer flex items-center gap-2 text-sm ${selectedIds.includes(c.id) ? 'bg-orange-50 text-orange-700 font-bold' : 'hover:bg-slate-50 text-slate-600'}`}>
                            <div className={`w-4 h-4 border rounded flex items-center justify-center ${selectedIds.includes(c.id) ? 'bg-orange-500 border-orange-500 text-white' : 'border-slate-300'}`}>
                                {selectedIds.includes(c.id) && <X size={10} />}
                            </div>
                            #{c.id} - {c.title}
                        </div>
                    ))}
                </div>
            </div>
        );
    };

    if (!isOpen) return null;

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleCriteriaChange = (index: number, value: string) => {
        const newCriteria = [...formData.criteria];
        newCriteria[index] = value;
        setFormData(prev => ({ ...prev, criteria: newCriteria }));
    };

    const addCriteria = () => {
        setFormData(prev => ({ ...prev, criteria: [...prev.criteria, ''] }));
    };

    const removeCriteria = (index: number) => {
        const newCriteria = formData.criteria.filter((_, i) => i !== index);
        setFormData(prev => ({ ...prev, criteria: newCriteria }));
    };

    const calculateWsjf = () => {
        return ((Number(formData.businessValue) + Number(formData.timeCriticality) + Number(formData.riskReduction)) / Number(formData.estimation)).toFixed(2);
    };

    const handleSubmit = async () => {
        setLoading(true);
        try {
            await projectService.createStory(projectId, {
                ...formData,
                estimation: Number(formData.estimation),
                businessValue: Number(formData.businessValue),
                timeCriticality: Number(formData.timeCriticality),
                riskReduction: Number(formData.riskReduction),
                acceptanceCriteria: formData.criteria.filter(c => c.trim() !== ''),
                blockerIds: formData.blockerIds
            });
            onSuccess();
            onClose();
        } catch (error) {
            console.error(error);
            alert('Failed to create story');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
            <div className="bg-white rounded-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto shadow-2xl flex flex-col">
                {/* Header */}
                <div className="p-6 border-b border-slate-100 flex justify-between items-center sticky top-0 bg-white z-10">
                    <div>
                        <h2 className="text-xl font-black text-slate-800">Créer une Story</h2>
                        <div className="flex gap-2 mt-2">
                            {[1, 2, 3, 4].map(s => (
                                <div key={s} className={`h-1 w-8 rounded-full ${s <= step ? 'bg-blue-600' : 'bg-slate-200'}`} />
                            ))}
                        </div>
                    </div>
                    <button onClick={onClose} className="p-2 hover:bg-slate-100 rounded-full text-slate-400">
                        <X size={24} />
                    </button>
                </div>

                {/* Body */}
                <div className="p-8 flex-1">
                    {step === 1 && (
                        <div className="space-y-6">
                            <div>
                                <label className="block text-sm font-bold text-slate-700 mb-2">Titre de la Story *</label>
                                <input
                                    name="title"
                                    value={formData.title}
                                    onChange={handleChange}
                                    placeholder="En tant que [rôle], je veux [action]..."
                                    className="w-full p-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none font-medium"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-bold text-slate-700 mb-2">Description *</label>
                                <textarea
                                    name="description"
                                    value={formData.description}
                                    onChange={handleChange}
                                    rows={6}
                                    placeholder="Contexte, besoin métier, valeur..."
                                    className="w-full p-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none font-medium resize-none"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-bold text-slate-700 mb-2">Critères d'Acceptation</label>
                                <div className="space-y-2">
                                    {formData.criteria.map((c, i) => (
                                        <div key={i} className="flex gap-2">
                                            <input
                                                value={c}
                                                onChange={(e) => handleCriteriaChange(i, e.target.value)}
                                                placeholder={`Critère ${i + 1}`}
                                                className="flex-1 p-2 bg-slate-50 border border-slate-200 rounded-lg text-sm"
                                            />
                                            <button onClick={() => removeCriteria(i)} className="text-slate-400 hover:text-red-500"><X size={16} /></button>
                                        </div>
                                    ))}
                                    <button onClick={addCriteria} className="text-xs font-bold text-blue-600 hover:text-blue-700">+ Ajouter un critère</button>
                                </div>
                            </div>
                        </div>
                    )}

                    {step === 2 && (
                        <div className="space-y-8">
                            <div>
                                <label className="block text-sm font-bold text-slate-700 mb-4">Priorité MoSCoW *</label>
                                <div className="grid grid-cols-2 gap-4">
                                    {[
                                        { id: 'MUST_HAVE', label: '⭐ Must Have', desc: 'Vital pour le MVP', color: 'border-red-500 bg-red-50 text-red-700' },
                                        { id: 'SHOULD_HAVE', label: '⚡ Should Have', desc: 'Important mais différable', color: 'border-orange-500 bg-orange-50 text-orange-700' },
                                        { id: 'COULD_HAVE', label: '💡 Could Have', desc: 'Bonus / Nice to have', color: 'border-blue-500 bg-blue-50 text-blue-700' },
                                        { id: 'WONT_HAVE', label: '🚫 Won\'t Have', desc: 'Hors périmètre', color: 'border-slate-400 bg-slate-50 text-slate-600' }
                                    ].map(opt => (
                                        <div
                                            key={opt.id}
                                            onClick={() => setFormData(prev => ({ ...prev, moscowPriority: opt.id }))}
                                            className={`p-4 rounded-xl border-2 cursor-pointer transition-all ${formData.moscowPriority === opt.id ? opt.color : 'border-slate-100 hover:border-slate-300'}`}
                                        >
                                            <div className="font-bold">{opt.label}</div>
                                            <div className="text-xs opacity-75">{opt.desc}</div>
                                        </div>
                                    ))}
                                </div>
                            </div>

                            <div>
                                <label className="block text-sm font-bold text-slate-700 mb-4">Estimation (Story Points) *</label>
                                <div className="flex flex-wrap gap-2">
                                    {[1, 2, 3, 5, 8, 13, 21].map(sp => (
                                        <button
                                            key={sp}
                                            onClick={() => setFormData(prev => ({ ...prev, estimation: sp }))}
                                            className={`w-12 h-12 rounded-lg font-bold text-lg transition-all ${Number(formData.estimation) === sp ? 'bg-blue-600 text-white shadow-lg scale-110' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'}`}
                                        >
                                            {sp}
                                        </button>
                                    ))}
                                </div>
                            </div>
                        </div>
                    )}

                    {step === 3 && (
                        <div className="space-y-6">
                            <div className="bg-slate-50 p-6 rounded-xl border border-slate-200">
                                <h3 className="font-bold text-slate-800 mb-4">Score WSJF (Calculé)</h3>
                                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                                    {[
                                        { label: 'Valeur Business', field: 'businessValue', val: formData.businessValue },
                                        { label: 'Urgence Temporelle', field: 'timeCriticality', val: formData.timeCriticality },
                                        { label: 'Réduction Risque', field: 'riskReduction', val: formData.riskReduction }
                                    ].map(item => (
                                        <div key={item.field}>
                                            <div className="flex justify-between text-xs font-bold text-slate-500 mb-2">
                                                <span>{item.label}</span>
                                                <span>{item.val}/10</span>
                                            </div>
                                            <input
                                                type="range" min="1" max="10"
                                                name={item.field}
                                                value={item.val}
                                                onChange={handleChange}
                                                className="w-full accent-blue-600 cursor-pointer"
                                            />
                                        </div>
                                    ))}
                                </div>
                                <div className="mt-6 pt-6 border-t border-slate-200 flex justify-between items-center">
                                    <div className="text-sm text-slate-500">
                                        (BV + TC + RR) / Size =
                                        ({formData.businessValue} + {formData.timeCriticality} + {formData.riskReduction}) / {formData.estimation}
                                    </div>
                                    <div className="text-3xl font-black text-blue-600 bg-white px-4 py-2 rounded-lg shadow-sm">
                                        {calculateWsjf()}
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}

                    {step === 4 && (
                        <div className="space-y-6">
                            <div className="bg-orange-50 p-4 rounded-xl border border-orange-200 mb-4">
                                <h3 className="font-bold text-orange-800 mb-2">Dépendances (Bloqueurs)</h3>
                                <p className="text-sm text-orange-700">Sélectionnez les stories qui doivent être terminées avant celle-ci.</p>
                            </div>

                            <DependencySelector projectId={projectId} selectedIds={formData.blockerIds} onChange={(ids: number[]) => setFormData(prev => ({ ...prev, blockerIds: ids }))} />
                        </div>
                    )}
                </div>

                {/* Footer */}
                <div className="p-6 border-t border-slate-100 bg-slate-50 flex justify-between rounded-b-2xl">
                    {step > 1 ? (
                        <button onClick={() => setStep(step - 1)} className="px-6 py-3 font-bold text-slate-600 hover:bg-white rounded-xl transition-colors flex items-center gap-2">
                            <ChevronLeft size={18} /> Précédent
                        </button>
                    ) : (
                        <div></div>
                    )}

                    {step < 4 ? (
                        <button
                            onClick={() => {
                                if (step === 1 && !formData.title.trim()) { alert('Le titre est obligatoire'); return; }
                                setStep(step + 1);
                            }}
                            className="bg-blue-600 hover:bg-blue-700 text-white px-8 py-3 rounded-xl font-bold flex items-center gap-2 shadow-lg shadow-blue-200 transition-all"
                        >
                            Suivant <ChevronRight size={18} />
                        </button>
                    ) : (
                        <button
                            onClick={handleSubmit}
                            disabled={loading}
                            className="bg-green-600 hover:bg-green-700 text-white px-8 py-3 rounded-xl font-bold flex items-center gap-2 shadow-lg shadow-green-200 transition-all disabled:opacity-50"
                        >
                            {loading ? <Spinner /> : <><Save size={18} /> Créer la Story</>}
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
};

export default CreateStoryModal;
