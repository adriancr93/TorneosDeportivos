import React from 'react';
import logoLogin from '../assets/branding/logo-login.png';

interface AuthLayoutProps {
  children: React.ReactNode;
}

const AuthLayout: React.FC<AuthLayoutProps> = ({ children }) => {
  return (
    <div className="relative flex min-h-screen flex-col items-center justify-center overflow-hidden bg-[#eef3ff] px-4 py-8">
      <div className="pointer-events-none absolute -left-20 -top-48 h-[620px] w-[620px] rounded-full border-[120px] border-[#dfe8ff]" />
      <div className="pointer-events-none absolute -bottom-24 -right-10 h-[320px] w-[320px] rounded-full bg-[#dfe8ff]/70" />

      <div className="relative z-10 flex w-full max-w-sm flex-col items-center">
        <div className="mb-5 flex justify-center">
          <img
            src={logoLogin}
            alt="Football Dashboard"
            className="h-auto w-72 max-w-full"
            style={{ filter: 'drop-shadow(0 12px 24px rgba(93,135,255,0.24))' }}
          />
        </div>

        <div className="w-full rounded-2xl border border-[#dfe5ef] bg-white px-7 py-8 shadow-[0_8px_24px_rgba(133,146,173,0.2)] sm:px-8">
          {children}
        </div>
      </div>
    </div>
  );
};

export default AuthLayout;
