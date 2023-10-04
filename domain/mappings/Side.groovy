package domain.mappings

class Side {
    final String name
    final ProjectScope project
    Side(String name, ProjectScope project) {
        this.name = name
        this.project = project
    }
}
