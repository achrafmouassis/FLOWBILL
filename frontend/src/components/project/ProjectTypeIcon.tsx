import React from 'react';
import { Globe, Smartphone, Server, Layers, Settings, HelpCircle } from 'lucide-react';

interface Props {
    type?: string;
    size?: number;
}

const ProjectTypeIcon: React.FC<Props> = ({ type, size = 20 }) => {
    const getIcon = () => {
        switch (type?.toUpperCase()) {
            case 'WEB':
                return <Globe size={size} className="text-blue-500" />;
            case 'MOBILE':
                return <Smartphone size={size} className="text-green-500" />;
            case 'API':
                return <Server size={size} className="text-orange-500" />;
            case 'FULLSTACK':
                return <Layers size={size} className="text-purple-500" />;
            case 'MAINTENANCE':
                return <Settings size={size} className="text-gray-500" />;
            default:
                return <HelpCircle size={size} className="text-slate-400" />;
        }
    };

    return (
        <div className="inline-flex items-center justify-center p-1.5 bg-slate-50 rounded-lg">
            {getIcon()}
        </div>
    );
};

export default ProjectTypeIcon;
