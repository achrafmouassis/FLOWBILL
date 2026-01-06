import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ChangePasswordModal from '../components/ChangePasswordModal';
import { jwtDecode } from 'jwt-decode';
import { MyTasksList } from '../components/dashboard/MyTasksList';
import { MySprintsList } from '../components/dashboard/MySprintsList';
import { MyProjectsList } from '../components/dashboard/MyProjectsList';
import { LogOut, Key, User as UserIcon, LayoutDashboard, Briefcase, Layers, CheckSquare } from 'lucide-react';

const UserDashboard = () => {
    const [isPasswordModalOpen, setIsPasswordModalOpen] = useState(false);
    const navigate = useNavigate();

    const token = localStorage.getItem('token');
    const decoded: any = token ? jwtDecode(token) : {};
    const email = decoded.sub || '';
    const fullName = decoded.fullName || 'Développeur';

    const doLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    }

    return (
        <div className="min-h-screen bg-slate-50 flex flex-col">
            {/* Navigation Glassmorphism */}
            <nav className="sticky top-0 z-50 bg-white/80 backdrop-blur-md border-b border-slate-200">
                <div className="max-w-7xl mx-auto px-6 py-4 flex justify-between items-center">
                    <div className="flex items-center gap-8">
                        <div className="flex items-center gap-2">
                            <div className="w-10 h-10 bg-blue-600 rounded-xl flex items-center justify-center shadow-lg shadow-blue-200">
                                <span className="text-white font-black text-xl italic">F</span>
                            </div>
                            <span className="text-2xl font-black text-slate-800 tracking-tighter uppercase">FLOWBILL</span>
                        </div>
                        <div className="hidden md:flex items-center gap-2 px-3 py-1 bg-blue-50 text-blue-700 rounded-full text-xs font-bold uppercase tracking-widest border border-blue-100">
                            <LayoutDashboard size={14} />
                            Developer Portal
                        </div>
                    </div>

                    <div className="flex items-center gap-6">
                        <div className="hidden sm:flex flex-col items-end mr-2">
                            <span className="text-sm font-bold text-slate-800">{fullName}</span>
                            <span className="text-xs text-slate-400 font-medium">{email}</span>
                        </div>
                        <div className="flex items-center gap-2 border-l pl-6 border-slate-100">
                            <button
                                onClick={() => setIsPasswordModalOpen(true)}
                                className="p-2 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-xl transition-all"
                                title="Changer le mot de passe"
                            >
                                <Key size={20} />
                            </button>
                            <button
                                onClick={doLogout}
                                className="p-2 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-xl transition-all"
                                title="Déconnexion"
                            >
                                <LogOut size={20} />
                            </button>
                        </div>
                    </div>
                </div>
            </nav>

            <main className="flex-1 max-w-7xl mx-auto px-6 py-10 w-full space-y-12">
                {/* Welcome Hero */}
                <div className="relative overflow-hidden bg-white p-8 rounded-[2.5rem] border border-slate-200 shadow-sm">
                    <div className="relative z-10">
                        <h2 className="text-3xl font-black text-slate-900 mb-2">Bienvenue, {fullName.split(' ')[0]} ! 👋</h2>
                        <p className="text-slate-500 font-medium">Voici un aperçu de vos projets et tâches en cours.</p>
                    </div>
                    <div className="absolute top-0 right-0 p-8 text-slate-50 opacity-10 pointer-events-none">
                        <UserIcon size={120} />
                    </div>
                </div>

                {/* Projects Section */}
                <section>
                    <div className="flex items-center gap-3 mb-6">
                        <div className="p-2 bg-indigo-50 text-indigo-600 rounded-lg">
                            <Briefcase size={20} />
                        </div>
                        <h3 className="text-xl font-black text-slate-800 uppercase tracking-tight">Mes Projets</h3>
                    </div>
                    <MyProjectsList />
                </section>

                <div className="grid grid-cols-1 lg:grid-cols-1 gap-12">
                    {/* Sprints Section */}
                    <section>
                        <div className="flex items-center gap-3 mb-6">
                            <div className="p-2 bg-purple-50 text-purple-600 rounded-lg">
                                <Layers size={20} />
                            </div>
                            <h3 className="text-xl font-black text-slate-800 uppercase tracking-tight">Sprints Actifs</h3>
                        </div>
                        <MySprintsList />
                    </section>

                    {/* Tasks Section */}
                    <section>
                        <div className="flex items-center justify-between mb-6">
                            <div className="flex items-center gap-3">
                                <div className="p-2 bg-emerald-50 text-emerald-600 rounded-lg">
                                    <CheckSquare size={20} />
                                </div>
                                <h3 className="text-xl font-black text-slate-800 uppercase tracking-tight">Mes Tâches</h3>
                            </div>
                        </div>
                        <MyTasksList />
                    </section>
                </div>
            </main>

            <ChangePasswordModal
                isOpen={isPasswordModalOpen}
                onClose={() => setIsPasswordModalOpen(false)}
                email={email}
            />
        </div>
    );
};

export default UserDashboard;
