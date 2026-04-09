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
        <form onSubmit={onSubmit} className="border-0 w-full">
          <div>
            <label htmlFor="email" className="mb-2 block text-sm font-semibold text-[#2a3547]">Correo</label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => onEmailChange(e.target.value)}
              placeholder="Usuario"
              className="w-full rounded-lg border border-[#dfe5ef] bg-white px-4 py-3 text-[#2a3547] placeholder:text-[#91a1bc] focus:border-[#5d87ff] focus:outline-none"
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
              {showPassword ? (
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" className="h-5 w-5">
                  <path d="M3.53 2.47a.75.75 0 0 0-1.06 1.06l18 18a.75.75 0 1 0 1.06-1.06l-18-18zM22.676 12.553a11.249 11.249 0 0 1-2.631 4.31l-3.099-3.099a5.25 5.25 0 0 0-6.71-6.71L7.759 4.577a11.217 11.217 0 0 1 4.242-.827c4.97 0 9.185 3.223 10.675 7.69.12.362.12.752 0 1.113z" />
                  <path d="M15.75 12c0 .18-.013.357-.037.53l-4.244-4.243A3.75 3.75 0 0 1 15.75 12zM12.53 15.713l-4.243-4.244a3.75 3.75 0 0 0 4.244 4.243z" />
                  <path d="M6.75 12c0-.619.107-1.213.304-1.764l-3.1-3.1a11.25 11.25 0 0 0-2.63 4.31c-.12.362-.12.752 0 1.114 1.489 4.467 5.704 7.69 10.675 7.69 1.5 0 2.933-.294 4.242-.827l-2.477-2.477A3.75 3.75 0 0 1 6.75 12z" />
                </svg>
              ) : (
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" className="h-5 w-5">
                  <path d="M12 15a3 3 0 1 0 0-6 3 3 0 0 0 0 6z" />
                  <path fillRule="evenodd" d="M1.323 11.447C2.811 6.976 7.028 3.75 12.001 3.75c4.97 0 9.185 3.223 10.675 7.69.12.362.12.752 0 1.113-1.487 4.471-5.705 7.697-10.677 7.697-4.97 0-9.186-3.223-10.675-7.69a1.762 1.762 0 0 1 0-1.113zM17.25 12a5.25 5.25 0 1 1-10.5 0 5.25 5.25 0 0 1 10.5 0z" clipRule="evenodd" />
                </svg>
              )}
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
