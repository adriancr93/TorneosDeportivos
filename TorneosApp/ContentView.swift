import SwiftUI
import Charts

// ─── Vista principal con pestañas ─────────────────
struct ContentView: View {
    @StateObject private var api = APIClient()

    var body: some View {
        TabView {
            StandingsView(api: api)
                .tabItem { Label("Posiciones", systemImage: "list.number") }

            GoleadoresView(api: api)
                .tabItem { Label("Goleadores", systemImage: "soccerball") }

            PartidosView(api: api)
                .tabItem { Label("Partidos", systemImage: "calendar") }

            GolesRadarView(api: api)
                .tabItem { Label("Goles por equipo", systemImage: "chart.bar.fill") }
        }
        .padding(16)
        .overlay(alignment: .topTrailing) {
            Button {
                api.cargarTodo()
            } label: {
                Label("Actualizar", systemImage: "arrow.clockwise")
                    .padding(8)
            }
            .buttonStyle(.borderedProminent)
            .padding(16)
        }
        .overlay {
            if api.isLoading {
                ZStack {
                    Color.black.opacity(0.25).ignoresSafeArea()
                    ProgressView("Cargando datos...").padding(20).background(.regularMaterial).cornerRadius(12)
                }
            }
        }
        .onAppear { api.cargarTodo() }
        .alert("Error de conexión", isPresented: .constant(api.errorMessage != nil)) {
            Button("OK") { api.errorMessage = nil }
        } message: {
            Text(api.errorMessage ?? "")
        }
    }
}
