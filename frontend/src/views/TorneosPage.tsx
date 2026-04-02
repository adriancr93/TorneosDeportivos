import React, { useEffect, useMemo, useState } from 'react';
import { toast } from 'react-toastify';
import { partidosApi, torneosApi, equiposApi } from '../api/client';
import type { Equipo, Torneo } from '../types';
import StatsCard from '../components/StatsCard';

const panelClass = 'rounded-lg border border-[#dfe5ef] bg-white p-6 shadow-[0_8px_24px_rgba(133,146,173,0.14)]';
const inputClass = 'w-full rounded-xl border border-[#dfe5ef] bg-white px-4 py-3 text-sm text-[#2a3547] placeholder:text-[#91a1bc] focus:border-[#5d87ff] focus:outline-none';
const buttonClass = 'rounded-xl bg-[#5d87ff] px-4 py-2.5 text-sm font-bold text-white shadow-[0_9px_18px_rgba(93,135,255,0.25)] transition hover:bg-[#4b74e8]';
const secondaryButtonClass = 'rounded-xl border border-[#dfe5ef] bg-white px-4 py-2.5 text-sm font-semibold text-[#2a3547] transition hover:bg-[#f3f7ff]';
const MIN_EQUIPOS = 4;

const TorneosPage: React.FC = () => {
  const [torneos, setTorneos] = useState<Torneo[]>([]);
  const [equipos, setEquipos] = useState<Equipo[]>([]);
  const [selectedEquipos, setSelectedEquipos] = useState<string[]>([]);
  const [selectedTorneoId, setSelectedTorneoId] = useState('');
  const [form, setForm] = useState({ nombre: '', sede: '', fechaInicio: '', fechaFin: '' });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [torneosData, equiposData] = await Promise.all([torneosApi.getAll(), equiposApi.getAll()]);
      setTorneos(torneosData);
      setEquipos(equiposData);
      setSelectedTorneoId((current) => current || torneosData[0]?.torneoId || '');
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

  const selectedTorneo = useMemo(() => torneos.find((torneo) => torneo.torneoId === selectedTorneoId) ?? null, [selectedTorneoId, torneos]);

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
      setForm({ nombre: '', sede: '', fechaInicio: '', fechaFin: '' });
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
    return <div className="py-20 text-center text-slate-400">Cargando torneos...</div>;
  }

  return (
    <div className="space-y-6 px-1 text-[#2a3547]">
      <section className={panelClass}>
        <p className="text-xs font-semibold uppercase tracking-[0.22em] text-[#5d87ff]">Centro de torneos</p>
        <h1 className="mt-2 text-3xl font-bold text-[#2a3547]">Crear y controlar torneos</h1>
        <p className="mt-2 text-sm text-[#5a6a85]">Define el torneo con un mínimo de cuatro equipos, genera el fixture aleatorio y ejecuta la simulación completa.</p>
      </section>

      <div className="grid grid-cols-1 gap-4 xl:grid-cols-3">
        <StatsCard label="Torneos creados" value={torneos.length} emoji="🏆" backgroundColor="bg-emerald-500/15" />
        <StatsCard label="Equipos disponibles" value={equipos.length} emoji="🛡️" backgroundColor="bg-sky-500/15" />
        <StatsCard label="Mínimo por torneo" value={MIN_EQUIPOS} emoji="📏" backgroundColor="bg-amber-500/15" />
      </div>

      <div className="grid grid-cols-1 gap-6 xl:grid-cols-[1.05fr_1.4fr]">
        <section className={panelClass}>
          <h2 className="text-2xl font-bold text-[#2a3547]">Crear Torneo</h2>
          <p className="mt-1 text-sm text-slate-400">Selecciona equipos antes de registrar el torneo.</p>

          <form className="mt-5 space-y-4" onSubmit={handleCreate}>
            <input className={inputClass} placeholder="Nombre del torneo" value={form.nombre} onChange={(event) => setForm((current) => ({ ...current, nombre: event.target.value }))} required />
            <input className={inputClass} placeholder="Sede o estadio principal" value={form.sede} onChange={(event) => setForm((current) => ({ ...current, sede: event.target.value }))} />
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <input className={inputClass} type="date" value={form.fechaInicio} onChange={(event) => setForm((current) => ({ ...current, fechaInicio: event.target.value }))} required />
              <input className={inputClass} type="date" value={form.fechaFin} onChange={(event) => setForm((current) => ({ ...current, fechaFin: event.target.value }))} required />
            </div>

            <div className="rounded-2xl border border-[#e6edf8] bg-[#f8fbff] p-4">
              <div className="mb-3 flex items-center justify-between">
                <p className="text-sm font-semibold text-[#2a3547]">Equipos participantes</p>
                <span className="text-xs text-slate-500">{selectedEquipos.length} seleccionados</span>
              </div>
              <div className="grid max-h-72 grid-cols-1 gap-2 overflow-auto pr-1 sm:grid-cols-2">
                {equipos.map((equipo) => (
                  <label key={equipo.equipoId} className="flex items-center gap-3 rounded-xl border border-[#e6edf8] bg-white px-3 py-2 text-sm text-[#2a3547]">
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
              <h2 className="text-2xl font-bold text-[#2a3547]">Operación del torneo</h2>
              <p className="text-sm text-slate-400">Selecciona un torneo para generar el fixture o simular toda la competencia.</p>
            </div>
            <select className={`${inputClass} max-w-xs`} value={selectedTorneoId} onChange={(event) => setSelectedTorneoId(event.target.value)}>
              <option value="">Seleccionar torneo</option>
              {torneos.map((torneo) => (
                <option key={torneo.torneoId} value={torneo.torneoId}>{torneo.nombre}</option>
              ))}
            </select>
          </div>

          {selectedTorneo ? (
            <div className="mt-5 grid grid-cols-1 gap-4 lg:grid-cols-3">
              <div className="rounded-2xl border border-[#e6edf8] bg-[#f8fbff] p-4"><p className="text-xs uppercase tracking-[0.2em] text-[#7a8ca8]">Estado</p><p className="mt-3 text-2xl font-bold text-[#2a3547]">{selectedTorneo.estado}</p></div>
              <div className="rounded-2xl border border-[#e6edf8] bg-[#f8fbff] p-4"><p className="text-xs uppercase tracking-[0.2em] text-[#7a8ca8]">Equipos</p><p className="mt-3 text-2xl font-bold text-[#2a3547]">{selectedTorneo.cantidadEquipos ?? selectedTorneo.equipos?.length ?? 0}</p></div>
              <div className="rounded-2xl border border-[#e6edf8] bg-[#f8fbff] p-4"><p className="text-xs uppercase tracking-[0.2em] text-[#7a8ca8]">Rango</p><p className="mt-3 text-sm font-semibold text-[#2a3547]">{selectedTorneo.fechaInicio} al {selectedTorneo.fechaFin}</p></div>
            </div>
          ) : (
            <div className="mt-5 rounded-2xl border border-dashed border-[#d5e0f4] bg-[#f8fbff] p-5 text-sm text-[#5a6a85]">Selecciona un torneo para habilitar las acciones.</div>
          )}

          <div className="mt-5 flex flex-wrap gap-3">
            <button className={secondaryButtonClass} disabled={!selectedTorneoId || saving} type="button" onClick={handleGenerate}>Generar Partidos</button>
            <button className={buttonClass} disabled={!selectedTorneoId || saving} type="button" onClick={handleSimulate}>Simular Torneo</button>
          </div>

          <div className="mt-6 grid grid-cols-1 gap-4 xl:grid-cols-2">
            {torneos.map((torneo) => (
              <article key={torneo.torneoId} className={`rounded-2xl border p-4 transition ${selectedTorneoId === torneo.torneoId ? 'border-[#c7d7fb] bg-[#eef3ff]' : 'border-[#e6edf8] bg-white'}`}>
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <h3 className="text-lg font-bold text-[#2a3547]">{torneo.nombre}</h3>
                    <p className="text-sm text-slate-400">{torneo.sede || 'Sede por definir'}</p>
                  </div>
                  <span className="rounded-full border border-[#dfe5ef] px-3 py-1 text-xs font-semibold text-[#5a6a85]">{torneo.estado}</span>
                </div>
                <div className="mt-4 grid grid-cols-2 gap-3 text-sm text-slate-400">
                  <div><p className="text-xs uppercase tracking-[0.2em] text-slate-500">Inicio</p><p className="mt-1 text-[#2a3547]">{torneo.fechaInicio}</p></div>
                  <div><p className="text-xs uppercase tracking-[0.2em] text-slate-500">Equipos</p><p className="mt-1 text-[#2a3547]">{torneo.cantidadEquipos ?? torneo.equipos?.length ?? 0}</p></div>
                </div>
              </article>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
};

export default TorneosPage;
