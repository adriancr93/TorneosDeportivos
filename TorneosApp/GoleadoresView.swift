import SwiftUI
import Charts

// ─── Tabla + gráfico de goleadores ────────────────
struct GoleadoresView: View {
    @ObservedObject var api: APIClient

    var topGoleadores: [Goleador] { Array(api.goleadores.sorted { $0.goles > $1.goles }.prefix(10)) }

    var body: some View {
        HSplitView {
            // Tabla
            Table(topGoleadores) {
                TableColumn("#") { g in
                    let pos = (topGoleadores.firstIndex { $0.id == g.id } ?? 0) + 1
                    Text("\(pos)").monospacedDigit()
                }.width(30)
                TableColumn("Jugador") { Text($0.nombre) }.width(min: 140, ideal: 180)
                TableColumn("Equipo")  { Text($0.equipo ?? "-").foregroundColor(.secondary) }.width(min: 120, ideal: 160)
                TableColumn("Goles")   {
                    Text("\($0.goles)")
                        .bold()
                        .foregroundColor(.accentColor)
                        .monospacedDigit()
                }.width(55)
            }
            .frame(minWidth: 380)

            // Gráfico de barras horizontales
            VStack(alignment: .leading, spacing: 8) {
                Text("Top 10 goleadores")
                    .font(.headline)
                    .padding(.leading, 8)

                Chart(topGoleadores) { g in
                    BarMark(
                        x: .value("Goles", g.goles),
                        y: .value("Jugador", g.nombre)
                    )
                    .foregroundStyle(by: .value("Equipo", g.equipo ?? ""))
                    .annotation(position: .trailing) {
                        Text("\(g.goles)").font(.caption).bold()
                    }
                }
                .chartLegend(position: .bottom, alignment: .leading)
                .chartYAxis {
                    AxisMarks { _ in AxisValueLabel().font(.caption) }
                }
                .padding()

                Spacer()
            }
            .frame(minWidth: 280)
        }
        .padding(.top, 8)
    }
}
