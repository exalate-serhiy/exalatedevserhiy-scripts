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
        this.jsonSlurper = new JsonSlurper()
    }

    SyncProjectProperty getSyncProperty(String projectIdOrKey) {
        Map<String, Object> context = syncScriptContextService.syncScriptContext
        def headerValue = context.userAuthorization
        //context.debug.error("headerValue=$headerValue")
        Map<String, Object> syncProperty = new JiraClient()
                .http(
                        "GET", "/rest/api/2/project/${projectIdOrKey}/properties/sync".toString(),
                        [:],
                        null,
                        ["Authorization": [
                                headerValue
                        ]]
                ) { response ->
                    if (response.code >= 400) throw new com.exalate.api.exception.IssueTrackerException(
                            "Failed to perform GET /rest/api/2/project/${projectIdOrKey}/properties/sync : ${response.code} ${response.body}".toString()
                    )
                    else jsonSlurper.parseText(response.body)  as Map<String, Object>;
                } as Map<String, Object>;
        //context.debug.error("syncProperty=$syncProperty syncProperty.\"active\"=${syncProperty."active"  as String}")
        new SyncProjectProperty(
                syncProperty.value?."connection" as String,
                syncProperty.value?."active" as String
        )
    }
}
