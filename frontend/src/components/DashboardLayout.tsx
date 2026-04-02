import React, { useMemo, useState } from 'react';
import { useNavigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../lib/authContext';
import Sidebar from './Sidebar';
import Header from './Header';

const DashboardLayout: React.FC = () => {
  const { usuario, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const menuItems = [
    { label: 'Jugadores', path: '/dashboard/jugadores' },
    { label: 'Torneos', path: '/dashboard/torneos' },
    { label: 'Equipos', path: '/dashboard/equipos' },
    { label: 'Partidos', path: '/dashboard/partidos' },
    { label: 'Estadísticas', path: '/dashboard/estadisticas' },
  ];

  const pageTitle = useMemo(() => {
    const current = menuItems.find((item) => location.pathname.startsWith(item.path));
    return current ? current.label : 'Panel';
  }, [location.pathname]);

  return (
    <div className="flex w-full min-h-screen bg-[#f5f7fb]">
      <div className="page-wrapper flex w-full">
        <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />

        <main className="body-wrapper w-full min-w-0 bg-[#f5f7fb]">
          <Header
            username={usuario?.username || 'Usuario'}
            pageTitle={pageTitle}
            onMenuClick={() => setSidebarOpen(true)}
            onLogout={handleLogout}
          />

          <section className="dashboard-canvas min-h-[calc(100vh-76px)] p-4 sm:p-6">
            <div className="mx-auto max-w-[1440px] rounded-xl border border-[#dfe5ef] bg-white p-4 shadow-[0_10px_30px_rgba(133,146,173,0.14)] sm:p-6">
              <Outlet />
            </div>
          </section>
        </main>
      </div>
    </div>
  );
};

export default DashboardLayout;
