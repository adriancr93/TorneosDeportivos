import SwiftUI
import Charts

// ─── Gráfico apilado: GF y GC por equipo ──────────
struct GolesRadarView: View {
    @ObservedObject var api: APIClient

    struct GolesRow: Identifiable {
        let id = UUID()
        let equipo: String
        let tipo: String     // "Goles a favor" o "Goles en contra"
        let cantidad: Int
    }

    var rows: [GolesRow] {
        api.standings.flatMap { s in [
            GolesRow(equipo: abrev(s.equipo), tipo: "Goles a favor",    cantidad: s.golesFavor),
            GolesRow(equipo: abrev(s.equipo), tipo: "Goles en contra", cantidad: s.golesContra)
        ]}
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Goles a favor vs Goles en contra")
                .font(.headline)
                .padding(.leading)

            Chart(rows) { row in
                BarMark(
                    x: .value("Equipo", row.equipo),
                    y: .value("Goles",  row.cantidad)
                )
                .foregroundStyle(by: .value("Tipo", row.tipo))
                .position(by: .value("Tipo", row.tipo))
            }
            .chartForegroundStyleScale([
                "Goles a favor":    Color.accentColor,
                "Goles en contra":  Color.red.opacity(0.75)
            ])
            .chartLegend(position: .bottom, alignment: .leading)
            .chartXAxis {
                AxisMarks { _ in AxisValueLabel().font(.caption) }
            }
            .padding()
            .frame(maxHeight: .infinity)

            // Mini-leyenda de victorias/empates/derrotas
            if !api.standings.isEmpty {
                Divider().padding(.horizontal)
                Text("Victorias / Empates / Derrotas")
                    .font(.headline)
                    .padding(.leading)

                Chart(api.standings.sorted { $0.victorias > $1.victorias }.prefix(6)) { s in
                    BarMark(x: .value("Equipo", abrev(s.equipo)), y: .value("V", s.victorias))
                        .foregroundStyle(Color.green).position(by: .value("", "V"))
                        .annotation(position: .top) { Text("V:\(s.victorias)").font(.system(size: 9)) }
                    BarMark(x: .value("Equipo", abrev(s.equipo)), y: .value("E", s.empates))
                        .foregroundStyle(Color.orange).position(by: .value("", "E"))
                    BarMark(x: .value("Equipo", abrev(s.equipo)), y: .value("D", s.derrotas))
                        .foregroundStyle(Color.red).position(by: .value("", "D"))
                }
                .chartLegend(.hidden)
                .chartXAxis {
                    AxisMarks { _ in AxisValueLabel().font(.caption) }
                }
                .padding(.horizontal)
                .frame(height: 160)
            }
        }
        .padding(.vertical, 8)
    }

    /// Abrevia nombre largo para que quepa en el eje X
    private func abrev(_ nombre: String) -> String {
        let partes = nombre.split(separator: " ")
        if partes.count >= 2 {
            return partes.prefix(2).map { String($0.prefix(4)) }.joined(separator: ".")
        }
        return String(nombre.prefix(8))
    }
}
