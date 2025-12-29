import React from 'react';

interface Props {
    membersCount: number;
    max?: number;
}

const TeamAvatarStack: React.FC<Props> = ({ membersCount, max = 5 }) => {
    // For MVP, we'll just show placeholders based on count
    const displayCount = Math.min(membersCount, max);
    const extra = membersCount > max ? membersCount - max : 0;

    return (
        <div className="flex -space-x-2 overflow-hidden px-1">
            {[...Array(displayCount)].map((_, i) => (
                <div
                    key={i}
                    className="inline-block h-8 w-8 rounded-full ring-2 ring-white bg-slate-200 flex items-center justify-center text-[10px] font-bold text-slate-500"
                >
                    {/* Placeholder Initials */}
                    {String.fromCharCode(65 + i)}
                </div>
            ))}
            {extra > 0 && (
                <div className="inline-block h-8 w-8 rounded-full ring-2 ring-white bg-slate-400 flex items-center justify-center text-[10px] font-bold text-white">
                    +{extra}
                </div>
            )}
        </div>
    );
};

export default TeamAvatarStack;
