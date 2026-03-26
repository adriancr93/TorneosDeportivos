import React, { useEffect, useState } from 'react';
import { torneosApi, equiposApi } from '../api/client';
import type { Torneo, Equipo } from '../types';
import StatsCard from '../components/StatsCard';

const TorneosPage: React.FC = () => {
  const [torneos, setTorneos] = useState<Torneo[]>([]);
  const [equipos, setEquipos] = useState<Equipo[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [torneosData, equiposData] = await Promise.all([
          torneosApi.getAll(),
          equiposApi.getAll(),
        ]);
        setTorneos(torneosData);
        setEquipos(equiposData);
        setError('');
      } catch (err) {
        console.error('Error fetching data:', err);
        setError('Error al cargar los datos del servidor');
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
          <p className="text-gray-600 font-medium">Cargando torneos...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      {/* Header */}
      <div>
        <h1 className="text-4xl font-bold text-gray-900 mb-2">Torneos</h1>
        <p className="text-gray-600">Gestiona y visualiza todos los torneos disputados</p>
      </div>

      {error && (
        <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-red-700 font-medium">
          {error}
        </div>
      )}

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <StatsCard
          label="Total Torneos"
          value={torneos.length}
          emoji="🏆"
          backgroundColor="bg-green-100"
        />
        <StatsCard
          label="Total Equipos"
          value={equipos.length}
          emoji="👥"
          backgroundColor="bg-blue-100"
        />
        <StatsCard
          label="Promedio Equipos/Torneo"
          value={(torneos.length > 0 ? (equipos.length / torneos.length).toFixed(1) : '0')}
          emoji="📊"
          backgroundColor="bg-purple-100"
        />
      </div>

      {/* Torneos List */}
      <div className="rounded-lg border border-gray-200 bg-white shadow-sm">
        <div className="border-b border-gray-200 px-6 py-4">
          <h2 className="text-lg font-bold text-gray-900">Listado de Torneos</h2>
        </div>
        <div className="p-6">
          {torneos.length === 0 ? (
            <p className="py-10 text-center text-gray-500">No hay torneos registrados</p>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {torneos.map((torneo) => (
                <article
                  key={torneo.torneoId}
                  className="rounded-lg border border-gray-200 p-5 bg-white hover:shadow-lg transition-all duration-200"
                >
                  <div className="flex items-start justify-between mb-4">
                    <h3 className="text-lg font-bold text-gray-900">{torneo.nombre}</h3>
                    <span className="inline-block rounded-full bg-green-100 px-3 py-1 text-xs font-semibold text-green-800">
                      {torneo.estado || 'Activo'}
                    </span>
                  </div>
                  <div className="space-y-2.5 text-sm text-gray-600">
                    <div className="flex justify-between items-center">
                      <span>📅 Inicio:</span>
                      <span className="font-medium text-gray-900">{torneo.fechaInicio}</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span>📅 Fin:</span>
                      <span className="font-medium text-gray-900">{torneo.fechaFin}</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span>👥 Equipos:</span>
                      <span className="font-medium text-gray-900">{torneo.equipos?.length || 0}</span>
                    </div>
                  </div>
                  <button className="mt-4 w-full rounded-lg bg-gradient-to-r from-green-600 to-green-700 px-4 py-2 text-sm font-semibold text-white hover:from-green-700 hover:to-green-800 transition-all">
                    Ver Detalles
                  </button>
                </article>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Equipos Section */}
      <div className="rounded-lg border border-gray-200 bg-white shadow-sm">
        <div className="border-b border-gray-200 px-6 py-4">
          <h2 className="text-lg font-bold text-gray-900">Equipos Participantes</h2>
        </div>
        <div className="p-6">
          {equipos.length === 0 ? (
            <p className="py-10 text-center text-gray-500">No hay equipos registrados</p>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
              {equipos.map((equipo) => (
                <article key={equipo.equipoId} className="rounded-lg border border-gray-200 p-4 bg-gradient-to-br from-gray-50 to-gray-100 hover:from-white hover:to-gray-50 transition-all">
                  <div className="flex items-center gap-3 mb-3">
                    <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-green-100 font-bold text-green-700">
                      {equipo.nombre.charAt(0).toUpperCase()}
                    </div>
                    <h4 className="font-bold text-gray-900">{equipo.nombre}</h4>
                  </div>
                  <div className="space-y-1 text-sm text-gray-600">
                    <p>📍 {equipo.ciudad}</p>
                    <p>👤 DT: <span className="font-semibold">{equipo.entrenador}</span></p>
                    <p>👥 Jugadores: <span className="font-semibold">{equipo.jugadores?.length || 0}</span></p>
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

export default TorneosPage;
