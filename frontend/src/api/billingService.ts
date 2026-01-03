import api from './api';

export interface QuoteLine {
    description: string;
    quantity: number;
    unitPrice: number;
    tvaRate: number;
}

export interface Quote {
    id: number;
    quoteNumber: string;
    projectId: number;
    clientId: number;
    clientName: string;
    status: string;
    amountHt: number;
    tvaAmount: number;
    amountTtc: number;
    validUntil: string;
}

export interface Invoice {
    id: number;
    invoiceNumber: string;
    projectId: number;
    clientId: number;
    clientName: string;
    status: string;
    amountHt: number;
    tvaAmount: number;
    amountTtc: number;
    paidAmount: number;
    dueDate: string;
}

export const billingService = {
    getQuotes: async (projectId: number) => {
        const response = await api.get<Quote[]>(`/billing/api/billing/quotes`, {
            params: { projectId }
        });
        return response.data;
    },
    getInvoices: async (projectId: number) => {
        const response = await api.get<Invoice[]>(`/billing/api/billing/invoices`, {
            params: { projectId }
        });
        return response.data;
    },
    downloadInvoicePdf: async (invoiceId: number) => {
        const response = await api.get(`/billing/api/billing/invoices/${invoiceId}/pdf`, {
            responseType: 'blob'
        });
        return response.data;
    }
};
