import React, { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { asistenciasApi, equiposApi, goleadoresApi, standingsApi, torneosApi } from '../api/client';
import type { AsistenciaItem, Equipo, GoleadorItem, Standings, Torneo } from '../types';
import StatsCard from '../components/dashboard/StatsCard';

const panelClass = 'rounded-lg border border-[#dfe5ef] bg-white p-6 shadow-[0_8px_24px_rgba(133,146,173,0.14)] dark:border-slate-800 dark:bg-slate-900 dark:shadow-[0_16px_32px_rgba(2,6,23,0.35)]';
const inputClass = 'w-full rounded-xl border border-[#dfe5ef] bg-white px-4 py-3 text-sm text-[#2a3547] focus:border-[#5d87ff] focus:outline-none dark:border-slate-700 dark:bg-slate-950 dark:text-slate-100 dark:focus:border-sky-400';

const EstadisticasPage: React.FC = () => {
  const [torneos, setTorneos] = useState<Torneo[]>([]);
  const [equipos, setEquipos] = useState<Equipo[]>([]);
  const [selectedTorneoId, setSelectedTorneoId] = useState('');
  const [selectedEquipoId, setSelectedEquipoId] = useState('');
  const [standings, setStandings] = useState<Standings[]>([]);
  const [goleadores, setGoleadores] = useState<GoleadorItem[]>([]);
  const [asistencias, setAsistencias] = useState<AsistenciaItem[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchStats = async (torneoId: string) => {
    if (!torneoId) {
      setStandings([]);
      setGoleadores([]);
      setAsistencias([]);
      return;
    }

    const results = await Promise.allSettled([
      standingsApi.getAll(torneoId),
      goleadoresApi.getAll(torneoId),
      asistenciasApi.getAll(torneoId),
    ]);

    const [tablaResult, goleadoresResult, asistenciasResult] = results;

    setStandings(tablaResult.status === 'fulfilled' ? tablaResult.value : []);
    setGoleadores(goleadoresResult.status === 'fulfilled' ? goleadoresResult.value : []);
    setAsistencias(asistenciasResult.status === 'fulfilled' ? asistenciasResult.value : []);

    const failed = results.filter((result) => result.status === 'rejected').length;
    if (failed > 0 && tablaResult.status !== 'fulfilled') {
      throw new Error('No se pudo cargar la tabla de posiciones del torneo');
    }
  };

  useEffect(() => {
    const bootstrap = async () => {
      try {
        setLoading(true);
        const [torneosData, equiposData] = await Promise.all([torneosApi.getAll(), equiposApi.getAll()]);
        setTorneos(torneosData);
        setEquipos(equiposData);
        const torneoId = torneosData[0]?.torneoId || '';
        setSelectedTorneoId(torneoId);
        await fetchStats(torneoId);
      } catch (fetchError) {
        console.error(fetchError);
        toast.error('No se pudieron cargar las estadísticas');
      } finally {
        setLoading(false);
      }
    };

    void bootstrap();
  }, []);

  useEffect(() => {
    if (!selectedTorneoId) return;
    setSelectedEquipoId('');
    void fetchStats(selectedTorneoId).catch((fetchError) => {
      console.error(fetchError);
      toast.error('No se pudieron refrescar las estadísticas del torneo');
    });
  }, [selectedTorneoId]);

  const torneoSeleccionado = torneos.find((torneo) => torneo.torneoId === selectedTorneoId);
  const equiposDisponibles = (() => {
    if (!torneoSeleccionado?.equipos?.length) return equipos;
    const permitidos = new Set(torneoSeleccionado.equipos);
    return equipos.filter((equipo) => permitidos.has(equipo.equipoId));
  })();

  const equipoSeleccionado = equipos.find((equipo) => equipo.equipoId === selectedEquipoId);
  const nombreEquipoSeleccionado = equipoSeleccionado?.nombre?.trim().toLowerCase() || '';

  const standingsVisibles = selectedEquipoId
    ? standings.filter((item) => item.equipoId === selectedEquipoId)
    : standings;

  const goleadoresVisibles = selectedEquipoId
    ? goleadores.filter((item) => item.equipo.trim().toLowerCase() === nombreEquipoSeleccionado)
    : goleadores;

  const asistenciasVisibles = selectedEquipoId
    ? asistencias.filter((item) => item.equipo.trim().toLowerCase() === nombreEquipoSeleccionado)
    : asistencias;

  if (loading) {
    return <div className="py-20 text-center text-slate-400 dark:text-slate-500">Cargando estadísticas...</div>;
  }

  return (
    <div className="space-y-6 px-1 text-[#2a3547] dark:text-slate-100">
      <section className={panelClass}>
        <p className="text-xs font-semibold uppercase tracking-[0.22em] text-[#5d87ff]">Analitica del torneo</p>
        <h1 className="mt-2 text-3xl font-bold text-[#2a3547] dark:text-slate-100">Tabla, goleadores y asistencias</h1>
        <p className="mt-2 text-sm text-[#5a6a85] dark:text-slate-400">Consulta el rendimiento competitivo, la modalidad y el campeón del torneo seleccionado.</p>
      </section>

      <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
        <div />
        <div className="flex w-full flex-col gap-3 sm:w-auto sm:flex-row">
          <select className={`${inputClass} max-w-sm`} value={selectedTorneoId} onChange={(event) => setSelectedTorneoId(event.target.value)}>
            <option value="">Seleccionar torneo</option>
            {torneos.map((torneo) => (
              <option key={torneo.torneoId} value={torneo.torneoId}>{torneo.nombre}</option>
            ))}
          </select>
          <select className={`${inputClass} max-w-sm`} value={selectedEquipoId} onChange={(event) => setSelectedEquipoId(event.target.value)}>
            <option value="">Todos los equipos</option>
            {equiposDisponibles.map((equipo) => (
              <option key={equipo.equipoId} value={equipo.equipoId}>{equipo.nombre}</option>
            ))}
          </select>
        </div>
      </div>

      <div className="grid grid-cols-1 gap-4 xl:grid-cols-3">
        <StatsCard label="Equipos en tabla" value={standingsVisibles.length} emoji="📋" backgroundColor="bg-sky-500/15" />
        <StatsCard label="Top goleadores" value={goleadoresVisibles.length} emoji="🥅" backgroundColor="bg-emerald-500/15" />
        <StatsCard label="Top asistencias" value={asistenciasVisibles.length} emoji="🎯" backgroundColor="bg-violet-500/15" />
      </div>

      {torneoSeleccionado?.estado === 'FINALIZADO' && (
        <section className={panelClass}>
          <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <p className="text-xs font-semibold uppercase tracking-[0.22em] text-[#5d87ff]">Podio final</p>
              <h2 className="mt-2 text-2xl font-bold text-[#2a3547] dark:text-slate-100">Resultado oficial del torneo</h2>
            </div>
            <span className="rounded-full border border-amber-300 bg-amber-100 px-3 py-1 text-xs font-semibold text-amber-700 dark:border-amber-400/40 dark:bg-amber-500/10 dark:text-amber-300">FINALIZADO</span>
          </div>

          <div className="mt-5 grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4">
            <div className="rounded-2xl border border-[#e6edf8] bg-[#f8fbff] p-4 dark:border-slate-700 dark:bg-slate-800/80">
              <p className="text-xs uppercase tracking-[0.2em] text-[#7a8ca8] dark:text-slate-500">Modalidad</p>
              <p className="mt-2 text-2xl font-black text-[#2a3547] dark:text-slate-100">{torneoSeleccionado.modalidad === 'LIGA' ? 'Liga' : 'Eliminatoria'}</p>
            </div>
            <div className="rounded-2xl border border-[#e6edf8] bg-[#f8fbff] p-4 dark:border-slate-700 dark:bg-slate-800/80">
              <p className="text-xs uppercase tracking-[0.2em] text-[#7a8ca8] dark:text-slate-500">Campeón</p>
              <p className="mt-2 text-2xl font-black text-[#2a3547] dark:text-slate-100">{torneoSeleccionado.campeon || 'Pendiente'}</p>
            </div>
            <div className="rounded-2xl border border-[#e6edf8] bg-[#f8fbff] p-4 dark:border-slate-700 dark:bg-slate-800/80">
              <p className="text-xs uppercase tracking-[0.2em] text-[#7a8ca8] dark:text-slate-500">Subcampeón</p>
              <p className="mt-2 text-2xl font-black text-[#2a3547] dark:text-slate-100">{torneoSeleccionado.subcampeon || 'Pendiente'}</p>
            </div>
            <div className="rounded-2xl border border-[#e6edf8] bg-[#f8fbff] p-4 dark:border-slate-700 dark:bg-slate-800/80">
              <p className="text-xs uppercase tracking-[0.2em] text-[#7a8ca8] dark:text-slate-500">Tercer Lugar</p>
              <p className="mt-2 text-2xl font-black text-[#2a3547] dark:text-slate-100">{torneoSeleccionado.tercerLugar || 'Pendiente'}</p>
            </div>
          </div>
        </section>
      )}

      <div className="grid grid-cols-1 gap-6 xl:grid-cols-[1.35fr_0.95fr]">
        <section className={panelClass}>
          <h2 className="text-2xl font-bold text-[#2a3547] dark:text-slate-100">Tabla de posiciones</h2>
          <div className="mt-5 overflow-x-auto">
            <table className="w-full min-w-180 text-sm">
              <thead>
                <tr className="border-b border-[#e8eef8] text-left text-xs uppercase tracking-[0.2em] text-[#7a8ca8] dark:border-slate-800 dark:text-slate-500">
                  <th className="pb-3">Pos</th>
                  <th className="pb-3">Equipo</th>
                  <th className="pb-3 text-center">PJ</th>
                  <th className="pb-3 text-center">G</th>
                  <th className="pb-3 text-center">E</th>
                  <th className="pb-3 text-center">P</th>
                  <th className="pb-3 text-center">GF</th>
                  <th className="pb-3 text-center">GC</th>
                  <th className="pb-3 text-center">Pts</th>
                </tr>
              </thead>
              <tbody>
                {standingsVisibles.map((item, index) => (
                  <tr key={item.equipoId} className="border-b border-[#eef2f8] text-[#5a6a85] dark:border-slate-800 dark:text-slate-400">
                    <td className="py-3 font-bold text-[#2a3547] dark:text-slate-100">{index + 1}</td>
                    <td className="py-3 font-semibold text-[#2a3547] dark:text-slate-100">{item.nombre}</td>
                    <td className="py-3 text-center">{item.partidos}</td>
                    <td className="py-3 text-center text-emerald-500 dark:text-emerald-300">{item.ganados}</td>
                    <td className="py-3 text-center text-amber-500 dark:text-amber-300">{item.empatados}</td>
                    <td className="py-3 text-center text-rose-500 dark:text-rose-300">{item.perdidos}</td>
                    <td className="py-3 text-center">{item.golesAFavor}</td>
                    <td className="py-3 text-center">{item.golesEnContra}</td>
                    <td className="py-3 text-center text-lg font-black text-[#2a3547] dark:text-slate-100">{item.puntos}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>

        <div className="space-y-6">
          <section className={panelClass}>
            <h2 className="text-2xl font-bold text-[#2a3547] dark:text-slate-100">Mejor goleador</h2>
            <div className="mt-4 space-y-3">
              {goleadoresVisibles.map((item, index) => (
                <div key={item.jugadorId} className="flex items-center justify-between rounded-2xl border border-[#e6edf8] bg-[#f8fbff] px-4 py-3 dark:border-slate-700 dark:bg-slate-800/80">
                  <div>
                    <p className="font-semibold text-[#2a3547] dark:text-slate-100">{index + 1}. {item.nombre}</p>
                    <p className="text-xs text-slate-500 dark:text-slate-400">{item.equipo}</p>
                  </div>
                  <span className="text-xl font-black text-emerald-500 dark:text-emerald-300">{item.goles}</span>
                </div>
              ))}
            </div>
          </section>

          <section className={panelClass}>
            <h2 className="text-2xl font-bold text-[#2a3547] dark:text-slate-100">Top asistencias</h2>
            <div className="mt-4 space-y-3">
              {asistenciasVisibles.map((item, index) => (
                <div key={item.jugadorId} className="flex items-center justify-between rounded-2xl border border-[#e6edf8] bg-[#f8fbff] px-4 py-3 dark:border-slate-700 dark:bg-slate-800/80">
                  <div>
                    <p className="font-semibold text-[#2a3547] dark:text-slate-100">{index + 1}. {item.nombre}</p>
                    <p className="text-xs text-slate-500 dark:text-slate-400">{item.equipo}</p>
                  </div>
                  <span className="text-xl font-black text-sky-500 dark:text-sky-300">{item.asistencias}</span>
                </div>
              ))}
            </div>
          </section>
        </div>
      </div>
    </div>
  );
};

export default EstadisticasPage;
