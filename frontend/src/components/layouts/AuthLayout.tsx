import React from 'react';
import Logo from '../assets/Logo';

interface AuthLayoutProps {
  children: React.ReactNode;
}

const AuthLayout: React.FC<AuthLayoutProps> = ({ children }) => {
  return (
    <div className="bg-gray-800 min-h-screen">
        <div className="py-10 lg:py-20 mx-auto w-[450px]">
           <Logo/>
          <div className="mt-10">
            {children}
          </div>
        </div>
    </div>
  );
};

export default AuthLayout;
