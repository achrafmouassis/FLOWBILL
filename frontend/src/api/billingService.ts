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
        const response = await api.get<Quote[]>(`/billing/quotes`, {
            params: { projectId }
        });
        return response.data;
    },
    getInvoices: async (projectId: number) => {
        const response = await api.get<Invoice[]>(`/billing/invoices`, {
            params: { projectId }
        });
        return response.data;
    },
    downloadInvoicePdf: async (invoiceId: number) => {
        const response = await api.get(`/billing/invoices/${invoiceId}/pdf`, {
            responseType: 'blob'
        });
        return response.data;
    },
    createQuote: async (data: any) => {
        const response = await api.post<Quote>(`/billing/quotes`, data);
        return response.data;
    },
    createInvoiceFromQuote: async (quoteId: number) => {
        const response = await api.post<Invoice>(`/billing/invoices/from-quote/${quoteId}`);
        return response.data;
    },
    acceptQuote: async (quoteId: number) => {
        const response = await api.put<Quote>(`/billing/quotes/${quoteId}/accept`);
        return response.data;
    },
    rejectQuote: async (quoteId: number, reason: string) => {
        const response = await api.put<Quote>(`/billing/quotes/${quoteId}/reject`, { reason });
        return response.data;
    }
};
