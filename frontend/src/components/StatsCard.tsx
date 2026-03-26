import React from 'react';

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
  backgroundColor = 'bg-blue-100',
  trend,
}) => {
  return (
    <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm hover:shadow-md transition-shadow">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-gray-600 mb-1">{label}</p>
          <div className="flex items-baseline gap-2">
            <p className="text-3xl font-bold text-gray-900">{value}</p>
            {trend && (
              <span className={`text-sm font-semibold ${trend.isPositive ? 'text-green-600' : 'text-red-600'}`}>
                {trend.isPositive ? '↑' : '↓'} {Math.abs(trend.value)}%
              </span>
            )}
          </div>
        </div>
        <div className={`flex h-14 w-14 items-center justify-center rounded-lg ${backgroundColor} text-2xl shadow-md`}>
          {emoji}
        </div>
      </div>
    </div>
  );
};

export default StatsCard;
