import React, { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { equiposApi } from '../api/client';
import type { Equipo } from '../types';
import StatsCard from '../components/dashboard/StatsCard';

const panelClass = 'rounded-lg border border-[#dfe5ef] bg-white p-6 shadow-[0_8px_24px_rgba(133,146,173,0.14)] dark:border-slate-800 dark:bg-slate-900 dark:shadow-[0_16px_32px_rgba(2,6,23,0.35)]';
const inputClass = 'w-full rounded-xl border border-[#dfe5ef] bg-white px-4 py-3 text-sm text-[#2a3547] placeholder:text-[#91a1bc] focus:border-[#5d87ff] focus:outline-none dark:border-slate-700 dark:bg-slate-950 dark:text-slate-100 dark:placeholder:text-slate-500 dark:focus:border-sky-400';
const buttonClass = 'rounded-xl bg-[#5d87ff] px-4 py-2.5 text-sm font-bold text-white shadow-[0_9px_18px_rgba(93,135,255,0.25)] transition hover:bg-[#4b74e8]';

const EquiposPage: React.FC = () => {
  const [equipos, setEquipos] = useState<Equipo[]>([]);
  const [form, setForm] = useState({ nombre: '', ciudad: '', entrenador: '', anioFundacion: '' });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const fetchEquipos = async () => {
    try {
      setLoading(true);
      const data = await equiposApi.getAll();
      setEquipos(data);
    } catch (fetchError) {
      console.error(fetchError);
      toast.error('No se pudieron cargar los equipos');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void fetchEquipos();
  }, []);

  const handleCreate = async (event: React.FormEvent) => {
    event.preventDefault();

    try {
      setSaving(true);
      const response = await equiposApi.create({
        nombre: form.nombre,
        ciudad: form.ciudad,
        entrenador: form.entrenador,
        anioFundacion: Number(form.anioFundacion) || 0,
      });
      if (!response.success) {
        toast.error(response.message || 'No se pudo crear el equipo');
        return;
      }
      toast.success('Equipo creado correctamente');
      setForm({ nombre: '', ciudad: '', entrenador: '', anioFundacion: '' });
      await fetchEquipos();
    } catch (createError) {
      console.error(createError);
      toast.error('Error al crear el equipo');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <div className="py-20 text-center text-slate-400 dark:text-slate-500">Cargando equipos...</div>;
  }

  const totalJugadores = equipos.reduce((accumulator, equipo) => accumulator + (equipo.jugadores?.length || 0), 0);

  return (
    <div className="space-y-6 px-1 text-[#2a3547] dark:text-slate-100">
      <section className={panelClass}>
        <p className="text-xs font-semibold uppercase tracking-[0.22em] text-[#5d87ff]">Gestion de equipos</p>
        <h1 className="mt-2 text-3xl font-bold text-[#2a3547] dark:text-slate-100">Crear nuevos equipos</h1>
        <p className="mt-2 text-sm text-[#5a6a85] dark:text-slate-400">Registra clubes y prepara la base de participantes para los torneos.</p>
      </section>

      <div className="grid grid-cols-1 gap-4 xl:grid-cols-3">
        <StatsCard label="Total equipos" value={equipos.length} emoji="🛡️" backgroundColor="bg-sky-500/15" />
        <StatsCard label="Jugadores registrados" value={totalJugadores} emoji="👥" backgroundColor="bg-emerald-500/15" />
        <StatsCard label="Promedio por equipo" value={equipos.length ? (totalJugadores / equipos.length).toFixed(1) : '0'} emoji="📊" backgroundColor="bg-violet-500/15" />
      </div>

      <div className="grid grid-cols-1 gap-6 xl:grid-cols-[0.95fr_1.35fr]">
        <section className={panelClass}>
          <h2 className="text-2xl font-bold text-[#2a3547] dark:text-slate-100">Crear Equipo</h2>
          <p className="mt-1 text-sm text-slate-400 dark:text-slate-500">Registra un nuevo equipo antes de asignarlo a un torneo.</p>

          <form className="mt-5 space-y-4" onSubmit={handleCreate}>
            <input className={inputClass} placeholder="Nombre del equipo" value={form.nombre} onChange={(event) => setForm((current) => ({ ...current, nombre: event.target.value }))} required />
            <input className={inputClass} placeholder="Ciudad" value={form.ciudad} onChange={(event) => setForm((current) => ({ ...current, ciudad: event.target.value }))} required />
            <input className={inputClass} placeholder="Entrenador" value={form.entrenador} onChange={(event) => setForm((current) => ({ ...current, entrenador: event.target.value }))} required />
            <input className={inputClass} type="number" min="1900" placeholder="Año de fundación" value={form.anioFundacion} onChange={(event) => setForm((current) => ({ ...current, anioFundacion: event.target.value }))} />
            <button className={`${buttonClass} w-full`} disabled={saving} type="submit">{saving ? 'Guardando...' : 'Crear Equipo'}</button>
          </form>
        </section>

        <section className={panelClass}>
          <h2 className="text-2xl font-bold text-[#2a3547] dark:text-slate-100">Equipos registrados</h2>
          <div className="mt-5 grid grid-cols-1 gap-4 md:grid-cols-2">
            {equipos.map((equipo) => (
              <article key={equipo.equipoId} className="rounded-2xl border border-[#e6edf8] bg-[#f8fbff] p-4 dark:border-slate-700 dark:bg-slate-800/80">
                <div className="flex items-center gap-3">
                  <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-cyan-400/15 font-bold text-cyan-700 dark:text-cyan-200">{equipo.nombre.charAt(0).toUpperCase()}</div>
                  <div>
                    <h3 className="text-lg font-bold text-[#2a3547] dark:text-slate-100">{equipo.nombre}</h3>
                    <p className="text-sm text-slate-400 dark:text-slate-500">{equipo.ciudad}</p>
                  </div>
                </div>
                <div className="mt-4 space-y-2 text-sm text-slate-600 dark:text-slate-300">
                  <p><span className="text-slate-500 dark:text-slate-400">Entrenador:</span> {equipo.entrenador}</p>
                  <p><span className="text-slate-500 dark:text-slate-400">Fundación:</span> {equipo.anioFundacion || 'No indicada'}</p>
                  <p><span className="text-slate-500 dark:text-slate-400">Jugadores:</span> {equipo.jugadores?.length || 0}</p>
                </div>
              </article>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
};

export default EquiposPage;
