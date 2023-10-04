package domain.project_properties.sync

enum Active {
    TRUE("true"),
    FALSE("false");
    final String text;

    Active(String text) {
        this.text = text;
    }

    static Active getOrDefault(String activeStr) {
        if ("true".equalsIgnoreCase(activeStr)) {
            return TRUE
        } else {
            return FALSE
        }
    }
}