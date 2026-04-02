import React from 'react';

interface AuthTabsProps {
  isLogin: boolean;
  onTabChange: (isLogin: boolean) => void;
}

const AuthTabs: React.FC<AuthTabsProps> = ({ isLogin, onTabChange }) => {
  return (
    <div className="mb-8 flex gap-2 rounded-lg bg-[#f3f7ff] p-1.5">
      <button
        onClick={() => onTabChange(true)}
        className={`flex-1 py-2.5 px-4 rounded-md font-semibold text-sm transition-all ${
          isLogin
            ? 'bg-white text-[#5d87ff] shadow-[0_2px_10px_rgba(93,135,255,0.2)]'
            : 'text-[#5a6a85] hover:text-[#2a3547]'
        }`}
      >
        Iniciar Sesión
      </button>
      <button
        onClick={() => onTabChange(false)}
        className={`flex-1 py-2.5 px-4 rounded-md font-semibold text-sm transition-all ${
          !isLogin
            ? 'bg-white text-[#5d87ff] shadow-[0_2px_10px_rgba(93,135,255,0.2)]'
            : 'text-[#5a6a85] hover:text-[#2a3547]'
        }`}
      >
        Crear Cuenta
      </button>
    </div>
  );
};

export default AuthTabs;
