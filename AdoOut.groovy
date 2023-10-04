import com.exalate.api.domain.IIssueKey
import com.exalate.api.domain.connection.IConnection
import com.exalate.basic.domain.hubobject.v1.BasicHubIssue
import services.SyncScriptContextService

class AdoOut {
    static final SyncScriptContextService syncScriptContextService = new SyncScriptContextService()

    static void send() {
        def context = syncScriptContextService.syncScriptContext
        BasicHubIssue workItem = context."workItem"
        BasicHubIssue replica = context."replica"
        def debug = context.debug
        def syncHelper = context.syncHelper

        IConnection connection = context.connection as IConnection
        IIssueKey issueKey = context.issueKey


        replica.key = workItem.key
        replica.summary = workItem.workItem
        replica.description = workItem.description
        replica.comments = workItem.comments
        replica.attachments = workItem.attachments
        replica.status = workItem.status


        def getPreviousJson = {
            def ttRepo = (syncHelper).twinTraceRepository
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
        replica.customKeys."lastStatusChange" = (previous?.status?.name == replica.status.name) ?  null : [
                "from": previous?.status?.name,
                "to": replica.status.name
        ]
    }
}
