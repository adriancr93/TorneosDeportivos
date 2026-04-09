import React, { useState } from 'react';

interface LoginFormProps {
  email: string;
  password: string;
  loading: boolean;
  onEmailChange: (email: string) => void;
  onPasswordChange: (password: string) => void;
  onSubmit: (e: React.FormEvent) => void;
  onSwitchToRegister: () => void;
}

const LoginForm: React.FC<LoginFormProps> = ({
  email,
  password,
  loading,
  onEmailChange,
  onPasswordChange,
  onSubmit,
  onSwitchToRegister,
}) => {
  const [showPassword, setShowPassword] = useState(false);

  return (
    <div className='m-1'>
        <form onSubmit={onSubmit} className="space-y-8 p-10 bg-white">
          <div className='flex flex-col gap-5'>
            <label htmlFor="email" className="font-normal text-2xl">Correo</label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => onEmailChange(e.target.value)}
              placeholder="Usuario"
              className="w-full p-3 border-gray-300 border"
              disabled={loading}
              required
            />
          </div>

          <div className="relative">
            <label htmlFor="password" className="mb-2 block text-sm font-semibold text-[#2a3547]">Contrasena</label>
            <input
              id="password"
              type={showPassword ? 'text' : 'password'}
              value={password}
              onChange={(e) => onPasswordChange(e.target.value)}
              placeholder="Contraseña"
              className="w-full rounded-lg border border-[#dfe5ef] bg-white px-4 py-3 pr-11 text-[#2a3547] placeholder:text-[#91a1bc] focus:border-[#5d87ff] focus:outline-none"
              disabled={loading}
              required
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
              tabIndex={-1}
            >
            </button>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full rounded-lg bg-[#5d87ff] py-3 font-bold text-white hover:bg-[#4b74e8] focus:outline-none focus:ring-4 focus:ring-[#e1eaff] transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {loading ? 'Cargando...' : 'Sign in'}
          </button>

          <div className="text-center">
            <button
              type="button"
              onClick={onSwitchToRegister}
              className="text-sm text-[#5a6a85] hover:text-[#2a3547] hover:underline"
            >
              Create an account
            </button>
          </div>
        </form>
    </div>
    
  );
};

export default LoginForm;
