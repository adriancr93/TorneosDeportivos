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
    <div className="flex h-screen overflow-hidden bg-gray-50">
      {/* Sidebar */}
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />

      {/* Main Content */}
      <main className="flex min-w-0 flex-1 flex-col overflow-hidden">
        {/* Header */}
        <Header
          username={usuario?.username || 'Usuario'}
          pageTitle={pageTitle}
          onMenuClick={() => setSidebarOpen(true)}
          onLogout={handleLogout}
        />

        {/* Page Content */}
        <section className="min-h-0 flex-1 overflow-auto">
          <div className="px-6 py-6">
            <Outlet />
          </div>
        </section>
      </main>
    </div>
  );
};

export default DashboardLayout;
