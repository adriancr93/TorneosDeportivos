# React Dashboard - Setup & Testing Guide

## Prerequisites
- Backend running on `http://localhost:8080`
- MongoDB connection established
- Node.js 20+ installed

## How to Run the React Dashboard

### Development Mode
```bash
cd frontend
npm run dev
```
The application will be available at `http://localhost:5173`

### Production Build
```bash
cd frontend
npm run build
npm run preview  # To preview the production build locally
```

## Project Structure

### `/frontend/src/`
- **`api/`** - API client and HTTP requests
  - `client.ts` - Axios configuration and API endpoints
  
- **`components/`** - Reusable UI components
  - `DashboardLayout.tsx` - Main dashboard layout with sidebar
  
- **`lib/`** - Utility functions and context
  - `authContext.tsx` - Authentication state management
  - `ProtectedRoute.tsx` - Route protection HOC
  
- **`types/`** - TypeScript type definitions
  - `index.ts` - All shared types (Usuario, Equipo, Torneo, etc.)
  
- **`views/`** - Page components
  - `LoginPage.tsx` - Login/Register form
  - `TorneosPage.tsx` - Tournaments overview
  - `EquiposPage.tsx` - Teams and players
  - `PartidosPage.tsx` - Matches and results
  - `EstadisticasPage.tsx` - Standings and statistics

### Features

#### 🔐 Authentication
- Login with username & password
- User registration
- Session persistence in localStorage
- Auto-logout redirect

#### 📊 Dashboard Pages
1. **Torneos** - View all tournaments and participating teams
2. **Equipos** - View teams with expandable player lists
3. **Partidos** - Match results with timeline view
4. **Estadísticas** - Standings table, top scorers, recent matches

#### 🎨 Design
- Tailwind CSS for styling
- Responsive grid layouts
- Gradient backgrounds
- Hover animations and transitions
- Professional color scheme (blue/indigo)

## API Integration

The frontend connects to these backend endpoints:

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/auth/register` | POST | Register new user |
| `/api/auth/login` | POST | Authenticate user |
| `/api/torneos` | GET | Fetch all tournaments |
| `/api/equipos` | GET | Fetch all teams |
| `/api/partidos` | GET | Fetch all matches |
| `/api/standings` | GET | Fetch standings table |
| `/api/goleadores` | GET | Fetch top scorers |

## Demo Credentials

```
Username: demo
Password: demo123
```

## Troubleshooting

### CORS Error
- Ensure backend is running with CORS headers enabled
- Check that API_BASE_URL in `src/api/client.ts` is correct

### Login Failed
- Verify backend is running on port 8080
- Check MongoDB connection in backend
- Try demo credentials first

### Data Not Loading
- Ensure MongoDB has seed data
- Run `java Main` with option `7` to seed demo data
- Check browser console for API errors

## Component Guide

### AuthContext
Provides authentication state globally:
```tsx
const { usuario, isAuthenticated, login, logout, isLoading } = useAuth();
```

### ProtectedRoute
Wraps protected pages to prevent unauthorized access:
```tsx
<ProtectedRoute>
  <DashboardLayout />
</ProtectedRoute>
```

### API Client
Import specific API modules:
```tsx
import { authApi, equiposApi, torneosApi } from '../api/client';

const response = await authApi.login('user', 'pass');
const equipos = await equiposApi.getAll();
```

## Development Tips

1. **Hot Module Replacement** - Changes auto-reload in dev mode
2. **React DevTools** - Install React DevTools browser extension
3. **Network Tab** - Debug API calls in browser DevTools
4. **Console Logs** - Check browser console for errors
5. **localStorage** - User data stored at key "usuario"

## Building for Production

```bash
npm run build  # Creates optimized dist/ folder
npm run preview  # Test production build locally
```

The production build is optimized with:
- Code splitting (84 modules)
- CSS minification
- JavaScript minification
- Asset optimization (92.72 kB gzipped)
