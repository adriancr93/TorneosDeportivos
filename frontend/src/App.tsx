import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './lib/authContext';
import ProtectedRoute from './lib/ProtectedRoute';
import LoginPage from './views/LoginPage';
import DashboardLayout from './components/DashboardLayout';
import TorneosPage from './views/TorneosPage';
import EquiposPage from './views/EquiposPage';
import PartidosPage from './views/PartidosPage';
import EstadisticasPage from './views/EstadisticasPage';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Public Routes */}
          <Route path="/login" element={<LoginPage />} />

          {/* Protected Routes */}
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <DashboardLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<Navigate to="/dashboard/torneos" replace />} />
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

export default App;
