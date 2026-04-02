import React from 'react';

interface DashboardHeroProps {
  eyebrow: string;
  title: string;
  description: string;
  imageSrc: string;
  imageAlt: string;
}

const DashboardHero: React.FC<DashboardHeroProps> = ({
  eyebrow,
  title,
  description,
  imageSrc,
  imageAlt,
}) => {
  return (
    <section className="relative overflow-hidden rounded-[30px] border border-[#dfe5ef] bg-white p-6 shadow-[0_10px_30px_rgba(133,146,173,0.16)] sm:p-7">
      <div className="pointer-events-none absolute -right-12 -top-16 h-44 w-44 rounded-full border-40 border-[#eef3ff]" />
      <div className="pointer-events-none absolute -left-10 bottom-0 h-24 w-32 rounded-t-full bg-[#eef3ff]" />

      <div className="relative flex flex-col gap-5 md:flex-row md:items-center md:justify-between">
        <div className="max-w-2xl">
          <p className="text-xs font-semibold uppercase tracking-[0.28em] text-[#5d87ff]">{eyebrow}</p>
          <h1 className="mt-2 text-3xl font-extrabold text-[#2a3547] sm:text-4xl">{title}</h1>
          <p className="mt-3 text-sm leading-relaxed text-[#5a6a85] sm:text-base">{description}</p>
        </div>

        <img
          src={imageSrc}
          alt={imageAlt}
          className="h-24 w-24 shrink-0 self-end rounded-2xl border border-[#e6edf8] bg-[#f8fbff] p-2 shadow-[0_8px_20px_rgba(133,146,173,0.2)] md:h-28 md:w-28"
        />
      </div>
    </section>
  );
};

export default DashboardHero;
