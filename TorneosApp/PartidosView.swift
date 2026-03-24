import SwiftUI

// ─── Lista de partidos con resultado ──────────────
struct PartidosView: View {
    @ObservedObject var api: APIClient
    @State private var filtro = "Todos"

    let filtros = ["Todos", "JUGADO", "PENDIENTE"]

    var partidosFiltrados: [Partido] {
        filtro == "Todos" ? api.partidos : api.partidos.filter { $0.estado == filtro }
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Picker("Filtro", selection: $filtro) {
                ForEach(filtros, id: \.self) { Text($0) }
            }
            .pickerStyle(.segmented)
            .padding(.horizontal)
            .padding(.vertical, 8)

            Table(partidosFiltrados) {
                TableColumn("Jornada") { Text($0.fecha).foregroundColor(.secondary) }.width(80)
                TableColumn("Local")    { Text($0.local) }.width(min: 140, ideal: 180)
                TableColumn("Resultado") { p in
                    if p.estado == "JUGADO" {
                        Text("\(p.golesLocal)  –  \(p.golesVisitante)")
                            .bold()
                            .monospacedDigit()
                    } else {
                        Text("vs").foregroundColor(.secondary)
                    }
                }.width(80)
                TableColumn("Visitante") { Text($0.visitante) }.width(min: 140, ideal: 180)
                TableColumn("Estado") { p in
                    Label(p.estado == "JUGADO" ? "Jugado" : "Pendiente",
                          systemImage: p.estado == "JUGADO" ? "checkmark.circle.fill" : "clock")
                    .foregroundColor(p.estado == "JUGADO" ? .green : .orange)
                    .font(.caption)
                }.width(100)
            }
        }
        .padding(.top, 8)
    }
}
