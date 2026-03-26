import React from 'react';
import { NavLink } from 'react-router-dom';

interface SidebarProps {
  isOpen: boolean;
  onClose: () => void;
}

const Sidebar: React.FC<SidebarProps> = ({ isOpen, onClose }) => {
  const menuItems = [
    { label: 'Torneos', path: '/dashboard/torneos', icon: '🏆' },
    { label: 'Equipos', path: '/dashboard/equipos', icon: '👥' },
    { label: 'Partidos', path: '/dashboard/partidos', icon: '⚽' },
    { label: 'Estadísticas', path: '/dashboard/estadisticas', icon: '📊' },
  ];

  return (
    <>
      {/* Mobile Overlay */}
      {isOpen && (
        <button
          onClick={onClose}
          className="fixed inset-0 z-30 bg-black/50 lg:hidden"
          aria-label="Cerrar menú"
        />
      )}

      {/* Sidebar */}
      <aside
        className={`fixed left-0 top-0 z-40 h-screen w-64 transform transition-transform duration-300 lg:relative lg:z-0 lg:translate-x-0 ${
          isOpen ? 'translate-x-0' : '-translate-x-full'
        } bg-gradient-to-b from-slate-900 to-slate-800 text-white overflow-y-auto`}
      >
        {/* Logo Section */}
        <div className="border-b border-slate-700 px-6 py-8 sticky top-0 bg-slate-900">
          <div className="flex items-center gap-3">
            <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-gradient-to-br from-green-400 to-green-600 font-bold text-2xl shadow-lg">
              ⚽
            </div>
            <div>
              <h1 className="text-xl font-bold">Torneos</h1>
              <p className="text-xs text-slate-400">Dashboard</p>
            </div>
            <button
              onClick={onClose}
              className="ml-auto lg:hidden text-slate-400 hover:text-white text-2xl"
            >
              ✕
            </button>
          </div>
        </div>

        {/* Navigation */}
        <nav className="flex flex-col gap-2 px-4 py-8">
          {menuItems.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              onClick={onClose}
              className={({ isActive }) =>
                `flex items-center gap-3 rounded-lg px-4 py-3 text-sm font-medium transition-all ${
                  isActive
                    ? 'bg-gradient-to-r from-green-500 to-green-600 text-white shadow-lg'
                    : 'text-slate-300 hover:bg-slate-700 hover:text-white'
                }`
              }
            >
              <span className="text-lg">{item.icon}</span>
              <span>{item.label}</span>
            </NavLink>
          ))}
        </nav>

        {/* Footer Info */}
        <div className="absolute bottom-0 left-0 right-0 border-t border-slate-700 bg-slate-900 px-6 py-4">
          <p className="text-xs text-slate-500 text-center">
            Torneos v1.0
          </p>
        </div>
      </aside>
    </>
  );
};

export default Sidebar;
