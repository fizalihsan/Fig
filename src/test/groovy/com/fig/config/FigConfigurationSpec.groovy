package com.fig.config

import spock.lang.Specification

/**
 * Comment here about the class
 * User: Fizal
 * Date: 12/10/13
 * Time: 6:30 PM
 */
class FigConfigurationSpec extends Specification {

    def "Load configurations"() {
        when: FigConfiguration config = FigConfiguration.getInstance()

        then:
        def activeMQConfig = config.getActiveMQConfig()
        activeMQConfig != null
        def neo4jConfig = config.getNeo4jConfig()
        neo4jConfig != null

        neo4jConfig.dbLocation == "C:/temp/test-graph-db"
        !neo4jConfig.enableWebserver

        activeMQConfig.brokerName == "fig"
        activeMQConfig.dataFolderLocation == "C:/temp/test_ems_data"
        activeMQConfig.brokerURI == "vm://localhost?broker.persistent=false"
        activeMQConfig.requestQueue == "fig.TestRequestQueue"
    }
}
