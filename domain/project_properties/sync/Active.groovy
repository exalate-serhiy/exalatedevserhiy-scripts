package domain.project_properties.sync

import services.jira.InjectorGetter

enum Active {
    TRUE("true"),
    FALSE("false");
    final String text;

    Active(String text) {
        this.text = text;
    }

    static Active getOrDefault(String activeStr) {
        InjectorGetter.debug.error("\"true\".equalsIgnoreCase(activeStr)=${"true".equalsIgnoreCase(activeStr)} activeStr=${activeStr}")
        if ("true".equalsIgnoreCase(activeStr)) {
            return TRUE
        } else {
            return FALSE
        }
    }
}