import React, { useEffect, useState } from 'react';
import { standingsApi, partidosApi } from '../api/client';
import type { Standings, Partido } from '../types';
import StatsCard from '../components/StatsCard';

const EstadisticasPage: React.FC = () => {
  const [standings, setStandings] = useState<Standings[]>([]);
  const [recentPartidos, setRecentPartidos] = useState<Partido[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [standingsData, partidosData] = await Promise.all([
          standingsApi.getAll(),
          partidosApi.getAll(),
        ]);
        setStandings(standingsData);
        setRecentPartidos(partidosData.slice(0, 5));
        setError('');
      } catch (err) {
        console.error('Error fetching estadisticas:', err);
        setError('Error al cargar estadísticas desde el servidor');
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
          <p className="text-gray-600 font-medium">Cargando estadísticas...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      {/* Header */}
      <div>
        <h1 className="text-4xl font-bold text-gray-900 mb-2">Estadísticas</h1>
        <p className="text-gray-600">Posiciones, goleadores y partidos recientes</p>
      </div>

      {error && (
        <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-red-700 font-medium">
          {error}
        </div>
      )}

      {/* Stats Overview */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <StatsCard
          label="Equipos"
          value={standings.length}
          emoji="👥"
          backgroundColor="bg-blue-100"
        />
        <StatsCard
          label="Partidos Totales"
          value={recentPartidos.length}
          emoji="⚽"
          backgroundColor="bg-green-100"
        />
        <StatsCard
          label="Goles Anotados"
          value={standings.reduce((acc, s) => acc + s.golesAFavor, 0)}
          emoji="🔥"
          backgroundColor="bg-red-100"
        />
      </div>

      {/* Grid Layout */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Standings */}
        <div className="lg:col-span-2">
          <div className="rounded-lg border border-gray-200 bg-white shadow-sm">
            <div className="border-b border-gray-200 px-6 py-4">
              <h2 className="text-lg font-bold text-gray-900">Tabla de Posiciones</h2>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-gray-200 bg-gray-50">
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Pos</th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Equipo</th>
                    <th className="px-6 py-3 text-center text-xs font-semibold text-gray-600 uppercase">PJ</th>
                    <th className="px-6 py-3 text-center text-xs font-semibold text-gray-600 uppercase">G</th>
                    <th className="px-6 py-3 text-center text-xs font-semibold text-gray-600 uppercase">E</th>
                    <th className="px-6 py-3 text-center text-xs font-semibold text-gray-600 uppercase">P</th>
                    <th className="px-6 py-3 text-center text-xs font-semibold text-gray-600 uppercase">Pts</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {standings.length === 0 ? (
                    <tr>
                      <td colSpan={7} className="py-8 text-center text-gray-500">
                        No hay datos de posiciones
                      </td>
                    </tr>
                  ) : (
                    standings.map((standing, idx) => (
                      <tr
                        key={standing.equipoId}
                        className={`transition-colors ${
                          idx === 0 ? 'bg-green-50 hover:bg-green-100' : idx === standings.length - 1 ? 'bg-red-50 hover:bg-red-100' : 'hover:bg-gray-50'
                        }`}
                      >
                        <td className="px-6 py-4 font-bold text-gray-900">
                          {idx === 0 ? '🥇' : idx === 1 ? '🥈' : idx === 2 ? '🥉' : idx + 1}
                        </td>
                        <td className="px-6 py-4 font-semibold text-gray-900">{standing.nombre}</td>
                        <td className="px-6 py-4 text-center text-gray-600">{standing.partidos}</td>
                        <td className="px-6 py-4 text-center text-green-600 font-semibold">{standing.ganados}</td>
                        <td className="px-6 py-4 text-center text-yellow-600 font-semibold">{standing.empatados}</td>
                        <td className="px-6 py-4 text-center text-red-600 font-semibold">{standing.perdidos}</td>
                        <td className="px-6 py-4 text-center font-bold text-gray-900 bg-gradient-to-r from-transparent to-green-50">
                          {standing.puntos}
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        {/* Resumen */}
        <div>
          <div className="rounded-lg border border-gray-200 bg-white shadow-sm">
            <div className="border-b border-gray-200 px-6 py-4">
              <h2 className="text-lg font-bold text-gray-900">Resumen</h2>
            </div>
            <div className="divide-y divide-gray-200 p-6">
              <div className="pb-4 mb-4">
                <p className="text-sm text-gray-600 mb-1">Total Equipos</p>
                <p className="text-4xl font-bold text-green-600">{standings.length}</p>
              </div>
              <div className="py-4">
                <p className="text-sm text-gray-600 mb-1">Partidos Registrados</p>
                <p className="text-4xl font-bold text-blue-600">{recentPartidos.length}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Recent Matches */}
      <div className="rounded-lg border border-gray-200 bg-white shadow-sm">
        <div className="border-b border-gray-200 px-6 py-4">
          <h2 className="text-lg font-bold text-gray-900">Partidos Recientes</h2>
        </div>
        <div className="divide-y divide-gray-200">
          {recentPartidos.length === 0 ? (
            <p className="py-8 text-center text-gray-500">No hay partidos recientes</p>
          ) : (
            recentPartidos.map((partido) => (
              <div key={partido.partidoId} className="p-6 hover:bg-gray-50 transition-colors">
                <div className="flex items-center justify-between gap-6">
                  <div className="flex items-center gap-6 flex-1">
                    <div className="text-right">
                      <p className="font-semibold text-gray-900">{partido.equipoLocal}</p>
                      <p className="text-xs text-gray-500 mt-1">{new Date(partido.fecha).toLocaleDateString('es-ES')}</p>
                    </div>
                    {partido.golesLocal >= 0 && partido.golesVisitante >= 0 ? (
                      <div className="flex items-center gap-2 bg-gray-100 rounded-lg px-4 py-2">
                        <span className="text-2xl font-bold text-green-600">{partido.golesLocal}</span>
                        <span className="text-gray-400 font-bold">-</span>
                        <span className="text-2xl font-bold text-red-600">{partido.golesVisitante}</span>
                      </div>
                    ) : (
                      <span className="rounded-full bg-yellow-100 px-3 py-1 text-xs font-semibold text-yellow-800">Pendiente</span>
                    )}
                    <div className="text-left">
                      <p className="font-semibold text-gray-900">{partido.equipoVisitante}</p>
                      <p className="text-xs text-gray-500 mt-1">{partido.torneoId}</p>
                    </div>
                  </div>
                  <button className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700 transition-colors">
                    Detalles
                  </button>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default EstadisticasPage;
