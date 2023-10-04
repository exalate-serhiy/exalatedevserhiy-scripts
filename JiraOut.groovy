import com.exalate.basic.domain.hubobject.v1.BasicHubIssue
import domain.project_properties.sync.Active
import domain.project_properties.sync.SyncProjectProperty
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import services.SyncScriptContextService
import services.jira.SyncProjectPropertyService

class JiraOut {
    static final Logger LOG = LoggerFactory.getLogger("com.exalate.scripts.out")
    static final SyncScriptContextService syncScriptContextService = new SyncScriptContextService()
    static final SyncProjectPropertyService syncProjectPropertyService = new SyncProjectPropertyService(syncScriptContextService)
    static void send() {
        Map<String, Object> context = syncScriptContextService.syncScriptContext
        BasicHubIssue issue = context."issue" as BasicHubIssue
        BasicHubIssue replica = context."replica" as BasicHubIssue
        SyncProjectProperty sync = syncProjectPropertyService.getSyncProperty(
                issue.getProject().key
        )
        if (sync.active == Active.FALSE) {
            LOG.info("#jira_out not syncing ${issue.key} because the project ${issue.getProject().key} has property sync.active set to ${sync.active.text}")
            return
        }
        if (sync.connection != context.connection.name) {
            LOG.info("#jira_out not syncing ${issue.key} because the project ${issue.getProject().key} has property sync.connection set to ${sync.connection} which doesn't match the current connection: ${context.connection.name}")
            return
        }

        replica.key = issue.key
        replica.summary = issue.summary

        replica.comments = issue.comments

        // Send all custom fields, which have value
        issue.getCustomFields().each { k, cf ->
            if(cf.value) {
                replica.getCustomFields().put(k, cf)
            }
        }
    }
}
