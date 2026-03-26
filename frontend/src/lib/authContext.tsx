import React, { createContext, useContext, useState, useEffect } from 'react';
import type { Usuario } from '../types';

interface AuthContextType {
  usuario: Usuario | null;
  isLoading: boolean;
  login: (usuario: Usuario) => void;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [usuario, setUsuario] = useState<Usuario | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Check if user is in localStorage
    const storedUser = localStorage.getItem('usuario');
    if (storedUser) {
      try {
        setUsuario(JSON.parse(storedUser));
      } catch (error) {
        console.error('Error parsing stored usuario:', error);
        localStorage.removeItem('usuario');
      }
    }
    setIsLoading(false);
  }, []);

  const login = (user: Usuario) => {
    setUsuario(user);
    localStorage.setItem('usuario', JSON.stringify(user));
  };

  const logout = () => {
    setUsuario(null);
    localStorage.removeItem('usuario');
  };

  return (
    <AuthContext.Provider
      value={{
        usuario,
        isLoading,
        login,
        logout,
        isAuthenticated: usuario !== null,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
