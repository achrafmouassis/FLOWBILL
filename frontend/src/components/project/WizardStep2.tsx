import React, { useEffect, useState } from 'react';
import api from '../../api/api';
import { jwtDecode } from 'jwt-decode';
import type { CreateProjectRequest, User, ProjectMember } from '../../types';
import { Search, Plus, Trash2, User as UserIcon } from 'lucide-react';

interface Props {
    data: CreateProjectRequest;
    onChange: (data: Partial<CreateProjectRequest>) => void;
}

const WizardStep2: React.FC<Props> = ({ data, onChange }) => {
    const [availableUsers, setAvailableUsers] = useState<User[]>([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [loading, setLoading] = useState(true);

    const token = localStorage.getItem('token');
    const decoded: any = token ? jwtDecode(token) : {};
    const tenantId = decoded.tenantId || decoded.tenant;

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const response = await api.get(`/auth/users/${tenantId}`);
                // Filter only developers (ROLE_USER in this system)
                const devs = response.data.filter((u: any) =>
                    u.roles && u.roles.some((r: any) => r.name === 'ROLE_USER')
                );
                setAvailableUsers(devs);
            } catch (err) {
                console.error("Error fetching users", err);
            } finally {
                setLoading(false);
            }
        };
        fetchUsers();
    }, [tenantId]);

    const addMember = (user: User) => {
        if (data.teamMembers.some(m => m.userId === user.id)) return;

        const newMember: Partial<ProjectMember> = {
            userId: user.id,
            fullName: user.fullName,
            role: 'Développeur',
            capacityHours: 40
        };

        onChange({ teamMembers: [...data.teamMembers, newMember] });
    };

    const removeMember = (userId: number) => {
        onChange({ teamMembers: data.teamMembers.filter(m => m.userId !== userId) });
    };

    const updateMember = (userId: number, updates: Partial<ProjectMember>) => {
        onChange({
            teamMembers: data.teamMembers.map(m =>
                m.userId === userId ? { ...m, ...updates } : m
            )
        });
    };

    const filteredUsers = availableUsers.filter(u =>
        u.fullName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        u.email.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 h-full">
            {/* User Selection */}
            <div className="flex flex-col h-full border-r border-slate-100 pr-4">
                <h3 className="text-lg font-bold text-slate-900 mb-4">Membres disponibles</h3>
                <div className="relative mb-4">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={16} />
                    <input
                        type="text"
                        placeholder="Rechercher..."
                        className="w-full pl-10 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-500/20 text-sm"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>

                <div className="flex-1 overflow-y-auto space-y-2 max-h-[400px]">
                    {loading ? <p className="text-center py-10 text-slate-400">Chargement...</p> :
                        filteredUsers.map(user => (
                            <div key={user.id} className="flex justify-between items-center p-3 bg-white border border-slate-100 rounded-xl hover:border-blue-200 hover:shadow-sm transition-all group">
                                <div className="flex items-center gap-3">
                                    <div className="w-8 h-8 rounded-full bg-slate-100 flex items-center justify-center text-slate-400">
                                        <UserIcon size={14} />
                                    </div>
                                    <div>
                                        <p className="text-sm font-bold text-slate-700">{user.fullName}</p>
                                        <p className="text-[11px] text-slate-400">{user.email}</p>
                                    </div>
                                </div>
                                <button
                                    onClick={() => addMember(user)}
                                    className={`p-1.5 rounded-lg transition-all ${data.teamMembers.some(m => m.userId === user.id)
                                            ? 'bg-green-100 text-green-600'
                                            : 'bg-blue-50 text-blue-600 hover:bg-blue-100 opacity-0 group-hover:opacity-100'
                                        }`}
                                >
                                    <Plus size={16} />
                                </button>
                            </div>
                        ))}
                </div>
            </div>

            {/* Configured Team */}
            <div className="flex flex-col">
                <h3 className="text-lg font-bold text-slate-900 mb-4">Équipe du projet ({data.teamMembers.length})</h3>
                <div className="space-y-4 overflow-y-auto max-h-[450px]">
                    {data.teamMembers.length === 0 ? (
                        <div className="py-20 text-center border-2 border-dashed border-slate-100 rounded-2xl">
                            <p className="text-slate-400 italic text-sm">Sélectionnez des membres à gauche pour commencer</p>
                        </div>
                    ) : (
                        data.teamMembers.map(member => (
                            <div key={member.userId} className="p-4 bg-slate-50 border border-slate-200 rounded-2xl space-y-3">
                                <div className="flex justify-between items-center">
                                    <span className="font-bold text-slate-800">{member.fullName}</span>
                                    <button
                                        onClick={() => removeMember(member.userId!)}
                                        className="text-slate-400 hover:text-red-500 transition-colors"
                                    >
                                        <Trash2 size={16} />
                                    </button>
                                </div>
                                <div className="grid grid-cols-2 gap-4">
                                    <div className="space-y-1">
                                        <label className="text-[10px] uppercase font-bold text-slate-400">Rôle</label>
                                        <select
                                            className="w-full bg-white border border-slate-200 rounded-lg py-1 px-2 text-xs outline-none focus:ring-1 focus:ring-blue-500"
                                            value={member.role}
                                            onChange={(e) => updateMember(member.userId!, { role: e.target.value })}
                                        >
                                            <option>Développeur</option>
                                            <option>Fullstack</option>
                                            <option>Backend</option>
                                            <option>Frontend</option>
                                            <option>Tech Lead</option>
                                            <option>Designer</option>
                                        </select>
                                    </div>
                                    <div className="space-y-1">
                                        <label className="text-[10px] uppercase font-bold text-slate-400">Capacité (h/sem)</label>
                                        <input
                                            type="number"
                                            className="w-full bg-white border border-slate-200 rounded-lg py-1 px-2 text-xs outline-none focus:ring-1 focus:ring-blue-500"
                                            value={member.capacityHours}
                                            onChange={(e) => updateMember(member.userId!, { capacityHours: parseInt(e.target.value) || 0 })}
                                        />
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
};

export default WizardStep2;
