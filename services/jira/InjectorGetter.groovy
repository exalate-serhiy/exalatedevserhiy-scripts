package services.jira

import services.SyncScriptContextService

class InjectorGetter {
    static SyncScriptContextService syncScriptContextService = new SyncScriptContextService()

    static Object getInjector() {
        def context = syncScriptContextService.syncScriptContext
        context.injector
    }

    static def getHttpClient() {
        def context = syncScriptContextService.syncScriptContext
        context.httpClient
    }
    static def getDebug() {
        def context = syncScriptContextService.syncScriptContext
        context.debug
    }
}
