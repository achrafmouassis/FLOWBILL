import React, { useEffect, useState } from 'react';
import { DragDropContext, Droppable, Draggable, type DropResult } from '@hello-pangea/dnd';
import { projectService } from '../../api/projectService';
import type { Task, Sprint } from '../../types';
import Spinner from '../Spinner';
import { Calendar, CheckCircle2, Circle, Clock } from 'lucide-react';

interface ActiveSprintBoardProps {
    projectId: number;
}

const ActiveSprintBoard: React.FC<ActiveSprintBoardProps> = ({ projectId }) => {
    const [activeSprint, setActiveSprint] = useState<Sprint | null>(null);
    const [tasks, setTasks] = useState<Task[]>([]);
    const [loading, setLoading] = useState(true);

    const loadActiveSprint = async () => {
        setLoading(true);
        try {
            // 1. Get all sprints and find the active one
            const sprints = await projectService.getSprints(projectId);
            const active = sprints.find(s => s.status === 'ACTIVE');

            if (active) {
                setActiveSprint(active);
                // 2. Get tasks for this sprint
                const sprintTasks = await projectService.getSprintTasks(projectId, active.id);
                setTasks(sprintTasks);
            } else {
                setActiveSprint(null);
            }
        } catch (error) {
            console.error("Failed to load active sprint", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadActiveSprint();
    }, [projectId]);

    const onDragEnd = async (result: DropResult) => {
        if (!result.destination) return;

        const { source, destination, draggableId } = result;

        if (source.droppableId === destination.droppableId && source.index === destination.index) return;

        // Optimistic Update
        const newStatus = destination.droppableId;
        const newTasks = Array.from(tasks);
        const taskIndex = newTasks.findIndex(t => t.id === Number(draggableId));

        if (taskIndex === -1) return;

        const updatedTask = { ...newTasks[taskIndex], status: newStatus as any };
        newTasks[taskIndex] = updatedTask;
        setTasks(newTasks);

        // API Call
        try {
            await projectService.updateTask(Number(draggableId), {
                projectId,
                status: newStatus as any
            });
        } catch (error) {
            console.error("Failed to update task status", error);
            loadActiveSprint(); // Revert
        }
    };

    if (loading) return <div className="flex justify-center py-20"><Spinner /></div>;

    if (!activeSprint) {
        return (
            <div className="flex flex-col items-center justify-center py-20 bg-slate-50 rounded-3xl border border-slate-200 border-dashed">
                <div className="w-20 h-20 bg-slate-100 rounded-full flex items-center justify-center mb-6 text-slate-300">
                    <Calendar size={40} />
                </div>
                <h3 className="text-xl font-bold text-slate-800 mb-2">Aucun sprint actif</h3>
                <p className="text-slate-500 max-w-md text-center mb-6">
                    Planifiez et démarrez un sprint depuis l'onglet "Sprints" pour voir le tableau d'avancement.
                </p>
            </div>
        );
    }

    const columns = [
        { id: 'TODO', label: 'À Faire', icon: Circle, color: 'bg-slate-100 border-slate-200 text-slate-600' },
        { id: 'IN_PROGRESS', label: 'En Cours', icon: Clock, color: 'bg-blue-50 border-blue-200 text-blue-700' },
        { id: 'DONE', label: 'Terminé', icon: CheckCircle2, color: 'bg-green-50 border-green-200 text-green-700' }
    ];

    return (
        <div className="h-full flex flex-col">
            {/* Header */}
            <div className="mb-6 bg-white p-4 rounded-xl border border-slate-200 shadow-sm flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
                <div>
                    <h2 className="text-xl font-black text-slate-800 flex items-center gap-2">
                        {activeSprint.name}
                        <span className="px-2 py-0.5 bg-green-100 text-green-700 text-xs rounded-full uppercase">Actif</span>
                    </h2>
                    <div className="flex items-center gap-4 text-sm text-slate-500 mt-1">
                        <span className="flex items-center gap-1"><Calendar size={14} /> {activeSprint.startDate} - {activeSprint.endDate}</span>
                        {activeSprint.goal && <span className="flex items-center gap-1"><CheckCircle2 size={14} /> Objectif: {activeSprint.goal}</span>}
                    </div>
                </div>
                <div className="flex gap-4">
                    <div className="text-right">
                        <div className="text-xs text-slate-400 font-bold uppercase">Reste</div>
                        <div className="font-bold text-slate-700">? Jours</div>
                    </div>
                    <div className="text-right">
                        <div className="text-xs text-slate-400 font-bold uppercase">Avancement</div>
                        <div className="font-bold text-blue-600">
                            {tasks.filter(t => t.status === 'DONE').length} / {tasks.length}
                        </div>
                    </div>
                </div>
            </div>

            {/* Board */}
            <DragDropContext onDragEnd={onDragEnd}>
                <div className="flex-1 grid grid-cols-1 md:grid-cols-3 gap-4 h-full min-h-[500px]">
                    {columns.map(col => (
                        <div key={col.id} className="flex flex-col bg-slate-50 rounded-2xl border border-slate-200 h-full">
                            <div className={`p-4 border-b rounded-t-2xl flex items-center justify-between ${col.color.replace('text-', 'border-').split(' ')[1]}`}>
                                <h3 className={`font-bold flex items-center gap-2 ${col.color.split(' ').pop()}`}>
                                    <col.icon size={18} /> {col.label}
                                </h3>
                                <span className="bg-white/50 px-2 py-1 rounded text-xs font-bold">
                                    {tasks.filter(t => t.status === col.id).length}
                                </span>
                            </div>

                            <Droppable droppableId={col.id}>
                                {(provided, snapshot) => (
                                    <div
                                        ref={provided.innerRef}
                                        {...provided.droppableProps}
                                        className={`flex-1 p-3 space-y-3 overflow-y-auto transition-colors ${snapshot.isDraggingOver ? 'bg-slate-100' : ''}`}
                                    >
                                        {tasks.filter(t => t.status === col.id).map((task, index) => (
                                            <Draggable key={task.id} draggableId={String(task.id)} index={index}>
                                                {(provided) => (
                                                    <div
                                                        ref={provided.innerRef}
                                                        {...provided.draggableProps}
                                                        {...provided.dragHandleProps}
                                                        className="bg-white p-4 rounded-xl shadow-sm border border-slate-100 hover:shadow-md transition-all group"
                                                    >
                                                        <div className="flex justify-between items-start mb-2">
                                                            <span className={`text-[10px] font-bold px-2 py-1 rounded uppercase ${task.type === 'STORY' ? 'bg-blue-50 text-blue-600' : 'bg-slate-100 text-slate-500'
                                                                }`}>
                                                                {task.type}
                                                            </span>
                                                            <span className={`text-[10px] font-bold px-2 py-1 rounded ${task.priority === 'URGENT' ? 'bg-red-50 text-red-600' :
                                                                task.priority === 'HIGH' ? 'bg-orange-50 text-orange-600' :
                                                                    'bg-slate-50 text-slate-500'
                                                                }`}>
                                                                {task.priority || 'MEDIUM'}
                                                            </span>
                                                        </div>
                                                        <h4 className="font-bold text-slate-800 text-sm mb-2 group-hover:text-blue-600 transition-colors">
                                                            {task.title}
                                                        </h4>
                                                        <div className="flex justify-between items-center mt-3 pt-3 border-t border-slate-50">
                                                            <div className="text-xs text-slate-400 font-bold">
                                                                ID-{task.id}
                                                            </div>
                                                            {task.estimation && (
                                                                <div className="text-xs font-bold bg-slate-50 px-2 py-1 rounded text-slate-600">
                                                                    {task.estimation} {task.type === 'STORY' ? 'SP' : 'h'}
                                                                </div>
                                                            )}
                                                        </div>
                                                    </div>
                                                )}
                                            </Draggable>
                                        ))}
                                        {provided.placeholder}
                                    </div>
                                )}
                            </Droppable>
                        </div>
                    ))}
                </div>
            </DragDropContext>
        </div>
    );
};

export default ActiveSprintBoard;
