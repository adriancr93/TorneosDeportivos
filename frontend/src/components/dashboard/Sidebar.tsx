import React from 'react';
import { NavLink } from 'react-router-dom';
import logoLogin from '../../assets/branding/logo-login.png';
import rocketImage from '../../assets/dashboard/rocket.png';

interface SidebarProps {
  isOpen: boolean;
  onClose: () => void;
}

const Sidebar: React.FC<SidebarProps> = ({ isOpen, onClose }) => {
  const iconClassName = 'h-[18px] w-[18px] stroke-[1.8]';

  const menuItems = [
    {
      label: 'Torneos',
      path: '/dashboard/torneos',
      icon: (
        <svg viewBox="0 0 24 24" fill="none" className={iconClassName}>
          <path d="M7 5h10v3a4 4 0 0 1-4 4h-2a4 4 0 0 1-4-4V5z" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" />
          <path d="M8 19h8M12 12v7" stroke="currentColor" strokeLinecap="round" />
        </svg>
      ),
    },
    {
      label: 'Equipos',
      path: '/dashboard/equipos',
      icon: (
        <svg viewBox="0 0 24 24" fill="none" className={iconClassName}>
          <path d="M12 3l7 3v5c0 5-3.5 8-7 10-3.5-2-7-5-7-10V6l7-3z" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" />
          <path d="M9.5 11.5l2 2 3-3" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
      ),
    },
    {
      label: 'Jugadores',
      path: '/dashboard/jugadores',
      icon: (
        <svg viewBox="0 0 24 24" fill="none" className={iconClassName}>
          <circle cx="12" cy="8" r="3" stroke="currentColor" />
          <path d="M5 20a7 7 0 0 1 14 0" stroke="currentColor" strokeLinecap="round" />
        </svg>
      ),
    },
    {
      label: 'Partidos',
      path: '/dashboard/partidos',
      icon: (
        <svg viewBox="0 0 24 24" fill="none" className={iconClassName}>
          <circle cx="12" cy="12" r="8" stroke="currentColor" />
          <path d="M8.5 9.5l3.5-2 3.5 2v4l-3.5 2-3.5-2v-4z" stroke="currentColor" strokeLinejoin="round" />
        </svg>
      ),
    },
    {
      label: 'Estadísticas',
      path: '/dashboard/estadisticas',
      icon: (
        <svg viewBox="0 0 24 24" fill="none" className={iconClassName}>
          <path d="M5 19V11M12 19V7M19 19V14" stroke="currentColor" strokeLinecap="round" />
          <path d="M4 19h16" stroke="currentColor" strokeLinecap="round" />
        </svg>
      ),
    },
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
        className={`fixed left-0 top-0 z-40 h-screen w-72 transform transition-transform duration-300 lg:relative lg:z-0 lg:translate-x-0 ${
          isOpen ? 'translate-x-0' : '-translate-x-full'
        } overflow-y-auto border-r border-[#dfe5ef] bg-white text-[#2a3547]`}
      >
        <div className="sticky top-0 border-b border-[#eef2f8] bg-white px-6 py-5">
          <div className="flex items-start gap-3">
            <img src={logoLogin} alt="Football Dashboard" className="w-16 shrink-0" />
            <div className="pt-2">
              <p className="text-[10px] font-bold uppercase tracking-[0.3em] text-[#5d87ff]">Torneos</p>
              <h1 className="mt-1 text-base font-bold text-[#2a3547]">Admin Dashboard</h1>
            </div>
            <button
              onClick={onClose}
              className="ml-auto text-2xl text-[#5a6a85] hover:text-[#2a3547] lg:hidden"
            >
              ✕
            </button>
          </div>
        </div>

        <div className="mx-4 mt-4 rounded-lg bg-[#eef3ff] p-4">
          <div className="flex items-center gap-3">
            <img src={rocketImage} alt="Promo" className="h-12 w-12 shrink-0" />
            <div>
              <p className="text-xs font-semibold text-[#2a3547]">Panel listo</p>
              <p className="mt-1 text-xs text-[#5a6a85]">Gestiona todas las categorías</p>
            </div>
          </div>
        </div>

        <div className="px-6 pt-6 pb-2 text-[11px] font-bold uppercase tracking-[0.2em] text-[#7a8ca8]">Menu</div>

        <nav className="flex flex-col gap-1 px-4 pb-6">
          {menuItems.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              onClick={onClose}
              className={({ isActive }) =>
                `group rounded-lg border px-4 py-2.5 transition-all ${
                  isActive
                    ? 'border-[#d6e2ff] bg-[#eef3ff] text-[#2a3547]'
                    : 'border-transparent text-[#5a6a85] hover:border-[#e8eef8] hover:bg-[#f8fbff] hover:text-[#2a3547]'
                }`
              }
            >
              <div className="flex items-center gap-3">
                <span className="flex h-8 w-8 items-center justify-center rounded-md bg-[#f4f7fd] text-[#5d87ff]">{item.icon}</span>
                <div>
                  <p className="text-sm font-semibold">{item.label}</p>
                </div>
              </div>
            </NavLink>
          ))}
        </nav>

        <div className="mt-auto border-t border-[#eef2f8] px-5 py-4 text-xs text-[#7a8ca8]">
          Torneos v2.0 • Panel administrativo
        </div>
      </aside>
    </>
  );
};

export default Sidebar;
