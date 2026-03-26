import React, { useEffect, useState } from 'react';
import { equiposApi } from '../api/client';
import type { Equipo } from '../types';
import StatsCard from '../components/StatsCard';

const EquiposPage: React.FC = () => {
  const [equipos, setEquipos] = useState<Equipo[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [expandedId, setExpandedId] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const data = await equiposApi.getAll();
        setEquipos(data);
        setError('');
      } catch (err) {
        console.error('Error fetching equipos:', err);
        setError('Error al cargar los equipos desde el servidor');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return (
      <div className="flex h-full items-center justify-center">
        <div className="text-center">
          <div className="inline-block animate-spin rounded-full border-4 border-gray-200 border-t-green-600 w-12 h-12 mb-4"></div>
          <p className="text-gray-600 font-medium">Cargando equipos...</p>
        </div>
      </div>
    );
  }

  const totalJugadores = equipos.reduce((acc, eq) => acc + (eq.jugadores?.length || 0), 0);

  return (
    <div className="space-y-8">
      {/* Header */}
      <div>
        <h1 className="text-4xl font-bold text-gray-900 mb-2">Equipos</h1>
        <p className="text-gray-600">Administra todos los equipos participantes</p>
      </div>

      {error && (
        <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-red-700 font-medium">
          {error}
        </div>
      )}

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <StatsCard
          label="Total Equipos"
          value={equipos.length}
          emoji="👥"
          backgroundColor="bg-blue-100"
        />
        <StatsCard
          label="Total Jugadores"
          value={totalJugadores}
          emoji="⚽"
          backgroundColor="bg-green-100"
        />
        <StatsCard
          label="Prom. Jugadores/Equipo"
          value={equipos.length > 0 ? (totalJugadores / equipos.length).toFixed(1) : '0'}
          emoji="📊"
          backgroundColor="bg-purple-100"
        />
      </div>

      {/* Equipos List */}
      <div className="rounded-lg border border-gray-200 bg-white shadow-sm">
        <div className="border-b border-gray-200 px-6 py-4">
          <h2 className="text-lg font-bold text-gray-900">Listado de Equipos</h2>
        </div>
        <div className="divide-y divide-gray-200">
          {equipos.length === 0 ? (
            <p className="py-10 text-center text-gray-500">No hay equipos registrados</p>
          ) : (
            equipos.map((equipo) => (
              <div key={equipo.equipoId} className="p-6 hover:bg-gray-50 transition-colors">
                <button
                  onClick={() => setExpandedId(expandedId === equipo.equipoId ? null : equipo.equipoId)}
                  className="w-full flex items-center justify-between"
                >
                  <div className="flex items-center gap-4 text-left">
                    <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-green-100 font-bold text-green-700">
                      {equipo.nombre.charAt(0).toUpperCase()}
                    </div>
                    <div>
                      <h3 className="font-bold text-gray-900">{equipo.nombre}</h3>
                      <p className="text-sm text-gray-600">📍 {equipo.ciudad} • DT: {equipo.entrenador}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-4">
                    <span className="text-sm font-semibold text-gray-600 bg-gray-100 px-3 py-1 rounded-full">
                      {equipo.jugadores?.length || 0} 👥
                    </span>
                    <div className="text-gray-400 text-lg">
                      {expandedId === equipo.equipoId ? '▼' : '▶'}
                    </div>
                  </div>
                </button>

                {/* Expanded Content */}
                {expandedId === equipo.equipoId && (
                  <div className="mt-4 pt-4 border-t border-gray-100 animate-in">
                    <h4 className="mb-4 font-semibold text-gray-900">👥 Jugadores del Equipo</h4>
                    {equipo.jugadores && equipo.jugadores.length > 0 ? (
                      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
                        {equipo.jugadores.map((jugador, idx) => (
                          <div key={idx} className="rounded-lg border border-gray-200 bg-gradient-to-br from-gray-50 to-white p-3 hover:shadow-md transition-shadow">
                            <p className="font-semibold text-gray-900">{idx + 1}. {jugador.nombre}</p>
                            <p className="text-xs text-gray-600 mt-1">
                              <span className="font-medium">Posición:</span> {jugador.posicion}
                            </p>
                            <p className="text-xs text-gray-600 mt-0.5">
                              <span className="font-medium">Número:</span> #{jugador.numero}
                            </p>
                          </div>
                        ))}
                      </div>
                    ) : (
                      <p className="text-sm text-gray-500">Sin jugadores registrados</p>
                    )}
                  </div>
                )}
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default EquiposPage;
