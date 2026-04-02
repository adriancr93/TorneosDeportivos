import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { toast } from 'react-toastify';
import { authApi } from '../api/client';
import { useAuth } from '../lib/authContext';
import AuthLayout from '../components/AuthLayout';
import AuthTabs from '../components/AuthTabs';
import LoginForm from '../components/LoginForm';
import RegisterForm from '../components/RegisterForm';

const LoginPage: React.FC = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  
  const navigate = useNavigate();
  const { login } = useAuth();

  const validateEmail = (email: string): boolean => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      // Validations
      if (!email || !password) {
        toast.error('Por favor completa todos los campos');
        setLoading(false);
        return;
      }

      if (!validateEmail(email)) {
        toast.error('Por favor ingresa un correo válido');
        setLoading(false);
        return;
      }

      if (password.length < 6) {
        toast.error('La contraseña debe tener al menos 6 caracteres');
        setLoading(false);
        return;
      }

      if (!isLogin && password !== confirmPassword) {
        toast.error('Las contraseñas no coinciden');
        setLoading(false);
        return;
      }

      if (isLogin) {
        // Login with email
        const response = await authApi.login(email, password);
        if (response.success) {
          login({ username: response.username || email, usuarioId: response.usuarioId });
          navigate('/dashboard');
        } else {
          toast.error(response.message || 'Correo o contraseña incorrectos');
        }
      } else {
        // Register with email
        const response = await authApi.register(email, password);
        if (response.success) {
          login({ username: response.username || email, usuarioId: response.usuarioId });
          navigate('/dashboard');
        } else {
          toast.error(response.message || 'Error al crear la cuenta');
        }
      }
    } catch (err) {
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
      } else {
        toast.error('Ocurrió un error inesperado. Intenta nuevamente.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout>
      <div>
        <div className="mb-6 text-center">
          <h2 className="text-3xl font-extrabold text-[#2a3547]">
            {isLogin ? 'Iniciar Sesion' : 'Crear Cuenta'}
          </h2>
          <p className="mt-1 text-sm text-[#5a6a85]">
            {isLogin ? 'Accede al panel de torneos deportivos' : 'Registra tu usuario para administrar torneos'}
          </p>
        </div>

        <AuthTabs isLogin={isLogin} onTabChange={(value) => { setIsLogin(value); }} />

        <div className="mb-5 text-center text-xs font-medium uppercase tracking-[0.18em] text-[#7a8ca8]">
          {isLogin ? 'or sign in with' : 'or sign up with'}
        </div>

        {/* Form */}
        {isLogin ? (
          <LoginForm
            email={email}
            password={password}
            loading={loading}
            onEmailChange={setEmail}
            onPasswordChange={setPassword}
            onSubmit={handleSubmit}
            onSwitchToRegister={() => { setIsLogin(false); }}
          />
        ) : (
          <RegisterForm
            email={email}
            password={password}
            confirmPassword={confirmPassword}
            loading={loading}
            onEmailChange={setEmail}
            onPasswordChange={setPassword}
            onConfirmPasswordChange={setConfirmPassword}
            onSubmit={handleSubmit}
            onSwitchToLogin={() => { setIsLogin(true); }}
          />
        )}

        {/* Demo Credentials */}
        <div className="mt-6 rounded-xl border border-[#dfe5ef] bg-[#f8fbff] p-3.5">
          <p className="mb-2 text-xs font-bold uppercase tracking-[0.16em] text-[#5d87ff]">Credenciales de Prueba</p>
          <div className="space-y-1 text-xs text-[#5a6a85]">
            <p>📧 <span className="font-mono">demo@example.com</span></p>
            <p>🔑 <span className="font-mono">demo123</span></p>
          </div>
        </div>
      </div>
    </AuthLayout>
  );
};

export default LoginPage;
