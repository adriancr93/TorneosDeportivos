import Foundation

class APIClient: ObservableObject {
    static let base = "http://localhost:8080/api"

    @Published var standings: [Standing] = []
    @Published var goleadores: [Goleador] = []
    @Published var partidos: [Partido] = []
    @Published var equipos: [Equipo] = []
    @Published var isLoading = false
    @Published var errorMessage: String?

    func cargarTodo() {
        isLoading = true
        errorMessage = nil
        let group = DispatchGroup()

        fetchList(path: "/standings",  group: group) { self.standings  = $0 }
        fetchList(path: "/goleadores", group: group) { self.goleadores = $0 }
        fetchList(path: "/partidos",   group: group) { self.partidos   = $0 }
        fetchList(path: "/equipos",    group: group) { self.equipos    = $0 }

        group.notify(queue: .main) {
            self.isLoading = false
        }
    }

    private func fetchList<T: Decodable>(path: String,
                                         group: DispatchGroup,
                                         assign: @escaping ([T]) -> Void) {
        guard let url = URL(string: "\(APIClient.base)\(path)") else { return }
        group.enter()
        URLSession.shared.dataTask(with: url) { data, _, error in
            defer { group.leave() }
            if let error {
                DispatchQueue.main.async { self.errorMessage = error.localizedDescription }
                return
            }
            guard let data else { return }
            if let decoded = try? JSONDecoder().decode([T].self, from: data) {
                DispatchQueue.main.async { assign(decoded) }
            }
        }.resume()
    }
}
