package services.mappings

import domain.mappings.SidesMapping
import domain.mappings.ProjectScope
import domain.mappings.Side
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MappingService {
    private static final Logger LOG = LoggerFactory.getLogger(MappingService.class)
    private static final String MAPPING_JSON_FILE_NAME = "mapping.json"
    private final JsonSlurper jsonSlurper = new JsonSlurper()

    List<SidesMapping> getMappings() {
        def mappingsStr = getClass()
                .classLoader
                .getResource(MAPPING_JSON_FILE_NAME)
                .text
        List<Map<String, Object>> mappingsJson = jsonSlurper.parseText(mappingsStr) as List<Map<String, Object>>;
        mappingsJson
            .inject([] as List<SidesMapping>) { result, mappingJson ->
                def keys = mappingJson.keySet() as List<String>
                if (keys.size() < 2) {
                    LOG.warn("#getMappings mapping ${JsonOutput.toJson(mappingJson)} doesn't have 2 sides specified, ignoring it")
                    return result
                }
                if (keys.size() > 2) {
                    def ignoredKeys = keys.collect()
                    ignoredKeys.pop()
                    ignoredKeys.pop()
                    LOG.warn("#getMappings ignoring the keys: ${ignoredKeys.join(", ")}")
                }
                def sideA = getSideByIndex(mappingJson, keys, 0)
                if (!sideA) {
                    return result
                }
                def sideB = getSideByIndex(mappingJson, keys, 1)
                if (!sideB) {
                    return result
                }
                SidesMapping mapping = new SidesMapping(sideA, sideB, JsonOutput.toJson(mappingJson))
                result.add(mapping)
                result
            }
    }

    private Side getSideByIndex(Map<String, Object> mappingJson, List<String> keys, int index) {
        if (keys.size() <= index) {
            LOG.error(
                    "#getSideByIndex contact the maintainer: the script fails to read a mapping ${JsonOutput.toJson(mappingJson)}. " +
                    "The script attempted to find key by index $index, available keys: ${keys.join(", ")}"
            )
            return null
        }
        def sideName = keys[index]
        Map<String, Object> sideJson = mappingJson[sideName] as Map<String, Object>;
        def projectScope = getProjectScope(sideJson)
        if (!projectScope) {
            return null
        }
        def side = new Side(
                sideName,
                projectScope
        )
        side
    }

    private ProjectScope getProjectScope(Map<String, Object> sideJson) {
        def projectScope = new ProjectScope(sideJson.project as String)
        projectScope
    }
}
