import React, { useEffect, useMemo, useState } from 'react';
import { useNavigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../../lib/authContext';
import Sidebar from '../dashboard/Sidebar';
import Header from '../dashboard/Header';

type DashboardTheme = 'light' | 'dark';

const DashboardLayout: React.FC = () => {
  const { usuario, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [theme, setTheme] = useState<DashboardTheme>(() => {
    if (typeof window === 'undefined') {
      return 'light';
    }

    const storedTheme = window.localStorage.getItem('dashboard-theme');
    if (storedTheme === 'light' || storedTheme === 'dark') {
      return storedTheme;
    }

    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  });

  useEffect(() => {
    document.documentElement.classList.toggle('dark', theme === 'dark');
    document.documentElement.style.colorScheme = theme;
    window.localStorage.setItem('dashboard-theme', theme);
  }, [theme]);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handleToggleTheme = () => {
    setTheme((currentTheme) => currentTheme === 'dark' ? 'light' : 'dark');
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
    <div className="flex min-h-screen w-full bg-[#f5f7fb] transition-colors dark:bg-slate-950">
      <div className="page-wrapper flex w-full">
        <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />

        <main className="body-wrapper w-full min-w-0 bg-[#f5f7fb] transition-colors dark:bg-slate-950">
          <Header
            username={usuario?.username || 'Usuario'}
            pageTitle={pageTitle}
            theme={theme}
            onMenuClick={() => setSidebarOpen(true)}
            onLogout={handleLogout}
            onToggleTheme={handleToggleTheme}
          />

          <section className="dashboard-canvas min-h-[calc(100vh-76px)] p-4 sm:p-6">
            <div className="mx-auto max-w-360 rounded-xl border border-[#dfe5ef] bg-white p-4 shadow-[0_10px_30px_rgba(133,146,173,0.14)] transition-colors dark:border-slate-800 dark:bg-slate-900 dark:shadow-[0_18px_40px_rgba(2,6,23,0.45)] sm:p-6">
              <Outlet />
            </div>
          </section>
        </main>
      </div>
    </div>
  );
};

export default DashboardLayout;
