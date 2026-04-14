import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { toast } from 'react-toastify';
import { authApi } from '../api/client';
import { useAuth } from '../lib/authContext';

const RegisterPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

  const navigate = useNavigate();
  const { login } = useAuth();

  const validateEmail = (value: string): boolean => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(value);
  };

  const handleAuthError = (err: unknown) => {
    console.error('Auth error:', err);
    if (axios.isAxiosError(err)) {
      const backendMessage = (err.response?.data as { message?: string } | undefined)?.message;

      if (backendMessage) {
        toast.error(backendMessage);
      } else if (err.code === 'ERR_NETWORK') {
        toast.error('Error de conexión. Asegúrate que el servidor esté corriendo en puerto 8080');
      } else {
        toast.error('No se pudo completar la solicitud. Intenta nuevamente.');
      }
      return;
    }

    toast.error('Ocurrió un error inesperado. Intenta nuevamente.');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      if (!email || !password || !confirmPassword) {
        toast.error('Por favor completa todos los campos');
        return;
      }

      if (!validateEmail(email)) {
        toast.error('Por favor ingresa un correo válido');
        return;
      }

      if (password.length < 6) {
        toast.error('La contraseña debe tener al menos 6 caracteres');
        return;
      }

      if (password !== confirmPassword) {
        toast.error('Las contraseñas no coinciden');
        return;
      }

      const response = await authApi.register(email, password);

      if (!response.success) {
        toast.error(response.message || 'Error al crear la cuenta');
        return;
      }

      login({ username: response.username || email, usuarioId: response.usuarioId });
      navigate('/dashboard');
    } catch (err) {
      handleAuthError(err);
    } finally {
      setLoading(false);
    }
  };

  return (
      <form onSubmit={handleSubmit} className="space-y-6 bg-white p-10">
        <div className="mb-6 text-center">
          <h2 className="text-3xl font-extrabold text-[#2a3547]">Crear Cuenta</h2>
          <p className="mt-1 text-sm text-[#5a6a85]">Registra tu usuario para administrar torneos</p>
        </div>
        
        <div className="flex flex-col gap-3">
          <label htmlFor="email" className="text-lg font-medium text-[#2a3547]">Email</label>
          <input
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Email de registro"
            className="w-full border border-gray-300 p-3"
            disabled={loading}
            required
          />
        </div>

        <div className="flex flex-col gap-3">
          <label htmlFor="password" className="text-lg font-medium text-[#2a3547]">Password</label>
          <div className="relative">
            <input
              id="password"
              type={showPassword ? 'text' : 'password'}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Password de registro"
              className="w-full border border-gray-300 p-3 pr-12"
              disabled={loading}
              required
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-xs font-semibold text-[#5d87ff]"
              tabIndex={-1}
            >
              {showPassword ? 'Ocultar' : 'Mostrar'}
            </button>
          </div>
        </div>

        <div className="flex flex-col gap-3">
          <label htmlFor="confirmPassword" className="text-lg font-medium text-[#2a3547]">Confirmar Password</label>
          <div className="relative">
            <input
              id="confirmPassword"
              type={showConfirm ? 'text' : 'password'}
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Confirmar password"
              className="w-full border border-gray-300 p-3 pr-12"
              disabled={loading}
              required
            />
            <button
              type="button"
              onClick={() => setShowConfirm(!showConfirm)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-xs font-semibold text-[#5d87ff]"
              tabIndex={-1}
            >
              {showConfirm ? 'Ocultar' : 'Mostrar'}
            </button>
          </div>
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-[#5d87ff] p-3 text-xl font-black text-white transition-colors hover:bg-[#4b74e8] disabled:cursor-not-allowed disabled:opacity-50"
        >
          {loading ? 'Creando cuenta...' : 'Crear Cuenta'}
        </button>

        <nav className="mt-6 flex flex-col space-y-3 text-center">
          <Link to="/login" className="font-normal text-gray-500 hover:underline">
            Ya tienes cuenta? Inicia sesión
          </Link>
        </nav>
      </form>
  );
};

export default RegisterPage;
