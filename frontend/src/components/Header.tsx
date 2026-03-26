import React from 'react';

interface HeaderProps {
  username: string;
  pageTitle: string;
  onMenuClick: () => void;
  onLogout: () => void;
}

const Header: React.FC<HeaderProps> = ({ username, pageTitle, onMenuClick, onLogout }) => {
  return (
    <header className="border-b border-gray-200 bg-white px-6 py-4 shadow-sm sticky top-0 z-20">
      <div className="flex items-center justify-between gap-4">
        {/* Left Section */}
        <div className="flex items-center gap-4">
          <button
            onClick={onMenuClick}
            className="lg:hidden flex items-center justify-center w-10 h-10 rounded-lg hover:bg-gray-100 text-gray-600 transition-colors"
          >
            ☰
          </button>
          <div>
            <h2 className="text-2xl font-bold text-gray-900">{pageTitle}</h2>
            <p className="text-xs text-gray-500 mt-0.5">Panel de administración</p>
          </div>
        </div>

        {/* Right Section */}
        <div className="flex items-center gap-6">
          {/* Quick Stats */}
          <div className="hidden md:flex items-center gap-6 pr-6 border-r border-gray-200">
            <div className="text-right">
              <p className="text-xs text-gray-600">Usuario</p>
              <p className="text-sm font-semibold text-gray-900">{username}</p>
            </div>
          </div>

          {/* User Profile */}
          <div className="flex items-center gap-4">
            <div className="text-right hidden sm:block">
              <p className="text-sm font-semibold text-gray-900">{username}</p>
              <p className="text-xs text-gray-500">Administrador</p>
            </div>
            <div className="flex h-10 w-10 items-center justify-center rounded-full bg-gradient-to-br from-green-400 to-green-600 text-sm font-bold text-white shadow-md">
              {username.charAt(0).toUpperCase()}
            </div>
          </div>

          {/* Logout Button */}
          <button
            onClick={onLogout}
            className="hidden sm:flex items-center gap-2 px-4 py-2 rounded-lg bg-red-50 text-red-600 hover:bg-red-100 text-sm font-medium transition-colors"
          >
            <span>🚪</span>
            Salir
          </button>
        </div>
      </div>
    </header>
  );
};

export default Header;
