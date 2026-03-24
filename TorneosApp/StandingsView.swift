import SwiftUI
import Charts

// ─── Tabla de posiciones + gráfico de puntos ──────
struct StandingsView: View {
    @ObservedObject var api: APIClient

    var body: some View {
        HSplitView {
            // Tabla
            Table(api.standings) {
                TableColumn("#")        { Text("\($0.posicion)").monospacedDigit() }.width(30)
                TableColumn("Equipo")   { Text($0.equipo) }.width(min: 160, ideal: 200)
                TableColumn("PJ")       { Text("\($0.partidosJugados)").monospacedDigit() }.width(35)
                TableColumn("PG")       { Text("\($0.victorias)").monospacedDigit() }.width(35)
                TableColumn("PE")       { Text("\($0.empates)").monospacedDigit() }.width(35)
                TableColumn("PP")       { Text("\($0.derrotas)").monospacedDigit() }.width(35)
                TableColumn("GF")       { Text("\($0.golesFavor)").monospacedDigit() }.width(35)
                TableColumn("GC")       { Text("\($0.golesContra)").monospacedDigit() }.width(35)
                TableColumn("DIF")      { Text("\($0.diferencia)").monospacedDigit().foregroundColor(diferenciaColor($0.diferencia)) }.width(40)
                TableColumn("PTS")      {
                    Text("\($0.puntos)")
                        .bold()
                        .monospacedDigit()
                        .foregroundColor(.accentColor)
                }.width(40)
            }
            .frame(minWidth: 460)

            // Gráfico barras de puntos
            VStack(alignment: .leading, spacing: 8) {
                Text("Puntos por equipo")
                    .font(.headline)
                    .padding(.leading, 8)

                Chart(api.standings) { s in
                    BarMark(
                        x: .value("Puntos", s.puntos),
                        y: .value("Equipo", s.equipo)
                    )
                    .foregroundStyle(by: .value("Equipo", s.equipo))
                    .annotation(position: .trailing) {
                        Text("\(s.puntos)").font(.caption).bold()
                    }
                }
                .chartLegend(.hidden)
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

    private func diferenciaColor(_ dif: Int) -> Color {
        dif > 0 ? .green : dif < 0 ? .red : .secondary
    }
}
