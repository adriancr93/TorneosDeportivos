import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { AuthProvider } from './lib/authContext';
import ProtectedRoute from './lib/ProtectedRoute';
import AuthLayout from './components/layouts/AuthLayout';
import LoginPage from './views/LoginPage';
import RegisterPage from './views/RegisterPage';
import DashboardLayout from './components/layouts/DashboardLayout';
import JugadoresPage from './views/JugadoresPage';
import TorneosPage from './views/TorneosPage';
import EquiposPage from './views/EquiposPage';
import PartidosPage from './views/PartidosPage';
import EstadisticasPage from './views/EstadisticasPage';

export default function Router() {
  return (
    <BrowserRouter>
      <ToastContainer position="top-right" autoClose={4000} hideProgressBar={false} newestOnTop closeOnClick pauseOnHover theme="colored" aria-label="Notifications" />
      <AuthProvider>
        <Routes>
          {/* Public Routes */}
          <Route element={<AuthLayout />}>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
          </Route>
          <Route path="/auth/register" element={<Navigate to="/register" replace />} />

          {/* Protected Routes */}
          <Route path="/dashboard"
            element={
              <ProtectedRoute>
                <DashboardLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<Navigate to="/dashboard/torneos" replace />} />
            <Route path="jugadores" element={<JugadoresPage />} />
            <Route path="torneos" element={<TorneosPage />} />
            <Route path="equipos" element={<EquiposPage />} />
            <Route path="partidos" element={<PartidosPage />} />
            <Route path="estadisticas" element={<EstadisticasPage />} />
          </Route>

          {/* Redirect root to login */}
          <Route path="/" element={<Navigate to="/login" replace />} />

          {/* 404 fallback */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
