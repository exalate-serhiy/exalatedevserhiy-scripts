import com.exalate.api.domain.connection.IConnection
import com.exalate.basic.domain.hubobject.v1.BasicHubIssue
import domain.mappings.Side
import domain.project_properties.sync.Active
import domain.project_properties.sync.SyncProjectProperty
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import services.SyncScriptContextService
import services.jira.SyncProjectPropertyService
import services.mappings.MappingService

class JiraOut {
    static final Logger LOG = LoggerFactory.getLogger("com.exalate.scripts.out")
    static final SyncScriptContextService syncScriptContextService = new SyncScriptContextService()
    static final SyncProjectPropertyService syncProjectPropertyService = new SyncProjectPropertyService(syncScriptContextService)
    static final MappingService mappingService = new MappingService()
    static void send() {
        Map<String, Object> context = syncScriptContextService.syncScriptContext
        IConnection connection = context.connection as IConnection
        def nodeHelper = context.nodeHelper
        BasicHubIssue issue = context."issue" as BasicHubIssue
        BasicHubIssue replica = context."replica" as BasicHubIssue
        SyncProjectProperty sync = syncProjectPropertyService.getSyncProperty(
                issue.getProject().key
        )
        if (sync.active == Active.FALSE) {
            LOG.info("#jira_out not syncing ${issue.key} because the project ${issue.getProject().key} has property sync.active set to ${sync.active.text}")
            return
        }
        if (sync.connection && !sync.connection.trim().empty && sync.connection != context.connection.name) {
            LOG.info("#jira_out not syncing ${issue.key} because the project ${issue.getProject().key} has property sync.connection set to ${sync.connection} which doesn't match the current connection: ${context.connection.name}")
            return
        }
        if (!sync.connection || sync.connection.trim().empty) {
            // check the mappings.json to see if it's supposed to go to the remote instance or not
            def mappings = mappingService.mappings
            def sideMatchesWithIssue = { Side side ->
                side.project.projectKey.equalsIgnoreCase(issue.getProject().key)
            }
            def mappingForProject = mappings.find {
                sideMatchesWithIssue(it.a) ||
                        sideMatchesWithIssue(it.b)
            }
            if (!mappingForProject) {
                LOG.info(
                        "#jira_out not syncing ${issue.key} " +
                                "because the project ${issue.getProject().key} is not found in the mappings: " +
                                mappings.collect{ it.jsonStr}.join(", ")
                )
                return
            }
            def localSideAndRemoteSide = sideMatchesWithIssue(mappingForProject.a) ?
                    [ local: mappingForProject.a, remote: mappingForProject.b] :
                    [ local: mappingForProject.b, remote: mappingForProject.b]
            Side localSide = localSideAndRemoteSide.local
            Side remoteSide = localSideAndRemoteSide.remote

            if (connection.remoteInstance.name != remoteSide.name) {
                LOG.info(
                        "#jira_out not syncing ${issue.key} via connection ${connection.name} " +
                                "because this connection is between ${connection.fieldValues."localInstanceName"} and ${connection.remoteInstance.name} " +
                                "and not between ${localSide.name} and ${remoteSide.name}"
                )
                return
            }
            replica.targetProject = remoteSide.project.projectKey
        }

        replica.key = issue.key
        replica.summary = issue.summary

        replica.type = issue.getIssueType()
        replica.project = issue.getProject()
        // optimization - don't send all the info about the project
        replica.getProject().components = []
        replica.getProject().versions = []

        replica.comments = issue.comments

        replica.attachments = issue.attachments

        replica.status = issue.status

        // Send all custom fields, which have value
        issue.getCustomFields().each { k, cf ->
            if(cf.value) {
                replica.getCustomFields().put(k, cf)
            }
        }

        def getPreviousJson = {
            def ttRepo = (nodeHelper).twinTraceRepositoryProvider.get()
//com.exalate.persistence.twintrace.TwinTraceRepository ttRepo = ((services.jcloud.hubobjects.NodeHelper)nodeHelper).twinTraceRepositoryProvider.get()
            def await = { f -> scala.concurrent.Await$.MODULE$.result(f, scala.concurrent.duration.Duration.apply(1, java.util.concurrent.TimeUnit.MINUTES)) }
            def orNull = { opt -> opt.isEmpty() ? null : opt.get() }
            def ttOptFuture = ttRepo.getTwinTraceByLocalIssueKeyFuture(connection, issueKey)
            def ttOpt = await(ttOptFuture)
            def tt = orNull(ttOpt)
//com.exalate.api.domain.twintrace.ITwinTrace tt = orNull(ttOpt)
            def lr = tt?.localReplica
            !lr ? null : ({
                def js = new groovy.json.JsonSlurper()
                def previousPayload = js.parseText(lr.payload)
                previousPayload.hubIssue
            })()
        }

        def previous = getPreviousJson()
        replica.customKeys."lastStatusChange" = (previous?.status?.name == issue.status.name) ?  null : [
                "from": previous?.status?.name,
                "to": issue.status.name
        ]
    }
}
