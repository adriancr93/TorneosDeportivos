import React from 'react';

interface RegisterFormProps {
  email: string;
  password: string;
  confirmPassword: string;
  loading: boolean;
  error: string;
  onEmailChange: (email: string) => void;
  onPasswordChange: (password: string) => void;
  onConfirmPasswordChange: (password: string) => void;
  onSubmit: (e: React.FormEvent) => void;
}

const RegisterForm: React.FC<RegisterFormProps> = ({
  email,
  password,
  confirmPassword,
  loading,
  error,
  onEmailChange,
  onPasswordChange,
  onConfirmPasswordChange,
  onSubmit,
}) => {
  return (
    <form onSubmit={onSubmit} className="space-y-4">
      {error && (
        <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700 font-medium">
          {error}
        </div>
      )}

      <div>
        <label htmlFor="email-register" className="block text-sm font-semibold text-gray-700 mb-2">
          Correo Electrónico
        </label>
        <input
          id="email-register"
          type="email"
          value={email}
          onChange={(e) => onEmailChange(e.target.value)}
          placeholder="tu@correo.com"
          className="w-full rounded-lg border border-gray-300 bg-white px-4 py-3 text-gray-900 placeholder:text-gray-400 focus:border-green-500 focus:outline-none focus:ring-4 focus:ring-green-100 transition-all"
          disabled={loading}
          required
        />
      </div>

      <div>
        <label htmlFor="password-register" className="block text-sm font-semibold text-gray-700 mb-2">
          Contraseña
        </label>
        <input
          id="password-register"
          type="password"
          value={password}
          onChange={(e) => onPasswordChange(e.target.value)}
          placeholder="••••••••"
          className="w-full rounded-lg border border-gray-300 bg-white px-4 py-3 text-gray-900 placeholder:text-gray-400 focus:border-green-500 focus:outline-none focus:ring-4 focus:ring-green-100 transition-all"
          disabled={loading}
          required
        />
      </div>

      <div>
        <label htmlFor="confirm-password" className="block text-sm font-semibold text-gray-700 mb-2">
          Confirmar Contraseña
        </label>
        <input
          id="confirm-password"
          type="password"
          value={confirmPassword}
          onChange={(e) => onConfirmPasswordChange(e.target.value)}
          placeholder="••••••••"
          className="w-full rounded-lg border border-gray-300 bg-white px-4 py-3 text-gray-900 placeholder:text-gray-400 focus:border-green-500 focus:outline-none focus:ring-4 focus:ring-green-100 transition-all"
          disabled={loading}
          required
        />
      </div>

      <button
        type="submit"
        disabled={loading}
        className="w-full rounded-lg bg-gradient-to-r from-green-600 to-green-700 px-4 py-3 font-semibold text-white hover:from-green-700 hover:to-green-800 focus:outline-none focus:ring-4 focus:ring-green-200 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
      >
        {loading ? 'Creando cuenta...' : 'Crear Cuenta'}
      </button>
    </form>
  );
};

export default RegisterForm;
