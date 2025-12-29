import { useEffect, useState } from 'react';
import { reportsService } from '../../api/reportsService';
import type { Alert } from '../../types';

const AlertsWidget = () => {
    const [alerts, setAlerts] = useState<Alert[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchAlerts = async () => {
            try {
                const data = await reportsService.getAlerts();
                setAlerts(data);
            } catch (error) {
                console.error("Failed to fetch alerts", error);
            } finally {
                setLoading(false);
            }
        };

        fetchAlerts();
    }, []);

    if (loading) return <div className="bg-white p-6 rounded-lg shadow h-64 animate-pulse"></div>;

    return (
        <div className="bg-white p-6 rounded-lg shadow h-full">
            <h3 className="text-lg font-bold text-gray-800 mb-4 flex items-center">
                <span className="mr-2">⚠️</span> Alertes & Risques
                {alerts.length > 0 && (
                    <span className="ml-2 bg-red-100 text-red-800 text-xs font-semibold px-2 py-1 rounded-full">
                        {alerts.length}
                    </span>
                )}
            </h3>

            <div className="space-y-3 overflow-y-auto max-h-96">
                {alerts.length === 0 ? (
                    <p className="text-gray-500 text-sm italic">Aucune alerte critique.</p>
                ) : (
                    alerts.map((alert) => (
                        <div key={alert.id} className={`p-3 rounded border-l-4 ${alert.severity === 'CRITICAL' ? 'bg-red-50 border-red-500' :
                            alert.severity === 'WARNING' ? 'bg-orange-50 border-orange-500' :
                                'bg-blue-50 border-blue-500'
                            }`}>
                            <div className="flex justify-between items-start">
                                <h4 className="font-semibold text-sm text-gray-800">{alert.type.replace('_', ' ')}</h4>
                                <span className="text-xs font-mono text-gray-500">{alert.severity}</span>
                            </div>
                            <p className="text-sm text-gray-600 mt-1">{alert.message}</p>
                            {alert.actions && alert.actions.length > 0 && (
                                <div className="mt-2 flex gap-2">
                                    {alert.actions.map(action => (
                                        <button key={action} className="text-xs bg-white border border-gray-300 px-2 py-1 rounded hover:bg-gray-50">
                                            {action}
                                        </button>
                                    ))}
                                </div>
                            )}
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default AlertsWidget;
