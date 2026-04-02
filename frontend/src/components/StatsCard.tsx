import React from 'react';
import rocketImage from '../assets/dashboard/rocket.png';

interface StatsCardProps {
  label: string;
  value: string | number;
  emoji: string;
  backgroundColor?: string;
  trend?: {
    value: number;
    isPositive: boolean;
  };
}

const StatsCard: React.FC<StatsCardProps> = ({
  label,
  value,
  emoji,
  backgroundColor = 'bg-emerald-500/15',
  trend,
}) => {
  return (
    <div className="relative overflow-hidden rounded-3xl border border-[#dfe5ef] bg-white p-5 shadow-[0_8px_24px_rgba(133,146,173,0.14)] transition-all hover:-translate-y-0.5 hover:shadow-[0_12px_28px_rgba(133,146,173,0.2)]">
      <img
        src={rocketImage}
        alt="Decoración"
        className="pointer-events-none absolute -right-6 -top-8 h-20 w-20 opacity-10"
      />
      <div className="flex items-center justify-between">
        <div>
          <p className="mb-1 text-xs font-semibold uppercase tracking-[0.2em] text-[#7a8ca8]">{label}</p>
          <div className="flex items-baseline gap-2">
            <p className="text-3xl font-bold text-[#2a3547] md:text-4xl">{value}</p>
            {trend && (
              <span className={`text-sm font-semibold ${trend.isPositive ? 'text-[#13deb9]' : 'text-[#ef4444]'}`}>
                {trend.isPositive ? '↑' : '↓'} {Math.abs(trend.value)}%
              </span>
            )}
          </div>
        </div>
        <div className={`flex h-14 w-14 items-center justify-center rounded-2xl border border-[#edf1f7] ${backgroundColor} text-2xl`}>
          {emoji}
        </div>
      </div>
    </div>
  );
};

export default StatsCard;
