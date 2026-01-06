import React, { useState } from 'react';
import { X, Plus, Trash2 } from 'lucide-react';
import { billingService } from '../../api/billingService';

interface CreateQuoteModalProps {
    projectId: number;
    isOpen: boolean;
    onClose: () => void;
    onSuccess: () => void;
}

const CreateQuoteModal: React.FC<CreateQuoteModalProps> = ({ projectId, isOpen, onClose, onSuccess }) => {
    const [lines, setLines] = useState([{ description: '', quantity: 1, unitPrice: 0 }]);
    const [tvaRate, setTvaRate] = useState(20);
    const [notes, setNotes] = useState('');
    const [loading, setLoading] = useState(false);

    if (!isOpen) return null;

    const handleAddLine = () => {
        setLines([...lines, { description: '', quantity: 1, unitPrice: 0 }]);
    };

    const handleRemoveLine = (index: number) => {
        setLines(lines.filter((_, i) => i !== index));
    };

    const handleLineChange = (index: number, field: string, value: any) => {
        const newLines = [...lines];
        (newLines[index] as any)[field] = value;
        setLines(newLines);
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        try {
            await billingService.createQuote({
                projectId,
                lines,
                tvaRate,
                notes
            });
            onSuccess();
            onClose();
        } catch (error) {
            console.error("Failed to create quote", error);
            alert("Erreur lors de la création du devis");
        } finally {
            setLoading(false);
        }
    };

    const calculateTotalHt = () => {
        return lines.reduce((acc, line) => acc + (line.quantity * line.unitPrice), 0);
    };

    const totalHt = calculateTotalHt();
    const tvaAmount = (totalHt * tvaRate) / 100;
    const totalTtc = totalHt + tvaAmount;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 backdrop-blur-sm p-4">
            <div className="bg-white rounded-[2.5rem] shadow-2xl w-full max-w-4xl max-h-[90vh] overflow-hidden flex flex-col">
                <div className="px-8 py-6 border-b border-slate-100 flex justify-between items-center bg-slate-50/50">
                    <div>
                        <h2 className="text-2xl font-black text-slate-800 tracking-tight">Nouveau Devis</h2>
                        <p className="text-slate-500 text-sm font-medium">Créez une proposition commerciale pour ce projet.</p>
                    </div>
                    <button onClick={onClose} className="p-2 hover:bg-white hover:shadow-md rounded-xl transition-all text-slate-400 hover:text-slate-600">
                        <X size={24} />
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto p-8 space-y-8">
                    {/* Items Section */}
                    <div className="space-y-4">
                        <div className="flex justify-between items-center">
                            <h3 className="text-lg font-bold text-slate-800">Articles / Services</h3>
                            <button
                                type="button"
                                onClick={handleAddLine}
                                className="flex items-center gap-2 px-3 py-1.5 bg-blue-50 text-blue-600 rounded-lg text-xs font-bold hover:bg-blue-100 transition-all"
                            >
                                <Plus size={14} /> Ajouter une ligne
                            </button>
                        </div>

                        <div className="space-y-3">
                            {lines.map((line, index) => (
                                <div key={index} className="flex gap-4 items-start bg-slate-50 p-4 rounded-2xl border border-slate-100">
                                    <div className="flex-1">
                                        <label className="block text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1.5 ml-1">Description</label>
                                        <input
                                            required
                                            type="text"
                                            value={line.description}
                                            onChange={(e) => handleLineChange(index, 'description', e.target.value)}
                                            className="w-full px-4 py-2.5 bg-white border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500/20 text-sm font-bold"
                                            placeholder="Ex: Développement Frontend"
                                        />
                                    </div>
                                    <div className="w-24">
                                        <label className="block text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1.5 ml-1">Qté</label>
                                        <input
                                            required
                                            type="number"
                                            min="0.01"
                                            step="0.01"
                                            value={line.quantity}
                                            onChange={(e) => handleLineChange(index, 'quantity', parseFloat(e.target.value))}
                                            className="w-full px-4 py-2.5 bg-white border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500/20 text-sm font-bold"
                                        />
                                    </div>
                                    <div className="w-32">
                                        <label className="block text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1.5 ml-1">Prix Unit.</label>
                                        <input
                                            required
                                            type="number"
                                            min="0"
                                            step="0.01"
                                            value={line.unitPrice}
                                            onChange={(e) => handleLineChange(index, 'unitPrice', parseFloat(e.target.value))}
                                            className="w-full px-4 py-2.5 bg-white border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500/20 text-sm font-bold"
                                        />
                                    </div>
                                    <div className="pt-7">
                                        <button
                                            type="button"
                                            onClick={() => handleRemoveLine(index)}
                                            className="p-2 text-slate-400 hover:text-rose-500 hover:bg-rose-50 rounded-lg transition-all"
                                            disabled={lines.length === 1}
                                        >
                                            <Trash2 size={18} />
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                        <div>
                            <label className="block text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1.5 ml-1">Notes / Conditions</label>
                            <textarea
                                value={notes}
                                onChange={(e) => setNotes(e.target.value)}
                                rows={4}
                                className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl focus:outline-none focus:ring-2 focus:ring-blue-500/20 text-sm font-medium resize-none"
                                placeholder="Conditions de paiement, délais..."
                            />
                        </div>
                        <div className="bg-slate-50 rounded-[2rem] p-6 space-y-4">
                            <div className="flex justify-between items-center">
                                <span className="text-slate-500 text-sm">Taux TVA (%)</span>
                                <input
                                    type="number"
                                    value={tvaRate}
                                    onChange={(e) => setTvaRate(parseFloat(e.target.value))}
                                    className="w-20 px-3 py-1.5 bg-white border border-slate-200 rounded-lg text-sm font-bold text-right"
                                />
                            </div>
                            <div className="pt-4 border-t border-slate-200 space-y-2">
                                <div className="flex justify-between text-sm">
                                    <span className="text-slate-500">Total HT</span>
                                    <span className="font-bold text-slate-900">{totalHt.toLocaleString()} MAD</span>
                                </div>
                                <div className="flex justify-between text-sm">
                                    <span className="text-slate-500">TVA ({tvaRate}%)</span>
                                    <span className="font-bold text-slate-900">{tvaAmount.toLocaleString()} MAD</span>
                                </div>
                                <div className="flex justify-between pt-2">
                                    <span className="text-slate-900 font-black">TOTAL TTC</span>
                                    <span className="text-xl font-black text-blue-600">{totalTtc.toLocaleString()} MAD</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="flex justify-end gap-3 pt-4">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-6 py-3 bg-slate-100 text-slate-600 rounded-2xl text-sm font-bold hover:bg-slate-200 transition-all"
                        >
                            Annuler
                        </button>
                        <button
                            type="submit"
                            disabled={loading}
                            className="px-8 py-3 bg-blue-600 text-white rounded-2xl text-sm font-black hover:bg-blue-700 transition-all shadow-lg shadow-blue-200 disabled:opacity-50"
                        >
                            {loading ? "Création..." : "Générer le Devis"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default CreateQuoteModal;
