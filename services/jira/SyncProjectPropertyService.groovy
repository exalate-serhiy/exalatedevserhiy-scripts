package services.jira

import domain.project_properties.sync.SyncProjectProperty
import jcloudnode.services.replication.PreparedHttpClient
import services.SyncScriptContextService

/**
 * Allows to fetch the project/{projectIdOrKey}/property/sync
 * */
class SyncProjectPropertyService {
    private SyncScriptContextService syncScriptContextService

    SyncProjectPropertyService(SyncScriptContextService syncScriptContextService) {
        this.syncScriptContextService = syncScriptContextService
    }

    SyncProjectProperty getSyncProperty(String projectIdOrKey) {
        Map<String, Object> context = syncScriptContextService.syncScriptContext
        PreparedHttpClient httpClient = context.httpClient
        Map<String, Object> syncProperty = httpClient.get("/project/${projectIdOrKey}/property/sync".toString()) as Map<String, Object>;
        new SyncProjectProperty(
                syncProperty."connection" as String,
                syncProperty."active" as String
        )
    }
}
