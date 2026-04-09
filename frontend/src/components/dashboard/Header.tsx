import React from 'react';
import logoHeader from '../../assets/branding/logo-header.png';

interface HeaderProps {
  username: string;
  pageTitle: string;
  onMenuClick: () => void;
  onLogout: () => void;
}

const Header: React.FC<HeaderProps> = ({ username, pageTitle, onMenuClick, onLogout }) => {
  return (
    <header className="sticky top-0 z-20 border-b border-[#e8eef8] bg-white px-4 py-4 sm:px-6">
      <div className="flex items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <button
            onClick={onMenuClick}
            className="flex h-10 w-10 items-center justify-center rounded-xl border border-[#d9e3f3] text-[#2a3547] transition-colors hover:bg-[#f3f7ff] lg:hidden"
          >
            ☰
          </button>
          <img
            src={logoHeader}
            alt="Football Dashboard"
            className="hidden h-9 w-auto opacity-95 xl:block"
          />
          <div className="hidden xl:block">
            <p className="text-sm font-semibold text-[#2a3547]">{pageTitle}</p>
          </div>
        </div>

        <div className="hidden max-w-md flex-1 xl:flex">
          <div className="w-full rounded-lg border border-[#dfe5ef] bg-[#f8fbff] px-3 py-2 text-sm text-[#7a8ca8]">
            Buscar modulo...
          </div>
        </div>

        <div className="flex items-center gap-3">
          <div className="text-right hidden sm:block">
            <p className="text-sm font-semibold text-[#2a3547]">{username}</p>
            <p className="text-xs text-[#7a8ca8]">Administrador</p>
          </div>
            <div className="flex h-10 w-10 items-center justify-center rounded-full border border-[#d6e1f8] bg-[linear-gradient(180deg,#5d87ff_0%,#3f67d8_100%)] text-sm font-bold text-white shadow-[0_10px_20px_rgba(93,135,255,0.35)]">
              {username.charAt(0).toUpperCase()}
            </div>

          <button onClick={onLogout} className="rounded-lg border border-[#e9d8db] bg-[#fff3f4] px-3 py-2 text-xs font-semibold text-[#b94958] transition-colors hover:bg-[#ffecee]">
            Salir
          </button>
        </div>
      </div>
    </header>
  );
};

export default Header;
