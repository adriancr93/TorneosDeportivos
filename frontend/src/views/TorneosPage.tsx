import React, { useEffect, useMemo, useState } from 'react';
import { toast } from 'react-toastify';
import { partidosApi, torneosApi, equiposApi } from '../api/client';
import type { Equipo, Torneo } from '../types';
import StatsCard from '../components/dashboard/StatsCard';

const panelClass = 'rounded-lg border border-[#dfe5ef] bg-white p-6 shadow-[0_8px_24px_rgba(133,146,173,0.14)] dark:border-slate-800 dark:bg-slate-900 dark:shadow-[0_16px_32px_rgba(2,6,23,0.35)]';
const inputClass = 'w-full rounded-xl border border-[#dfe5ef] bg-white px-4 py-3 text-sm text-[#2a3547] placeholder:text-[#91a1bc] focus:border-[#5d87ff] focus:outline-none dark:border-slate-700 dark:bg-slate-950 dark:text-slate-100 dark:placeholder:text-slate-500 dark:focus:border-sky-400';
const buttonClass = 'rounded-xl bg-[#5d87ff] px-4 py-2.5 text-sm font-bold text-white shadow-[0_9px_18px_rgba(93,135,255,0.25)] transition hover:bg-[#4b74e8]';
const secondaryButtonClass = 'rounded-xl border border-[#dfe5ef] bg-white px-4 py-2.5 text-sm font-semibold text-[#2a3547] transition hover:bg-[#f3f7ff] dark:border-slate-700 dark:bg-slate-950 dark:text-slate-100 dark:hover:bg-slate-800';
const MIN_EQUIPOS = 4;

const TorneosPage: React.FC = () => {
  const [torneos, setTorneos] = useState<Torneo[]>([]);
  const [equipos, setEquipos] = useState<Equipo[]>([]);
  const [selectedEquipos, setSelectedEquipos] = useState<string[]>([]);
  const [selectedTorneoId, setSelectedTorneoId] = useState('');
  const [form, setForm] = useState({ nombre: '', sede: '', fechaInicio: '', fechaFin: '', modalidad: 'ELIMINATORIA' as 'ELIMINATORIA' | 'LIGA' });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [torneosData, equiposData] = await Promise.all([torneosApi.getAll(), equiposApi.getAll()]);
      setTorneos(torneosData);
      setEquipos(equiposData);
      const torneosEnGestion = torneosData.filter((torneo) => torneo.estado !== 'FINALIZADO');
      setSelectedTorneoId((current) => torneosEnGestion.some((torneo) => torneo.torneoId === current) ? current : (torneosEnGestion[0]?.torneoId || ''));
    } catch (fetchError) {
      console.error(fetchError);
      toast.error('No se pudieron cargar los torneos y equipos');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void fetchData();
  }, []);

  const torneosEnGestion = useMemo(() => torneos.filter((torneo) => torneo.estado !== 'FINALIZADO'), [torneos]);
  const torneosFinalizados = useMemo(() => torneos.filter((torneo) => torneo.estado === 'FINALIZADO'), [torneos]);
  const selectedTorneo = useMemo(() => torneosEnGestion.find((torneo) => torneo.torneoId === selectedTorneoId) ?? null, [selectedTorneoId, torneosEnGestion]);

  const handleCheckbox = (equipoId: string) => {
    setSelectedEquipos((current) => current.includes(equipoId) ? current.filter((id) => id !== equipoId) : [...current, equipoId]);
  };

  const handleCreate = async (event: React.FormEvent) => {
    event.preventDefault();

    if (selectedEquipos.length < MIN_EQUIPOS) {
      toast.error(`Selecciona al menos ${MIN_EQUIPOS} equipos para crear el torneo`);
      return;
    }

    try {
      setSaving(true);
      const response = await torneosApi.create({ ...form, equipoIds: selectedEquipos });
      if (!response.success) {
        toast.error(response.message || 'No se pudo crear el torneo');
        return;
      }
      toast.success('Torneo creado correctamente');
      setForm({ nombre: '', sede: '', fechaInicio: '', fechaFin: '', modalidad: 'ELIMINATORIA' });
      setSelectedEquipos([]);
      await fetchData();
    } catch (createError) {
      console.error(createError);
      toast.error('Error al crear el torneo');
    } finally {
      setSaving(false);
    }
  };

  const handleGenerate = async () => {
    if (!selectedTorneoId) return;
    try {
      setSaving(true);
      const response = await partidosApi.generate(selectedTorneoId);
      if (!response.success) {
        toast.error(response.message || 'No se pudieron generar los partidos');
        return;
      }
      toast.success('Fixture generado para el torneo seleccionado');
      await fetchData();
    } catch (actionError) {
      console.error(actionError);
      toast.error('Error al generar partidos');
    } finally {
      setSaving(false);
    }
  };

  const handleSimulate = async () => {
    if (!selectedTorneoId) return;
    try {
      setSaving(true);
      const response = await torneosApi.simulate(selectedTorneoId);
      if (!response.success) {
        toast.error(response.message || 'No se pudo simular el torneo');
        return;
      }
      toast.success('Torneo simulado con resultados aleatorios, goleadores y asistencias');
      await fetchData();
    } catch (actionError) {
      console.error(actionError);
      toast.error('Error al simular el torneo');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <div className="py-20 text-center text-slate-400 dark:text-slate-500">Cargando torneos...</div>;
  }

  return (
    <div className="space-y-6 px-1 text-[#2a3547] dark:text-slate-100">
      <section className={panelClass}>
        <p className="text-xs font-semibold uppercase tracking-[0.22em] text-[#5d87ff]">Centro de torneos</p>
        <h1 className="mt-2 text-3xl font-bold text-[#2a3547] dark:text-slate-100">Crear y controlar torneos</h1>
        <p className="mt-2 text-sm text-[#5a6a85] dark:text-slate-400">Define el torneo como liga o eliminación directa, genera el fixture y ejecuta la simulación completa.</p>
      </section>

      <div className="grid grid-cols-1 gap-4 xl:grid-cols-3">
        <StatsCard label="Torneos en gestión" value={torneosEnGestion.length} emoji="🏆" backgroundColor="bg-emerald-500/15" />
        <StatsCard label="Torneos finalizados" value={torneosFinalizados.length} emoji="🥇" backgroundColor="bg-amber-500/15" />
        <StatsCard label="Equipos disponibles" value={equipos.length} emoji="🛡️" backgroundColor="bg-sky-500/15" />
      </div>

      <div className="grid grid-cols-1 gap-6 xl:grid-cols-[1.05fr_1.4fr]">
        <section className={panelClass}>
          <h2 className="text-2xl font-bold text-[#2a3547] dark:text-slate-100">Crear Torneo</h2>
          <p className="mt-1 text-sm text-slate-400 dark:text-slate-500">Selecciona equipos antes de registrar el torneo.</p>

          <form className="mt-5 space-y-4" onSubmit={handleCreate}>
            <input className={inputClass} placeholder="Nombre del torneo" value={form.nombre} onChange={(event) => setForm((current) => ({ ...current, nombre: event.target.value }))} required />
            <input className={inputClass} placeholder="Sede o estadio principal" value={form.sede} onChange={(event) => setForm((current) => ({ ...current, sede: event.target.value }))} />
            <select className={inputClass} value={form.modalidad} onChange={(event) => setForm((current) => ({ ...current, modalidad: event.target.value as 'ELIMINATORIA' | 'LIGA' }))}>
              <option value="ELIMINATORIA">Eliminatoria directa</option>
              <option value="LIGA">Liga</option>
            </select>
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <input className={inputClass} type="date" value={form.fechaInicio} onChange={(event) => setForm((current) => ({ ...current, fechaInicio: event.target.value }))} required />
              <input className={inputClass} type="date" value={form.fechaFin} onChange={(event) => setForm((current) => ({ ...current, fechaFin: event.target.value }))} required />
            </div>

            <div className="rounded-2xl border border-[#e6edf8] bg-[#f8fbff] p-4 dark:border-slate-700 dark:bg-slate-800/80">
              <div className="mb-3 flex items-center justify-between">
                <p className="text-sm font-semibold text-[#2a3547] dark:text-slate-100">Equipos participantes</p>
                <span className="text-xs text-slate-500 dark:text-slate-400">{selectedEquipos.length} seleccionados</span>
              </div>
              <div className="grid max-h-72 grid-cols-1 gap-2 overflow-auto pr-1 sm:grid-cols-2">
                {equipos.map((equipo) => (
                  <label key={equipo.equipoId} className="flex items-center gap-3 rounded-xl border border-[#e6edf8] bg-white px-3 py-2 text-sm text-[#2a3547] dark:border-slate-700 dark:bg-slate-900 dark:text-slate-100">
                    <input type="checkbox" checked={selectedEquipos.includes(equipo.equipoId)} onChange={() => handleCheckbox(equipo.equipoId)} className="accent-cyan-400" />
                    <span>{equipo.nombre}</span>
                  </label>
                ))}
              </div>
            </div>

            <button className={`${buttonClass} w-full`} disabled={saving} type="submit">{saving ? 'Guardando...' : 'Crear Torneo'}</button>
          </form>
        </section>

        <section className={panelClass}>
          <div className="flex flex-col gap-3 lg:flex-row lg:items-center lg:justify-between">
            <div>
              <h2 className="text-2xl font-bold text-[#2a3547] dark:text-slate-100">Operación del torneo</h2>
              <p className="text-sm text-slate-400 dark:text-slate-500">Selecciona un torneo para generar el fixture o simular toda la competencia.</p>
            </div>
            <select className={`${inputClass} max-w-xs`} value={selectedTorneoId} onChange={(event) => setSelectedTorneoId(event.target.value)}>
              <option value="">Seleccionar torneo</option>
              {torneosEnGestion.map((torneo) => (
                <option key={torneo.torneoId} value={torneo.torneoId}>{torneo.nombre}</option>
              ))}
            </select>
          </div>

          {selectedTorneo ? (
            <div className="mt-5 grid grid-cols-1 gap-4 lg:grid-cols-3">
              <div className="rounded-2xl border border-[#e6edf8] bg-[#f8fbff] p-4 dark:border-slate-700 dark:bg-slate-800/80"><p className="text-xs uppercase tracking-[0.2em] text-[#7a8ca8] dark:text-slate-500">Estado</p><p className="mt-3 text-2xl font-bold text-[#2a3547] dark:text-slate-100">{selectedTorneo.estado}</p></div>
              <div className="rounded-2xl border border-[#e6edf8] bg-[#f8fbff] p-4 dark:border-slate-700 dark:bg-slate-800/80"><p className="text-xs uppercase tracking-[0.2em] text-[#7a8ca8] dark:text-slate-500">Modalidad</p><p className="mt-3 text-2xl font-bold text-[#2a3547] dark:text-slate-100">{selectedTorneo.modalidad === 'LIGA' ? 'Liga' : 'Eliminatoria'}</p></div>
              <div className="rounded-2xl border border-[#e6edf8] bg-[#f8fbff] p-4 dark:border-slate-700 dark:bg-slate-800/80"><p className="text-xs uppercase tracking-[0.2em] text-[#7a8ca8] dark:text-slate-500">Equipos</p><p className="mt-3 text-2xl font-bold text-[#2a3547] dark:text-slate-100">{selectedTorneo.cantidadEquipos ?? selectedTorneo.equipos?.length ?? 0}</p></div>
            </div>
          ) : (
            <div className="mt-5 rounded-2xl border border-dashed border-[#d5e0f4] bg-[#f8fbff] p-5 text-sm text-[#5a6a85] dark:border-slate-700 dark:bg-slate-800/80 dark:text-slate-400">Selecciona un torneo para habilitar las acciones.</div>
          )}

          <div className="mt-5 flex flex-wrap gap-3">
            <button className={secondaryButtonClass} disabled={!selectedTorneoId || saving} type="button" onClick={handleGenerate}>Generar Partidos</button>
            <button className={buttonClass} disabled={!selectedTorneoId || saving} type="button" onClick={handleSimulate}>Simular Torneo</button>
          </div>

          <div className="mt-6 grid grid-cols-1 gap-4 xl:grid-cols-2">
            {torneosEnGestion.map((torneo) => (
              <article key={torneo.torneoId} className={`rounded-2xl border p-4 transition ${selectedTorneoId === torneo.torneoId ? 'border-[#c7d7fb] bg-[#eef3ff] dark:border-sky-500/30 dark:bg-slate-800' : 'border-[#e6edf8] bg-white dark:border-slate-700 dark:bg-slate-900'}`}>
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <h3 className="text-lg font-bold text-[#2a3547] dark:text-slate-100">{torneo.nombre}</h3>
                    <p className="text-sm text-slate-400 dark:text-slate-500">{torneo.sede || 'Sede por definir'}</p>
                  </div>
                  <span className="rounded-full border border-[#dfe5ef] px-3 py-1 text-xs font-semibold text-[#5a6a85] dark:border-slate-700 dark:text-slate-400">{torneo.estado}</span>
                </div>
                <div className="mt-4 grid grid-cols-2 gap-3 text-sm text-slate-400 dark:text-slate-500">
                  <div><p className="text-xs uppercase tracking-[0.2em] text-slate-500 dark:text-slate-500">Modalidad</p><p className="mt-1 text-[#2a3547] dark:text-slate-100">{torneo.modalidad === 'LIGA' ? 'Liga' : 'Eliminatoria'}</p></div>
                  <div><p className="text-xs uppercase tracking-[0.2em] text-slate-500 dark:text-slate-500">Equipos</p><p className="mt-1 text-[#2a3547] dark:text-slate-100">{torneo.cantidadEquipos ?? torneo.equipos?.length ?? 0}</p></div>
                </div>
              </article>
            ))}
          </div>
        </section>
      </div>

      <section className={panelClass}>
        <div className="flex flex-col gap-2 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <h2 className="text-2xl font-bold text-[#2a3547] dark:text-slate-100">Historial de torneos finalizados</h2>
            <p className="text-sm text-slate-400 dark:text-slate-500">Los torneos terminados salen de gestión y quedan como consulta con su podio final.</p>
          </div>
          <span className="text-sm font-semibold text-[#5d87ff]">Consulta completa en Estadísticas</span>
        </div>

        {torneosFinalizados.length === 0 ? (
          <div className="mt-5 rounded-2xl border border-dashed border-[#d5e0f4] bg-[#f8fbff] p-5 text-sm text-[#5a6a85] dark:border-slate-700 dark:bg-slate-800/80 dark:text-slate-400">
            Todavía no hay torneos finalizados.
          </div>
        ) : (
          <div className="mt-5 grid grid-cols-1 gap-4 xl:grid-cols-2">
            {torneosFinalizados.map((torneo) => (
              <article key={torneo.torneoId} className="rounded-2xl border border-[#e6edf8] bg-[#f8fbff] p-5 dark:border-slate-700 dark:bg-slate-800/80">
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <h3 className="text-lg font-bold text-[#2a3547] dark:text-slate-100">{torneo.nombre}</h3>
                    <p className="text-sm text-slate-400 dark:text-slate-500">{torneo.sede || 'Sede por definir'}</p>
                  </div>
                  <span className="rounded-full border border-amber-300 bg-amber-100 px-3 py-1 text-xs font-semibold text-amber-700 dark:border-amber-400/40 dark:bg-amber-500/10 dark:text-amber-300">FINALIZADO</span>
                </div>

                <div className="mt-4 grid grid-cols-1 gap-3 sm:grid-cols-2 xl:grid-cols-4">
                  <div className="rounded-xl border border-[#e6edf8] bg-white p-3 dark:border-slate-700 dark:bg-slate-900">
                    <p className="text-xs uppercase tracking-[0.2em] text-[#7a8ca8] dark:text-slate-500">Modalidad</p>
                    <p className="mt-2 text-base font-bold text-[#2a3547] dark:text-slate-100">{torneo.modalidad === 'LIGA' ? 'Liga' : 'Eliminatoria'}</p>
                  </div>
                  <div className="rounded-xl border border-[#e6edf8] bg-white p-3 dark:border-slate-700 dark:bg-slate-900">
                    <p className="text-xs uppercase tracking-[0.2em] text-[#7a8ca8] dark:text-slate-500">Campeón</p>
                    <p className="mt-2 text-base font-bold text-[#2a3547] dark:text-slate-100">{torneo.campeon || 'Pendiente'}</p>
                  </div>
                  <div className="rounded-xl border border-[#e6edf8] bg-white p-3 dark:border-slate-700 dark:bg-slate-900">
                    <p className="text-xs uppercase tracking-[0.2em] text-[#7a8ca8] dark:text-slate-500">Subcampeón</p>
                    <p className="mt-2 text-base font-bold text-[#2a3547] dark:text-slate-100">{torneo.subcampeon || 'Pendiente'}</p>
                  </div>
                  <div className="rounded-xl border border-[#e6edf8] bg-white p-3 dark:border-slate-700 dark:bg-slate-900">
                    <p className="text-xs uppercase tracking-[0.2em] text-[#7a8ca8] dark:text-slate-500">Tercer Lugar</p>
                    <p className="mt-2 text-base font-bold text-[#2a3547] dark:text-slate-100">{torneo.tercerLugar || 'Pendiente'}</p>
                  </div>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>
    </div>
  );
};

export default TorneosPage;
