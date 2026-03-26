import React from 'react';

interface AuthTabsProps {
  isLogin: boolean;
  onTabChange: (isLogin: boolean) => void;
}

const AuthTabs: React.FC<AuthTabsProps> = ({ isLogin, onTabChange }) => {
  return (
    <div className="mb-8 flex gap-2 bg-gray-100 rounded-lg p-1.5">
      <button
        onClick={() => onTabChange(true)}
        className={`flex-1 py-2.5 px-4 rounded-md font-semibold text-sm transition-all ${
          isLogin
            ? 'bg-white text-green-600 shadow-md'
            : 'text-gray-600 hover:text-gray-900'
        }`}
      >
        Iniciar Sesión
      </button>
      <button
        onClick={() => onTabChange(false)}
        className={`flex-1 py-2.5 px-4 rounded-md font-semibold text-sm transition-all ${
          !isLogin
            ? 'bg-white text-green-600 shadow-md'
            : 'text-gray-600 hover:text-gray-900'
        }`}
      >
        Crear Cuenta
      </button>
    </div>
  );
};

export default AuthTabs;
