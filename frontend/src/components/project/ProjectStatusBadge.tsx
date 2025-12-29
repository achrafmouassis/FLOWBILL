import React from 'react';

interface Props {
    status: string;
}

const ProjectStatusBadge: React.FC<Props> = ({ status }) => {
    const getStyles = () => {
        switch (status) {
            case 'ACTIVE':
                return 'bg-green-100 text-green-800 border-green-200';
            case 'PLANNED':
                return 'bg-blue-100 text-blue-800 border-blue-200';
            case 'ON_HOLD':
                return 'bg-orange-100 text-orange-800 border-orange-200';
            case 'COMPLETED':
                return 'bg-gray-100 text-gray-800 border-gray-200';
            case 'ARCHIVED':
                return 'bg-gray-200 text-gray-600 border-gray-300';
            default:
                return 'bg-slate-100 text-slate-800 border-slate-200';
        }
    };

    const getLabel = () => {
        switch (status) {
            case 'ACTIVE': return 'Actif';
            case 'PLANNED': return 'Planifié';
            case 'ON_HOLD': return 'En Pause';
            case 'COMPLETED': return 'Terminé';
            case 'ARCHIVED': return 'Archivé';
            default: return status;
        }
    };

    return (
        <span className={`px-2 py-1 rounded-full text-xs font-semibold border ${getStyles()}`}>
            {getLabel()}
        </span>
    );
};

export default ProjectStatusBadge;
