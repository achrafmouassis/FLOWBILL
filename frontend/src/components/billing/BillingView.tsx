import React, { useEffect, useState } from 'react';
import { billingService } from '../../api/billingService';
import type { Quote, Invoice } from '../../api/billingService';
import { FileText, Receipt, Download, Plus, Clock, CheckCircle2, AlertCircle } from 'lucide-react';
import Spinner from '../Spinner';

interface BillingViewProps {
    projectId: number;
}

import CreateQuoteModal from './CreateQuoteModal';

const BillingView: React.FC<BillingViewProps> = ({ projectId }) => {
    const [quotes, setQuotes] = useState<Quote[]>([]);
    const [invoices, setInvoices] = useState<Invoice[]>([]);
    const [loading, setLoading] = useState(true);
    const [activeSection, setActiveSection] = useState<'quotes' | 'invoices'>('quotes');
    const [isCreateQuoteOpen, setIsCreateQuoteOpen] = useState(false);

    const fetchBillingData = async () => {
        setLoading(true);
        try {
            const [qData, iData] = await Promise.all([
                billingService.getQuotes(projectId),
                billingService.getInvoices(projectId)
            ]);
            setQuotes(qData);
            setInvoices(iData);
        } catch (err) {
            console.error("Failed to fetch billing data", err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchBillingData();
    }, [projectId]);

    const handleConvertToInvoice = async (quoteId: number) => {
        if (!window.confirm("Voulez-vous convertir ce devis en facture ?")) return;
        try {
            await billingService.createInvoiceFromQuote(quoteId);
            fetchBillingData();
            setActiveSection('invoices');
        } catch (err) {
            console.error("Conversion failed", err);
            alert("Erreur lors de la conversion en facture");
        }
    };

    const handleAcceptQuote = async (quoteId: number) => {
        if (!window.confirm("Accepter ce devis ?")) return;
        try {
            await billingService.acceptQuote(quoteId);
            fetchBillingData();
        } catch (err) {
            console.error("Accept failed", err);
            alert("Erreur lors de l'acceptation");
        }
    };

    const handleRejectQuote = async (quoteId: number) => {
        const reason = window.prompt("Raison du refus :");
        if (reason === null) return;
        try {
            await billingService.rejectQuote(quoteId, reason);
            fetchBillingData();
        } catch (err) {
            console.error("Reject failed", err);
            alert("Erreur lors du refus");
        }
    };

    const handleDownload = async (invoiceId: number) => {
        // ... (existing download logic)
        try {
            const blob = await billingService.downloadInvoicePdf(invoiceId);
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `facture-${invoiceId}.pdf`;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
        } catch (err) {
            console.error("Download failed", err);
        }
    };

    if (loading) return <div className="h-64 flex items-center justify-center"><Spinner /></div>;

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div className="flex gap-2 p-1 bg-slate-100 rounded-xl">
                    <button
                        onClick={() => setActiveSection('quotes')}
                        className={`px-4 py-2 rounded-lg text-xs font-bold transition-all ${activeSection === 'quotes' ? 'bg-white text-blue-600 shadow-sm' : 'text-slate-500 hover:text-slate-700'}`}
                    >
                        Devis ({quotes.length})
                    </button>
                    <button
                        onClick={() => setActiveSection('invoices')}
                        className={`px-4 py-2 rounded-lg text-xs font-bold transition-all ${activeSection === 'invoices' ? 'bg-white text-blue-600 shadow-sm' : 'text-slate-500 hover:text-slate-700'}`}
                    >
                        Factures ({invoices.length})
                    </button>
                </div>
                <button
                    onClick={() => {
                        if (activeSection === 'quotes') setIsCreateQuoteOpen(true);
                        else alert("Veuillez créer une facture à partir d'un devis accepté.");
                    }}
                    className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-xl text-xs font-bold hover:bg-blue-700 transition-all shadow-lg shadow-blue-200"
                >
                    <Plus size={14} />
                    Nouveau {activeSection === 'quotes' ? 'Devis' : 'Facture'}
                </button>
            </div>

            {activeSection === 'quotes' ? (
                <div className="bg-white rounded-[2.5rem] border border-slate-200 overflow-hidden shadow-sm">
                    <table className="w-full text-left border-collapse">
                        <thead>
                            <tr className="bg-slate-50/50 border-b border-slate-200">
                                <th className="px-6 py-4 text-[10px] font-black text-slate-400 uppercase tracking-widest">Référence</th>
                                <th className="px-6 py-4 text-[10px] font-black text-slate-400 uppercase tracking-widest">Client</th>
                                <th className="px-6 py-4 text-[10px] font-black text-slate-400 uppercase tracking-widest">Montant TTC</th>
                                <th className="px-6 py-4 text-[10px] font-black text-slate-400 uppercase tracking-widest">Statut</th>
                                <th className="px-6 py-4 text-[10px] font-black text-slate-400 uppercase tracking-widest text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                            {quotes.length === 0 ? (
                                <tr>
                                    <td colSpan={5} className="px-6 py-20 text-center text-slate-400 font-medium italic">Aucun devis trouvé pour ce projet.</td>
                                </tr>
                            ) : quotes.map(quote => (
                                <tr key={quote.id} className="hover:bg-slate-50/50 transition-colors group">
                                    <td className="px-6 py-4">
                                        <div className="flex items-center gap-3">
                                            <div className="w-8 h-8 rounded-lg bg-orange-50 flex items-center justify-center text-orange-600">
                                                <FileText size={16} />
                                            </div>
                                            <span className="font-bold text-slate-900">{quote.quoteNumber}</span>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 font-medium text-slate-600 font-bold">{quote.clientName}</td>
                                    <td className="px-6 py-4 font-bold text-slate-900">{quote.amountTtc.toLocaleString()} MAD</td>
                                    <td className="px-6 py-4">
                                        <span className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-[10px] font-black tracking-tight ${quote.status === 'ACCEPTED' ? 'bg-emerald-50 text-emerald-600' :
                                            quote.status === 'REJECTED' ? 'bg-rose-50 text-rose-600' :
                                                'bg-orange-50 text-orange-600'
                                            }`}>
                                            {quote.status === 'ACCEPTED' && <CheckCircle2 size={10} />}
                                            {quote.status === 'REJECTED' && <AlertCircle size={10} />}
                                            {quote.status === 'DRAFT' && <Clock size={10} />}
                                            {quote.status}
                                        </span>
                                    </td>
                                    <td className="px-6 py-4 text-right flex justify-end gap-2">
                                        {(quote.status === 'DRAFT' || quote.status === 'SENT') && (
                                            <>
                                                <button
                                                    onClick={() => handleAcceptQuote(quote.id)}
                                                    className="flex items-center gap-1 px-3 py-1.5 bg-emerald-50 text-emerald-600 rounded-lg text-[10px] font-black uppercase tracking-widest hover:bg-emerald-100 transition-all"
                                                    title="Accepter"
                                                >
                                                    <CheckCircle2 size={12} /> Accepter
                                                </button>
                                                <button
                                                    onClick={() => handleRejectQuote(quote.id)}
                                                    className="flex items-center gap-1 px-3 py-1.5 bg-rose-50 text-rose-600 rounded-lg text-[10px] font-black uppercase tracking-widest hover:bg-rose-100 transition-all"
                                                    title="Refuser"
                                                >
                                                    <AlertCircle size={12} /> Refuser
                                                </button>
                                            </>
                                        )}
                                        {quote.status === 'ACCEPTED' && (
                                            <button
                                                onClick={() => handleConvertToInvoice(quote.id)}
                                                className="flex items-center gap-1 px-3 py-1.5 bg-blue-50 text-blue-600 rounded-lg text-[10px] font-black uppercase tracking-widest hover:bg-blue-100 transition-all"
                                                title="Convertir en facture"
                                            >
                                                <Receipt size={12} /> Facturer
                                            </button>
                                        )}
                                        <button className="p-2 hover:bg-white hover:shadow-md rounded-lg text-slate-400 hover:text-blue-600 transition-all">
                                            <FileText size={16} />
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            ) : (
                <div className="bg-white rounded-[2.5rem] border border-slate-200 overflow-hidden shadow-sm">
                    <table className="w-full text-left border-collapse">
                        <thead>
                            <tr className="bg-slate-50/50 border-b border-slate-200">
                                <th className="px-6 py-4 text-[10px] font-black text-slate-400 uppercase tracking-widest">Référence</th>
                                <th className="px-6 py-4 text-[10px] font-black text-slate-400 uppercase tracking-widest">Client</th>
                                <th className="px-6 py-4 text-[10px] font-black text-slate-400 uppercase tracking-widest">Montant TTC</th>
                                <th className="px-6 py-4 text-[10px] font-black text-slate-400 uppercase tracking-widest">Status</th>
                                <th className="px-6 py-4 text-[10px] font-black text-slate-400 uppercase tracking-widest text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                            {invoices.length === 0 ? (
                                <tr>
                                    <td colSpan={5} className="px-6 py-20 text-center text-slate-400 font-medium italic">Aucune facture trouvée pour ce projet.</td>
                                </tr>
                            ) : invoices.map(invoice => (
                                <tr key={invoice.id} className="hover:bg-slate-50/50 transition-colors group">
                                    <td className="px-6 py-4">
                                        <div className="flex items-center gap-3">
                                            <div className="w-8 h-8 rounded-lg bg-blue-50 flex items-center justify-center text-blue-600">
                                                <Receipt size={16} />
                                            </div>
                                            <span className="font-bold text-slate-900">{invoice.invoiceNumber}</span>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 font-medium text-slate-600 font-bold">{invoice.clientName}</td>
                                    <td className="px-6 py-4 font-bold text-slate-900">{invoice.amountTtc.toLocaleString()} MAD</td>
                                    <td className="px-6 py-4">
                                        <span className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-[10px] font-black tracking-tight ${invoice.status === 'PAID' ? 'bg-emerald-50 text-emerald-600' :
                                            invoice.status === 'OVERDUE' ? 'bg-rose-50 text-rose-600' :
                                                'bg-blue-50 text-blue-600'
                                            }`}>
                                            {invoice.status === 'PAID' && <CheckCircle2 size={10} />}
                                            {invoice.status === 'OVERDUE' && <AlertCircle size={10} />}
                                            {invoice.status === 'ISSUED' && <Clock size={10} />}
                                            {invoice.status}
                                        </span>
                                    </td>
                                    <td className="px-6 py-4 text-right">
                                        <button
                                            onClick={() => handleDownload(invoice.id)}
                                            className="p-2 hover:bg-white hover:shadow-md rounded-lg text-slate-400 hover:text-blue-600 transition-all"
                                        >
                                            <Download size={16} />
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}

            <CreateQuoteModal
                projectId={projectId}
                isOpen={isCreateQuoteOpen}
                onClose={() => setIsCreateQuoteOpen(false)}
                onSuccess={fetchBillingData}
            />
        </div>
    );
};

export default BillingView;
