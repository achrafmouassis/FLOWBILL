import React, { useEffect, useState } from 'react';
import { DragDropContext, Droppable, Draggable, type DropResult } from '@hello-pangea/dnd';
import type { Task, Sprint } from '../../types';
import { projectService } from '../../api/projectService';
import StoryCard from './StoryCard'; // Reusing StoryCard
import CreateSprintModal from './CreateSprintModal';
import { Plus, Calendar, AlertCircle } from 'lucide-react';
import Spinner from '../Spinner';

interface SprintPlanningBoardProps {
    projectId: number;
}

const SprintPlanningBoard: React.FC<SprintPlanningBoardProps> = ({ projectId }) => {
    const [backlogStories, setBacklogStories] = useState<Task[]>([]);
    const [sprints, setSprints] = useState<Sprint[]>([]);
    const [sprintTasks, setSprintTasks] = useState<Record<number, Task[]>>({}); // Map SprintID -> Tasks
    const [loading, setLoading] = useState(true);
    const [isCreateSprintOpen, setIsCreateSprintOpen] = useState(false);

    const loadData = async () => {
        setLoading(true);
        try {
            // 1. Fetch Backlog (Unplanned Stories)
            // Fetch all (pagination hack: size 100 or loop, for MVP size 100)
            const backlogData = await projectService.searchBacklogStories(projectId, undefined, undefined, undefined, true, 0, 100);
            setBacklogStories(backlogData.content);

            // 2. Fetch Sprints (Planned & Active)
            const sprintsData = await projectService.getSprints(projectId);
            const activeOrPlanned = sprintsData.filter(s => s.status !== 'COMPLETED');
            setSprints(activeOrPlanned);

            // 3. Fetch Tasks for each Sprint
            const tasksMap: Record<number, Task[]> = {};
            await Promise.all(activeOrPlanned.map(async (s) => {
                const tasks = await projectService.getSprintTasks(projectId, s.id);
                tasksMap[s.id] = tasks.filter(t => t.type === 'STORY'); // Only planning Stories for now
            }));
            setSprintTasks(tasksMap);

        } catch (error) {
            console.error("Failed to load planning board", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadData();
    }, [projectId]);

    const onDragEnd = async (result: DropResult) => {
        const { source, destination, draggableId } = result;

        // Dropped outside
        if (!destination) return;

        // Dropped in same place
        if (source.droppableId === destination.droppableId && source.index === destination.index) return;

        // Identify Source and Target Lists
        let sourceList = source.droppableId === 'BACKLOG' ? [...backlogStories] : [...(sprintTasks[Number(source.droppableId)] || [])];
        let destList = destination.droppableId === 'BACKLOG' ? [...backlogStories] : [...(sprintTasks[Number(destination.droppableId)] || [])];

        // If dragging within same list (reordering) - For MVP manual order might not be fully implemented, but visual reorder is good
        if (source.droppableId === destination.droppableId) {
            const [moved] = sourceList.splice(source.index, 1);
            sourceList.splice(destination.index, 0, moved);

            // Update State Locally
            if (source.droppableId === 'BACKLOG') {
                setBacklogStories(sourceList);
            } else {
                setSprintTasks(prev => ({ ...prev, [Number(source.droppableId)]: sourceList }));
            }
            // Trigger API for reorder (Not strictly MVP, can skip for now or set manualOrder)
            return;
        }

        // Dragging between lists (Moving Story)
        const [movedTask] = sourceList.splice(source.index, 1);
        destList.splice(destination.index, 0, movedTask);

        // Update UI immediately (Optimistic)
        if (source.droppableId === 'BACKLOG') {
            setBacklogStories(sourceList);
        } else {
            setSprintTasks(prev => ({ ...prev, [Number(source.droppableId)]: sourceList }));
        }

        if (destination.droppableId === 'BACKLOG') {
            setBacklogStories(destList);
        } else {
            setSprintTasks(prev => ({ ...prev, [Number(destination.droppableId)]: destList }));
        }

        // Call API
        try {
            const taskId = Number(draggableId);
            const targetSprintId = destination.droppableId === 'BACKLOG' ? null : Number(destination.droppableId);

            // Use updateTask to set sprintId
            // Note: API might expect sprintId: null explicitly if moving to backlog
            // Or specifically updateTask({ sprintId: ... })
            await projectService.updateTask(taskId, {
                projectId,
                title: movedTask.title, // Required by DTO? Hopefully partial works or we send minimal. 
                // Creating a specific patch method would be cleaner, but reuse updateTask for now.
                sprintId: targetSprintId !== null ? targetSprintId : undefined
            });

            // Note: updateTask requires title etc? Let's check projectService.
            // If updateTask expects full object, we might need to send current values.
            // Assuming updateTask handles partial updates or we adjusted it.
            // Actually DTO usually requires fields.
            // Let's assume for MVP we fetch task details or backend handles nulls gracefully if patch.

            // Ideally: await projectService.assignSprint(taskId, targetSprintId);
            // Since we use generic updateTask:
            await projectService.updateTask(taskId, {
                title: movedTask.title, // Hack if required
                projectId: projectId,
                sprintId: targetSprintId || undefined
            });

            // If moving to backlog, we might need a specific way to unset sprintId if undefined is ignored?
            // Actually, if we send null for sprintId in JSON, backend should handle it. 
            // projectService.updateTask typings: sprintId?: number.
            // If we pass null, TS might complain if type is number | undefined. cast to any if needed.
            if (destination.droppableId === 'BACKLOG') {
                // Explicitly unassign sprint
                // We might need a specific endpoint or ensure updateTask clears it.
                // For now, let's assume updateTask clearing logic works if we send null (casted).
                await projectService.updateTask(taskId, { ...movedTask, sprintId: null as any });
            } else {
                await projectService.updateTask(taskId, { ...movedTask, sprintId: Number(destination.droppableId) });
            }

        } catch (err) {
            console.error("Move failed", err);
            loadData(); // Revert on failure
        }
    };

    // Helper to calculate velocity/load
    const calculateLoad = (tasks: Task[]) => tasks.reduce((sum, t) => sum + (t.estimation || 0), 0);

    return (
        <div className="flex h-[calc(100vh-200px)] gap-6 p-1">
            <DragDropContext onDragEnd={onDragEnd}>
                {/* BACKLOG COLUMN */}
                <div className="w-1/3 flex flex-col bg-slate-100 rounded-2xl border border-slate-200">
                    <div className="p-4 border-b border-slate-200 bg-slate-50 rounded-t-2xl">
                        <h3 className="font-black text-slate-700 flex items-center gap-2">
                            <AlertCircle size={18} className="text-slate-400" />
                            Backlog (Non planifié)
                        </h3>
                        <div className="text-xs text-slate-500 mt-1">
                            {backlogStories.length} stories • {calculateLoad(backlogStories)} SP
                        </div>
                    </div>
                    <Droppable droppableId="BACKLOG">
                        {(provided, snapshot) => (
                            <div
                                ref={provided.innerRef}
                                {...provided.droppableProps}
                                className={`flex-1 overflow-y-auto p-3 space-y-3 transition-colors ${snapshot.isDraggingOver ? 'bg-blue-50/50' : ''}`}
                            >
                                {loading && <div className="text-center py-10"><Spinner /></div>}
                                {!loading && backlogStories.map((story, index) => (
                                    <Draggable key={story.id} draggableId={String(story.id)} index={index}>
                                        {(provided) => (
                                            <div
                                                ref={provided.innerRef}
                                                {...provided.draggableProps}
                                                {...provided.dragHandleProps}
                                            >
                                                <StoryCard story={story} />
                                            </div>
                                        )}
                                    </Draggable>
                                ))}
                                {provided.placeholder}
                            </div>
                        )}
                    </Droppable>
                </div>

                {/* SPRINTS COLUMN */}
                <div className="w-2/3 flex flex-col space-y-4 overflow-y-auto pr-2">
                    <div className="flex justify-between items-center">
                        <h3 className="font-bold text-slate-500 uppercase tracking-widest text-sm">Sprints Planifiés</h3>
                        <button
                            onClick={() => setIsCreateSprintOpen(true)}
                            className="text-blue-600 font-bold text-sm bg-blue-50 px-3 py-1 rounded-lg hover:bg-blue-100 flex items-center gap-1"
                        >
                            <Plus size={14} /> Planifier Sprint
                        </button>
                    </div>

                    {sprints.map(sprint => (
                        <div key={sprint.id} className="bg-white border border-slate-200 rounded-2xl shadow-sm flex flex-col">
                            <div className="p-4 border-b border-slate-100 flex justify-between items-center bg-slate-50/50 rounded-t-2xl">
                                <div>
                                    <h4 className="font-black text-slate-800 text-lg">{sprint.name}</h4>
                                    <div className="text-xs text-slate-500 flex items-center gap-2 mt-1">
                                        <Calendar size={12} />
                                        {sprint.startDate} - {sprint.endDate}
                                        <span className={`px-2 py-0.5 rounded text-[10px] uppercase font-bold ${sprint.status === 'ACTIVE' ? 'bg-green-100 text-green-700' : 'bg-slate-200 text-slate-600'}`}>
                                            {sprint.status}
                                        </span>
                                    </div>
                                </div>
                                <div className="text-right">
                                    <div className="text-xs text-slate-400 font-bold uppercase">Planifié</div>
                                    <div className="text-xl font-bold text-blue-600">
                                        {calculateLoad(sprintTasks[sprint.id] || [])} <span className="text-sm text-slate-400">SP</span>
                                    </div>
                                    {sprint.status === 'PLANNED' && (
                                        <button
                                            onClick={async () => {
                                                if (confirm(`Démarrer le sprint "${sprint.name}" ?`)) {
                                                    try {
                                                        await projectService.startSprint(sprint.id);
                                                        loadData(); // Refresh to see status change
                                                    } catch (e) {
                                                        alert('Impossible de démarrer le sprint (un autre est peut-être déjà actif)');
                                                    }
                                                }
                                            }}
                                            className="mt-2 text-xs bg-green-600 text-white px-3 py-1 rounded-lg font-bold hover:bg-green-700 shadow-sm"
                                        >
                                            ▶ Démarrer
                                        </button>
                                    )}
                                </div>
                            </div>

                            <Droppable droppableId={String(sprint.id)}>
                                {(provided, snapshot) => (
                                    <div
                                        ref={provided.innerRef}
                                        {...provided.droppableProps}
                                        className={`p-3 min-h-[100px] transition-colors ${snapshot.isDraggingOver ? 'bg-green-50/50' : ''}`}
                                    >
                                        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                            {(sprintTasks[sprint.id] || []).map((story, index) => (
                                                <Draggable key={story.id} draggableId={String(story.id)} index={index}>
                                                    {(provided) => (
                                                        <div
                                                            ref={provided.innerRef}
                                                            {...provided.draggableProps}
                                                            {...provided.dragHandleProps}
                                                        >
                                                            <StoryCard story={story} />
                                                        </div>
                                                    )}
                                                </Draggable>
                                            ))}
                                        </div>
                                        {provided.placeholder}
                                        {(sprintTasks[sprint.id] || []).length === 0 && !snapshot.isDraggingOver && (
                                            <div className="text-center py-6 text-slate-300 text-sm border-2 border-dashed border-slate-100 rounded-lg">
                                                Glissez des stories ici pour planifier
                                            </div>
                                        )}
                                    </div>
                                )}
                            </Droppable>
                        </div>
                    ))}
                </div>
            </DragDropContext>

            <CreateSprintModal
                projectId={projectId}
                isOpen={isCreateSprintOpen}
                onClose={() => setIsCreateSprintOpen(false)}
                onSuccess={loadData}
            />
        </div>
    );
};

export default SprintPlanningBoard;
