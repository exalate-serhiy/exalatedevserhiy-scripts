package domain.project_properties.sync

class SyncProjectProperty {
    final String connection;
    final Active active;

    SyncProjectProperty(String connection, String activeStr) {
        this(connection, Active.getOrDefault(activeStr))
    }
    SyncProjectProperty(String connection, Active active) {
        this.connection = connection
        this.active = active
    }
}
