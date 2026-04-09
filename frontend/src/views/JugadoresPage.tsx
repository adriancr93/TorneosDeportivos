import React, { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { equiposApi, jugadoresApi } from '../api/client';
import type { Equipo, Jugador } from '../types';
import StatsCard from '../components/dashboard/StatsCard';

const panelClass = 'rounded-lg border border-[#dfe5ef] bg-white p-6 shadow-[0_8px_24px_rgba(133,146,173,0.14)]';
const inputClass = 'w-full rounded-xl border border-[#dfe5ef] bg-white px-4 py-3 text-sm text-[#2a3547] placeholder:text-[#91a1bc] focus:border-[#5d87ff] focus:outline-none';
const buttonClass = 'rounded-xl bg-[#5d87ff] px-4 py-2.5 text-sm font-bold text-white shadow-[0_9px_18px_rgba(93,135,255,0.25)] transition hover:bg-[#4b74e8]';

const JugadoresPage: React.FC = () => {
  const [equipos, setEquipos] = useState<Equipo[]>([]);
  const [jugadores, setJugadores] = useState<Jugador[]>([]);
  const [selectedEquipoId, setSelectedEquipoId] = useState('');
  const [form, setForm] = useState({ nombre: '', posicion: 'Delantero', numero: '', edad: '', equipoId: '' });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const fetchInitialData = async () => {
    const [equiposData, jugadoresData] = await Promise.all([equiposApi.getAll(), jugadoresApi.getAll()]);
    setEquipos(equiposData);
    setJugadores(jugadoresData);
    const equipoId = equiposData[0]?.equipoId || '';
    setSelectedEquipoId((current) => current || equipoId);
    setForm((current) => ({ ...current, equipoId: current.equipoId || equipoId }));
  };

  useEffect(() => {
    const bootstrap = async () => {
      try {
        setLoading(true);
        await fetchInitialData();
      } catch (fetchError) {
        console.error(fetchError);
        toast.error('No se pudieron cargar equipos y jugadores');
      } finally {
        setLoading(false);
      }
    };

    void bootstrap();
  }, []);

  const visibleJugadores = selectedEquipoId ? jugadores.filter((jugador) => jugador.equipoId === selectedEquipoId) : jugadores;

  const handleCreate = async (event: React.FormEvent) => {
    event.preventDefault();

    try {
      setSaving(true);
      const response = await jugadoresApi.create({
        nombre: form.nombre,
        posicion: form.posicion,
        numero: Number(form.numero),
        edad: Number(form.edad),
        equipoId: form.equipoId,
      });
      if (!response.success) {
        toast.error(response.message || 'No se pudo crear el jugador');
        return;
      }
      toast.success('Jugador registrado correctamente');
      setForm((current) => ({ ...current, nombre: '', numero: '', edad: '' }));
      setJugadores(await jugadoresApi.getAll());
    } catch (createError) {
      console.error(createError);
      toast.error('Error al crear el jugador');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <div className="py-20 text-center text-slate-400">Cargando jugadores...</div>;
  }

  return (
    <div className="space-y-6 px-1 text-[#2a3547]">
      <section className={panelClass}>
        <p className="text-xs font-semibold uppercase tracking-[0.22em] text-[#5d87ff]">Plantillas</p>
        <h1 className="mt-2 text-3xl font-bold text-[#2a3547]">Crear nuevos jugadores</h1>
        <p className="mt-2 text-sm text-[#5a6a85]">Administra fichas de jugadores por equipo, posicion y dorsal.</p>
      </section>

      <div className="grid grid-cols-1 gap-4 xl:grid-cols-3">
        <StatsCard label="Jugadores totales" value={jugadores.length} emoji="👤" backgroundColor="bg-emerald-500/15" />
        <StatsCard label="Equipos con plantilla" value={equipos.length} emoji="🛡️" backgroundColor="bg-sky-500/15" />
        <StatsCard label="Jugadores visibles" value={visibleJugadores.length} emoji="🎽" backgroundColor="bg-violet-500/15" />
      </div>

      <div className="grid grid-cols-1 gap-6 xl:grid-cols-[0.95fr_1.35fr]">
        <section className={panelClass}>
          <h2 className="text-2xl font-bold text-[#2a3547]">Crear Jugador</h2>
          <form className="mt-5 space-y-4" onSubmit={handleCreate}>
            <input className={inputClass} placeholder="Nombre completo" value={form.nombre} onChange={(event) => setForm((current) => ({ ...current, nombre: event.target.value }))} required />
            <select className={inputClass} value={form.posicion} onChange={(event) => setForm((current) => ({ ...current, posicion: event.target.value }))}>
              <option>Delantero</option>
              <option>Mediocampista</option>
              <option>Defensa</option>
              <option>Portero</option>
            </select>
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <input className={inputClass} type="number" min="1" placeholder="Número" value={form.numero} onChange={(event) => setForm((current) => ({ ...current, numero: event.target.value }))} required />
              <input className={inputClass} type="number" min="15" placeholder="Edad" value={form.edad} onChange={(event) => setForm((current) => ({ ...current, edad: event.target.value }))} />
            </div>
            <select className={inputClass} value={form.equipoId} onChange={(event) => setForm((current) => ({ ...current, equipoId: event.target.value }))} required>
              <option value="">Seleccionar equipo</option>
              {equipos.map((equipo) => (
                <option key={equipo.equipoId} value={equipo.equipoId}>{equipo.nombre}</option>
              ))}
            </select>
            <button className={`${buttonClass} w-full`} disabled={saving} type="submit">{saving ? 'Guardando...' : 'Crear Jugador'}</button>
          </form>
        </section>

        <section className={panelClass}>
          <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
            <div>
              <h2 className="text-2xl font-bold text-[#2a3547]">Plantilla por equipo</h2>
              <p className="text-sm text-slate-400">Filtra la lista según el equipo seleccionado.</p>
            </div>
            <select className={`${inputClass} max-w-xs`} value={selectedEquipoId} onChange={(event) => setSelectedEquipoId(event.target.value)}>
              <option value="">Todos los equipos</option>
              {equipos.map((equipo) => (
                <option key={equipo.equipoId} value={equipo.equipoId}>{equipo.nombre}</option>
              ))}
            </select>
          </div>

          <div className="mt-5 grid grid-cols-1 gap-4 md:grid-cols-2">
            {visibleJugadores.map((jugador) => {
              const equipo = equipos.find((item) => item.equipoId === jugador.equipoId);
              return (
                <article key={jugador.jugadorId} className="rounded-2xl border border-[#e6edf8] bg-[#f8fbff] p-4">
                  <div className="flex items-center justify-between gap-3">
                    <div>
                      <h3 className="text-lg font-bold text-[#2a3547]">{jugador.nombre}</h3>
                      <p className="text-sm text-slate-400">{equipo?.nombre || 'Sin equipo'}</p>
                    </div>
                    <span className="rounded-xl bg-cyan-400/15 px-3 py-1 text-xs font-bold text-cyan-200">#{jugador.numero}</span>
                  </div>
                  <div className="mt-3 text-sm text-slate-300">
                    <p><span className="text-slate-500">Posición:</span> {jugador.posicion}</p>
                    <p><span className="text-slate-500">Edad:</span> {jugador.edad || 'N/D'}</p>
                  </div>
                </article>
              );
            })}
          </div>
        </section>
      </div>
    </div>
  );
};

export default JugadoresPage;