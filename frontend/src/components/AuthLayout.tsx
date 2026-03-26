import React from 'react';

interface AuthLayoutProps {
  children: React.ReactNode;
}

const AuthLayout: React.FC<AuthLayoutProps> = ({ children }) => {
  return (
    <div className="flex h-screen overflow-hidden bg-white">
      {/* Left Side - Hero/Branding */}
      <div className="hidden w-1/2 bg-gradient-to-br from-green-600 via-green-700 to-green-800 p-12 lg:flex lg:flex-col lg:justify-between relative overflow-hidden">
        {/* Decorative elements */}
        <div className="absolute top-0 right-0 w-96 h-96 bg-green-500 rounded-full opacity-20 -mr-48 -mt-48"></div>
        <div className="absolute bottom-0 left-0 w-72 h-72 bg-white rounded-full opacity-10 -ml-36 -mb-36"></div>

        <div className="relative z-10">
          <div className="flex items-center gap-3 mb-16">
            <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-white text-green-600 font-bold text-2xl shadow-lg">
              ⚽
            </div>
            <div>
              <h1 className="text-3xl font-bold text-white">Torneos</h1>
              <p className="text-green-100 text-sm">Sistema Deportivo Profesional</p>
            </div>
          </div>

          <div className="space-y-10">
            <div>
              <h2 className="text-5xl font-bold text-white mb-6 leading-tight">
                Gestión de Torneos Deportivos
              </h2>
              <p className="text-xl text-green-50 leading-relaxed">
                Plataforma completa para administrar ligas, equipos, partidos y estadísticas en tiempo real
              </p>
            </div>

            <div className="space-y-4">
              <div className="flex items-start gap-4">
                <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-white/20 text-white flex-shrink-0 mt-1 font-bold">
                  ✓
                </div>
                <div>
                  <h3 className="text-lg font-semibold text-white">Actualizaciones en Tiempo Real</h3>
                  <p className="text-green-100 mt-1">Información de partidos y resultados actualizada instantáneamente</p>
                </div>
              </div>

              <div className="flex items-start gap-4">
                <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-white/20 text-white flex-shrink-0 mt-1 font-bold">
                  ✓
                </div>
                <div>
                  <h3 className="text-lg font-semibold text-white">Tabla Automática</h3>
                  <p className="text-green-100 mt-1">Posiciones y goleadores calculados automáticamente</p>
                </div>
              </div>

              <div className="flex items-start gap-4">
                <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-white/20 text-white flex-shrink-0 mt-1 font-bold">
                  ✓
                </div>
                <div>
                  <h3 className="text-lg font-semibold text-white">Panel Intuitivo</h3>
                  <p className="text-green-100 mt-1">Interfaz fácil de usar para administradores y usuarios</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <p className="text-sm text-green-200 relative z-10">© 2026 Sistema de Torneos. Todos los derechos reservados.</p>
      </div>

      {/* Right Side - Auth Form */}
      <div className="flex w-full items-center justify-center px-6 py-12 lg:w-1/2 bg-gradient-to-b from-gray-50 to-gray-100">
        <div className="w-full max-w-md">
          {children}
        </div>
      </div>
    </div>
  );
};

export default AuthLayout;
