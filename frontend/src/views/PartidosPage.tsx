import React, { useEffect, useState } from 'react';
import { partidosApi } from '../api/client';
import type { Partido } from '../types';
import StatsCard from '../components/StatsCard';

const PartidosPage: React.FC = () => {
  const [partidos, setPartidos] = useState<Partido[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const data = await partidosApi.getAll();
        setPartidos(data);
        setError('');
      } catch (err) {
        console.error('Error fetching partidos:', err);
        setError('Error al cargar los partidos desde el servidor');
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
          <p className="text-gray-600 font-medium">Cargando partidos...</p>
        </div>
      </div>
    );
  }

  const totalGoles = partidos.reduce((acc, p) => acc + p.golesLocal + p.golesVisitante, 0);
  const partidosJugados = partidos.filter((p) => p.golesLocal >= 0 && p.golesVisitante >= 0).length;

  return (
    <div className="space-y-8">
      {/* Header */}
      <div>
        <h1 className="text-4xl font-bold text-gray-900 mb-2">Partidos</h1>
        <p className="text-gray-600">Resultados y detalles de todos los encuentros</p>
      </div>

      {error && (
        <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-red-700 font-medium">
          {error}
        </div>
      )}

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <StatsCard
          label="Total Partidos"
          value={partidos.length}
          emoji="⚽"
          backgroundColor="bg-orange-100"
        />
        <StatsCard
          label="Partidos Jugados"
          value={partidosJugados}
          emoji="✓"
          backgroundColor="bg-green-100"
        />
        <StatsCard
          label="Total Goles"
          value={totalGoles}
          emoji="🔥"
          backgroundColor="bg-red-100"
        />
      </div>

      {/* Partidos Grid */}
      <div className="rounded-lg border border-gray-200 bg-white shadow-sm">
        <div className="border-b border-gray-200 px-6 py-4">
          <h2 className="text-lg font-bold text-gray-900">Listado de Partidos</h2>
        </div>
        <div className="p-6">
          {partidos.length === 0 ? (
            <p className="py-10 text-center text-gray-500">No hay partidos registrados</p>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {partidos.map((partido) => (
                <article key={partido.partidoId} className="rounded-lg border border-gray-200 p-5 hover:shadow-lg transition-all">
                  <div className="mb-4 text-xs font-semibold text-gray-500 uppercase bg-gray-100 px-2 py-1 rounded inline-block">
                    {partido.torneoId}
                  </div>

                  {partido.golesLocal >= 0 && partido.golesVisitante >= 0 ? (
                    <div className="flex items-center justify-between gap-4">
                      <div className="flex-1">
                        <p className="font-semibold text-gray-900 truncate">{partido.equipoLocal}</p>
                        <p className="text-3xl font-bold text-green-600 mt-1">{partido.golesLocal}</p>
                      </div>
                      <div className="flex-1 text-center">
                        <p className="text-xs text-gray-500 mb-2">VS</p>
                        <p className="text-sm text-gray-600 font-medium">
                          {new Date(partido.fecha).toLocaleDateString('es-ES')}
                        </p>
                      </div>
                      <div className="flex-1 text-right">
                        <p className="font-semibold text-gray-900 truncate">{partido.equipoVisitante}</p>
                        <p className="text-3xl font-bold text-red-600 mt-1">{partido.golesVisitante}</p>
                      </div>
                    </div>
                  ) : (
                    <div className="text-center">
                      <p className="text-sm font-medium text-gray-900 mb-2">
                        {partido.equipoLocal} <span className="text-gray-400">vs</span> {partido.equipoVisitante}
                      </p>
                      <p className="text-xs text-gray-500 mb-3">
                        {new Date(partido.fecha).toLocaleDateString('es-ES')}
                      </p>
                      <span className="inline-block rounded-full bg-yellow-100 px-3 py-1 text-xs font-semibold text-yellow-800">
                        ⏳ Pendiente
                      </span>
                    </div>
                  )}

                  <div className="mt-4 border-t border-gray-200 pt-3 text-xs text-gray-600 space-y-1">
                    <p>📍 Estadio: {partido.estadio || 'Sin especificar'}</p>
                    <p>📅 {new Date(partido.fecha).toLocaleDateString('es-ES', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}</p>
                  </div>
                </article>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default PartidosPage;
