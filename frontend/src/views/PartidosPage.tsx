import React, { useEffect, useMemo, useState } from 'react';
import { toast } from 'react-toastify';
import { equiposApi, partidosApi, torneosApi } from '../api/client';
import type { Equipo, Partido, Torneo } from '../types';
import StatsCard from '../components/dashboard/StatsCard';

const panelClass = 'rounded-lg border border-[#dfe5ef] bg-white p-6 shadow-[0_8px_24px_rgba(133,146,173,0.14)]';
const inputClass = 'w-full rounded-xl border border-[#dfe5ef] bg-white px-4 py-3 text-sm text-[#2a3547] focus:border-[#5d87ff] focus:outline-none';
const secondaryButtonClass = 'rounded-xl border border-[#dfe5ef] bg-white px-4 py-2.5 text-sm font-semibold text-[#2a3547] transition hover:bg-[#f3f7ff]';
const primaryButtonClass = 'rounded-xl bg-[#5d87ff] px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-[#4b74e8] disabled:opacity-60';

const PartidosPage: React.FC = () => {
  const [torneos, setTorneos] = useState<Torneo[]>([]);
  const [equipos, setEquipos] = useState<Equipo[]>([]);
  const [partidos, setPartidos] = useState<Partido[]>([]);
  const [selectedTorneoId, setSelectedTorneoId] = useState('');
  const [selectedEquipoId, setSelectedEquipoId] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const fetchTorneos = async () => {
    const [torneosData, equiposData] = await Promise.all([torneosApi.getAll(), equiposApi.getAll()]);
    setTorneos(torneosData);
    setEquipos(equiposData);
    setSelectedTorneoId((current) => current || torneosData[0]?.torneoId || '');
    return torneosData;
  };

  const fetchPartidos = async (torneoId: string) => {
    if (!torneoId) {
      setPartidos([]);
      return;
    }
    const data = await partidosApi.getAll(torneoId);
    setPartidos(data);
  };

  useEffect(() => {
    const bootstrap = async () => {
      try {
        setLoading(true);
        const data = await fetchTorneos();
        if (data[0]?.torneoId) {
          await fetchPartidos(data[0].torneoId);
        }
      } catch (fetchError) {
        console.error(fetchError);
        toast.error('No se pudieron cargar torneos y partidos');
      } finally {
        setLoading(false);
      }
    };

    void bootstrap();
  }, []);

  useEffect(() => {
    if (!selectedTorneoId) return;

    setSelectedEquipoId('');
    void fetchPartidos(selectedTorneoId).catch((fetchError) => {
      console.error(fetchError);
      toast.error('No se pudieron cargar los partidos del torneo');
    });
  }, [selectedTorneoId]);

  const torneoSeleccionado = torneos.find((torneo) => torneo.torneoId === selectedTorneoId);
  const equiposDisponibles = useMemo(() => {
    if (!torneoSeleccionado?.equipos?.length) return equipos;
    const permitidos = new Set(torneoSeleccionado.equipos);
    return equipos.filter((equipo) => permitidos.has(equipo.equipoId));
  }, [torneoSeleccionado, equipos]);

  const partidosVisibles = useMemo(() => {
    if (!selectedEquipoId) return partidos;
    return partidos.filter((partido) => partido.equipoLocalId === selectedEquipoId || partido.equipoVisitanteId === selectedEquipoId);
  }, [partidos, selectedEquipoId]);

  const totalGoles = useMemo(() => partidosVisibles.reduce((total, partido) => total + Math.max(partido.golesLocal, 0) + Math.max(partido.golesVisitante, 0), 0), [partidosVisibles]);
  const jugados = useMemo(() => partidosVisibles.filter((partido) => partido.estado === 'JUGADO').length, [partidosVisibles]);
  const pendientes = partidosVisibles.length - jugados;

  const ROUND_ORDER = ['Octavos de Final', 'Cuartos de Final', 'Semifinal', 'Final'];
  const partidosPorRonda = useMemo(() => {
    const groups: Record<string, Partido[]> = {};
    for (const partido of partidosVisibles) {
      const ronda = partido.ronda || partido.fecha || 'Sin ronda';
      if (!groups[ronda]) groups[ronda] = [];
      groups[ronda].push(partido);
    }
    // Sort rounds by tournament progression order
    const sortedKeys = Object.keys(groups).sort((a, b) => {
      const idxA = ROUND_ORDER.indexOf(a);
      const idxB = ROUND_ORDER.indexOf(b);
      return (idxA === -1 ? 999 : idxA) - (idxB === -1 ? 999 : idxB);
    });
    return sortedKeys.map((ronda) => ({ ronda, partidos: groups[ronda] }));
  }, [partidosVisibles]);

  const handleGenerate = async () => {
    if (!selectedTorneoId) return;
    setSaving(true);
    try {
      const response = await partidosApi.generate(selectedTorneoId);
      if (!response.success) {
        toast.error(response.message || 'No se pudo generar el fixture');
        return;
      }
      toast.success('Fixture generado correctamente');
      await fetchPartidos(selectedTorneoId);
    } catch (actionError) {
      console.error(actionError);
      toast.error('Error al generar partidos');
    } finally {
      setSaving(false);
    }
  };

  const handleSimulate = async () => {
    if (!selectedTorneoId) return;
    setSaving(true);
    try {
      const response = await torneosApi.simulate(selectedTorneoId);
      if (!response.success) {
        toast.error(response.message || 'No se pudo simular el torneo');
        return;
      }
      toast.success('Torneo simulado correctamente');
      await fetchPartidos(selectedTorneoId);
    } catch (actionError) {
      console.error(actionError);
      toast.error('Error al simular partidos');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <div className="py-20 text-center text-slate-400">Cargando partidos...</div>;
  }

  return (
    <div className="space-y-6 px-1 text-[#2a3547]">
      <section className={panelClass}>
        <p className="text-xs font-semibold uppercase tracking-[0.22em] text-[#5d87ff]">Calendario del torneo</p>
        <h1 className="mt-2 text-3xl font-bold text-[#2a3547]">Partidos dependientes del torneo</h1>
        <p className="mt-2 text-sm text-[#5a6a85]">Visualiza cruces programados, genera fixture y monitorea resultados.</p>
      </section>

      <div className="grid grid-cols-1 gap-4 xl:grid-cols-3">
        <StatsCard label="Partidos visibles" value={partidosVisibles.length} emoji="⚽" backgroundColor="bg-sky-500/15" />
        <StatsCard label="Partidos jugados" value={jugados} emoji="✅" backgroundColor="bg-emerald-500/15" />
        <StatsCard label="Goles del torneo" value={totalGoles} emoji="🔥" backgroundColor="bg-rose-500/15" />
      </div>

      <section className={panelClass}>
        <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
          <div>
            <h2 className="text-2xl font-bold text-[#2a3547]">Torneo cargado</h2>
            <p className="text-sm text-slate-400">La lista y las acciones cambian según el torneo seleccionado.</p>
          </div>

          <div className="flex flex-col gap-3 sm:flex-row">
            <select className={`${inputClass} min-w-64`} value={selectedTorneoId} onChange={(event) => setSelectedTorneoId(event.target.value)}>
              <option value="">Seleccionar torneo</option>
              {torneos.map((torneo) => (
                <option key={torneo.torneoId} value={torneo.torneoId}>{torneo.nombre}</option>
              ))}
            </select>
            <select className={`${inputClass} min-w-64`} value={selectedEquipoId} onChange={(event) => setSelectedEquipoId(event.target.value)}>
              <option value="">Todos los equipos</option>
              {equiposDisponibles.map((equipo) => (
                <option key={equipo.equipoId} value={equipo.equipoId}>{equipo.nombre}</option>
              ))}
            </select>
            <button className={secondaryButtonClass} type="button" disabled={!selectedTorneoId || saving} onClick={handleGenerate}>Generar Fixture</button>
            <button className={primaryButtonClass} type="button" disabled={!selectedTorneoId || saving} onClick={handleSimulate}>Simular Torneo</button>
          </div>
        </div>

        {partidosPorRonda.length === 0 ? (
          <div className="mt-6 rounded-2xl border border-dashed border-[#d5e0f4] bg-[#f8fbff] p-6 text-sm text-[#5a6a85]">
            Este torneo todavía no tiene partidos. Usa &quot;Generar Fixture&quot; o simúlalo desde Torneos.
          </div>
        ) : (
          partidosPorRonda.map(({ ronda, partidos: partidosRonda }) => {
            const isFinal = ronda === 'Final';
            const isSemifinal = ronda === 'Semifinal';
            const badgeColor = isFinal ? 'bg-amber-100 text-amber-700 border-amber-300' : isSemifinal ? 'bg-violet-100 text-violet-700 border-violet-300' : 'bg-blue-100 text-blue-700 border-blue-300';
            return (
              <div key={ronda} className="mt-6">
                <span className={`inline-block rounded-full border px-3 py-1 text-xs font-bold uppercase tracking-widest ${badgeColor}`}>{ronda}</span>
                <div className="mt-3 grid grid-cols-1 gap-4 xl:grid-cols-2">
                  {partidosRonda.map((partido) => (
                    <article key={partido.partidoId} className="rounded-2xl border border-[#e6edf8] bg-[#f8fbff] p-4">
                      <div className="flex items-center justify-between gap-3">
                        <span className="rounded-full border border-white/10 px-3 py-1 text-xs font-semibold text-slate-300">{partido.estado}</span>
                        <span className="text-xs text-slate-500">{partido.fecha}</span>
                      </div>
                      <div className="mt-4 grid grid-cols-[1fr_auto_1fr] items-center gap-3">
                        <div>
                          <p className="text-lg font-bold text-[#2a3547]">{partido.equipoLocal}</p>
                          <p className="text-xs text-slate-500">Local</p>
                        </div>
                        <div className="text-center">
                          {partido.estado === 'JUGADO' ? <p className="text-3xl font-black text-[#2a3547]">{partido.golesLocal} - {partido.golesVisitante}</p> : <p className="text-sm font-semibold text-amber-500">Pendiente</p>}
                        </div>
                        <div className="text-right">
                          <p className="text-lg font-bold text-[#2a3547]">{partido.equipoVisitante}</p>
                          <p className="text-xs text-slate-500">Visitante</p>
                        </div>
                      </div>
                    </article>
                  ))}
                </div>
              </div>
            );
          })
        )}
      </section>

      <section className={panelClass}>
        <h2 className="text-2xl font-bold text-[#2a3547]">Resumen rápido</h2>
        <div className="mt-4 grid grid-cols-1 gap-4 md:grid-cols-3">
          <div className="rounded-2xl border border-[#e6edf8] bg-[#f8fbff] p-4"><p className="text-xs uppercase tracking-[0.2em] text-[#7a8ca8]">Pendientes</p><p className="mt-2 text-3xl font-black text-[#2a3547]">{pendientes}</p></div>
          <div className="rounded-2xl border border-[#e6edf8] bg-[#f8fbff] p-4"><p className="text-xs uppercase tracking-[0.2em] text-[#7a8ca8]">Jugados</p><p className="mt-2 text-3xl font-black text-[#2a3547]">{jugados}</p></div>
          <div className="rounded-2xl border border-[#e6edf8] bg-[#f8fbff] p-4"><p className="text-xs uppercase tracking-[0.2em] text-[#7a8ca8]">Goles</p><p className="mt-2 text-3xl font-black text-[#2a3547]">{totalGoles}</p></div>
        </div>
      </section>
    </div>
  );
};

export default PartidosPage;
