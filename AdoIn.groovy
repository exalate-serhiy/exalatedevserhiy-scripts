import com.exalate.api.domain.IIssueKey
import com.exalate.api.domain.twintrace.INonPersistentTrace
import com.exalate.basic.domain.hubobject.v1.BasicHubIssue
import services.SyncScriptContextService
import scala.*
import scala.collection.Seq

class AdoIn {
    static final SyncScriptContextService syncScriptContextService = new SyncScriptContextService()
    static Tuple2<IIssueKey, Seq<INonPersistentTrace>> receive() {
        def context = syncScriptContextService.syncScriptContext
        def commentHelper = context.commentHelper
        def attachmentHelper = context.attachmentHelper
        BasicHubIssue workItem = context."workItem"
        BasicHubIssue replica = context."replica"
        def debug = context.debug

        //debug.error("#ado_in not implemented yet!")

        if (context.firstSync) {

            // figure out the project from the mapping.json
            if (replica.targetProject) {
                workItem.projectKey = replica.targetProject
            }

            workItem.type = "Task"
        }

        workItem.description = replica.description

        workItem.summary = replica.summary

        workItem.comments  = commentHelper.mergeComments(workItem, replica)
        workItem.attachments  = attachmentHelper.mergeAttachments(workItem, replica)


        // return the tuple if you'd like to ensure that no updates are made by Exalate after the script execution
        return null
    }
}
