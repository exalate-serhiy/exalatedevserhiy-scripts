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
        BasicHubIssue workItem = context."workItem"

        if (context.firstSync) {
            // figure out the project from the mapping.json
        }
        // return the tuple if you'd like to ensure that no updates are made by Exalate after the script execution
        return null
    }
}
