import { useEffect, useState } from 'react';
import { reportsService } from '../../api/reportsService';
import type { SprintDashboardResponse } from '../../types';
import VelocityChart from './VelocityChart';

interface SprintClosureModalProps {
    sprint: SprintDashboardResponse;
    onClose: () => void;
    onSuccess: () => void;
}

const SprintClosureModal = ({ sprint, onClose, onSuccess }: SprintClosureModalProps) => {
    const [action, setAction] = useState<'BACKLOG' | 'NEXT_SPRINT'>('BACKLOG');
    const [loading, setLoading] = useState(false);

    const handleClosure = async () => {
        setLoading(true);
        try {
            await reportsService.completeSprint(sprint.id, {
                actionForIncompleteTasks: action,
                nextSprintId: null // For simplified MVP
            });
            onSuccess();
        } catch (error) {
            console.error("Failed to complete sprint", error);
            alert("Erreur lors de la clôture du sprint");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white p-6 rounded-lg shadow-xl max-w-md w-full">
                <h3 className="text-lg font-bold mb-4">Clôturer Sprint: {sprint.name}</h3>
                <p className="mb-4 text-sm text-gray-600">
                    Il reste {sprint.totalPoints - sprint.completedPoints} points non terminés.
                    Que voulez-vous faire des tâches restantes ?
                </p>
                <div className="space-y-2 mb-6">
                    <label className="flex items-center space-x-3 p-3 border rounded hover:bg-gray-50 cursor-pointer">
                        <input
                            type="radio"
                            name="action"
                            value="BACKLOG"
                            checked={action === 'BACKLOG'}
                            onChange={() => setAction('BACKLOG')}
                            className="text-blue-600"
                        />
                        <div>
                            <span className="block font-medium text-sm">Renvoyer au Backlog</span>
                            <span className="block text-xs text-gray-500">Les tâches seront dépriorisées</span>
                        </div>
                    </label>
                    <label className="flex items-center space-x-3 p-3 border rounded hover:bg-gray-50 cursor-pointer">
                        <input
                            type="radio"
                            name="action"
                            value="NEXT_SPRINT"
                            checked={action === 'NEXT_SPRINT'}
                            onChange={() => setAction('NEXT_SPRINT')}
                            className="text-blue-600"
                        />
                        <div>
                            <span className="block font-medium text-sm">Déplacer vers le prochain Sprint</span>
                            <span className="block text-xs text-gray-500">Créer un nouveau sprint si nécessaire</span>
                        </div>
                    </label>
                </div>
                <div className="flex justify-end space-x-3">
                    <button onClick={onClose} className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded">
                        Annuler
                    </button>
                    <button
                        onClick={handleClosure}
                        disabled={loading}
                        className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 disabled:opacity-50"
                    >
                        {loading ? 'Clôture...' : 'Clôturer le Sprint'}
                    </button>
                </div>
            </div>
        </div>
    );
};

const ActiveSprints = () => {
    const [sprints, setSprints] = useState<SprintDashboardResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [selectedSprint, setSelectedSprint] = useState<SprintDashboardResponse | null>(null);

    const fetchSprints = async () => {
        setLoading(true);
        try {
            const data = await reportsService.getActiveSprints();
            setSprints(data);
        } catch (error) {
            console.error("Failed to fetch active sprints", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchSprints();
    }, []);

    if (loading && sprints.length === 0) return <div className="p-6 text-center">Chargement des sprints...</div>;

    return (
        <div className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {sprints.map((sprint) => (
                    <div key={sprint.id} className="bg-white rounded-lg shadow p-6 border-l-4 border-l-blue-500">
                        <div className="flex justify-between items-start mb-4">
                            <div>
                                <h3 className="text-lg font-bold text-gray-800">{sprint.name}</h3>
                                <p className="text-sm text-gray-500">
                                    {new Date(sprint.startDate).toLocaleDateString()} - {new Date(sprint.endDate).toLocaleDateString()}
                                </p>
                            </div>
                            <span className={`px-2 py-1 text-xs font-bold rounded uppercase ${sprint.riskStatus === 'RED' ? 'bg-red-100 text-red-800' :
                                sprint.riskStatus === 'ORANGE' ? 'bg-orange-100 text-orange-800' :
                                    'bg-green-100 text-green-800'
                                }`}>
                                {sprint.riskStatus} Risk
                            </span>
                        </div>

                        <div className="mb-4">
                            <div className="flex justify-between text-sm mb-1">
                                <span className="text-gray-600">Progression</span>
                                <span className="font-semibold">{Math.round(sprint.progress * 100)}%</span>
                            </div>
                            <div className="w-full bg-gray-200 rounded-full h-2">
                                <div
                                    className="bg-blue-600 h-2 rounded-full transition-all duration-500"
                                    style={{ width: `${sprint.progress * 100}%` }}
                                ></div>
                            </div>
                        </div>

                        <div className="flex justify-between items-center text-sm text-gray-600 mb-6">
                            <div>
                                <p className="font-bold text-gray-800">{sprint.completedPoints} / {sprint.totalPoints}</p>
                                <p className="text-xs">Story Points</p>
                            </div>
                            <div className="text-right">
                                <p className="font-bold text-gray-800">{sprint.daysRemaining}j</p>
                                <p className="text-xs">Restants</p>
                            </div>
                        </div>

                        <div className="flex justify-end">
                            <button
                                onClick={() => setSelectedSprint(sprint)}
                                className="text-sm border border-red-200 text-red-600 hover:bg-red-50 px-3 py-1.5 rounded transition-colors"
                            >
                                Clôturer Sprint
                            </button>
                        </div>
                    </div>
                ))}
            </div>

            {sprints.length === 0 && (
                <div className="text-center p-8 bg-white rounded-lg border border-dashed border-gray-300">
                    <p className="text-gray-500">Aucun sprint actif pour le moment.</p>
                </div>
            )}

            {/* Velocity Chart Integration */}
            <VelocityChart />

            {selectedSprint && (
                <SprintClosureModal
                    sprint={selectedSprint}
                    onClose={() => setSelectedSprint(null)}
                    onSuccess={() => {
                        setSelectedSprint(null);
                        fetchSprints();
                    }}
                />
            )}
        </div>
    );
};

export default ActiveSprints;
