import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
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
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const navigate = useNavigate();
  const { login } = useAuth();

  const validateEmail = (email: string): boolean => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // Validations
      if (!email || !password) {
        setError('Por favor completa todos los campos');
        setLoading(false);
        return;
      }

      if (!validateEmail(email)) {
        setError('Por favor ingresa un correo válido');
        setLoading(false);
        return;
      }

      if (password.length < 6) {
        setError('La contraseña debe tener al menos 6 caracteres');
        setLoading(false);
        return;
      }

      if (!isLogin && password !== confirmPassword) {
        setError('Las contraseñas no coinciden');
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
          setError(response.message || 'Correo o contraseña incorrectos');
        }
      } else {
        // Register with email
        const response = await authApi.register(email, password);
        if (response.success) {
          login({ username: response.username || email, usuarioId: response.usuarioId });
          navigate('/dashboard');
        } else {
          setError(response.message || 'Error al crear la cuenta');
        }
      }
    } catch (err) {
      console.error('Auth error:', err);
      setError('Error de conexión. Asegúrate que el servidor esté corriendo en puerto 8080');
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout>
      <div>
        {/* Header */}
        <div className="mb-8">
          <h2 className="text-3xl font-bold text-gray-900 mb-2">
            {isLogin ? '¡Bienvenido!' : 'Crear Cuenta'}
          </h2>
          <p className="text-gray-600">
            {isLogin ? 'Inicia sesión para acceder al panel' : 'Regístrate para comenzar a usar Torneos'}
          </p>
        </div>

        {/* Tabs */}
        <AuthTabs isLogin={isLogin} onTabChange={setIsLogin} />

        {/* Form */}
        {isLogin ? (
          <LoginForm
            email={email}
            password={password}
            loading={loading}
            error={error}
            onEmailChange={setEmail}
            onPasswordChange={setPassword}
            onSubmit={handleSubmit}
          />
        ) : (
          <RegisterForm
            email={email}
            password={password}
            confirmPassword={confirmPassword}
            loading={loading}
            error={error}
            onEmailChange={setEmail}
            onPasswordChange={setPassword}
            onConfirmPasswordChange={setConfirmPassword}
            onSubmit={handleSubmit}
          />
        )}

        {/* Demo Credentials */}
        <div className="mt-8 rounded-lg border border-gray-200 bg-gradient-to-br from-blue-50 to-blue-50 p-4">
          <p className="text-xs font-bold text-blue-900 uppercase mb-3">Credenciales de Prueba</p>
          <div className="space-y-1.5 text-sm text-blue-800">
            <p>
              📧 <span className="font-mono font-medium">demo@example.com</span>
            </p>
            <p>
              🔑 <span className="font-mono font-medium">demo123</span>
            </p>
          </div>
        </div>
      </div>
    </AuthLayout>
  );
};

export default LoginPage;
