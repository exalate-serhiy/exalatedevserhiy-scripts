package services.jira

import domain.project_properties.sync.SyncProjectProperty
import groovy.json.JsonSlurper
import services.SyncScriptContextService

/**
 * Allows to fetch the project/{projectIdOrKey}/property/sync
 * */
class SyncProjectPropertyService {
    private final SyncScriptContextService syncScriptContextService
    private final JsonSlurper jsonSlurper

    SyncProjectPropertyService(SyncScriptContextService syncScriptContextService) {
        this.syncScriptContextService = syncScriptContextService
    }

    SyncProjectProperty getSyncProperty(String projectIdOrKey) {
        Map<String, Object> context = syncScriptContextService.syncScriptContext
        Map<String, Object> syncProperty = new JiraClient()
                .http(
                        "GET", "/project/${projectIdOrKey}/property/sync".toString(),
                        [:],
                        null,
                        ["Authorization":["ADMIN:557058:71941f13-d5e0-46d8-9dfd-027bc4d8f6ce"]]
                ) { response ->
                    if (response.code >= 400) throw new com.exalate.api.exception.IssueTrackerException(
                            "Failed to perform impersonated GET /project/${projectIdOrKey}/property/sync : ${response.code} ${response.body}".toString()
                    )
                    else jsonSlurper.parseText(response.body)  as Map<String, Object>;
                } as Map<String, Object>;
        new SyncProjectProperty(
                syncProperty."connection" as String,
                syncProperty."active" as String
        )
    }
}
