package com.fig.webservices

import com.fig.manager.TaskManager
import spock.lang.Specification

import javax.ws.rs.core.Response

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/30/13
 * Time: 5:06 PM
 */
class RelationshipResourceSpec extends Specification {
    def "create - Valid Response"() {
        String json = """[{"fromTask":"a1", "toTasks":["b1", "c1"]}]""";
        def resource = Spy(RelationshipResource)
        def taskManager = Mock(TaskManager)
        resource.getTaskManager() >> taskManager

        when:
        def response = resource.create(json)
        then:
        response.getStatusInfo() == Response.Status.OK
        1 * taskManager.createTaskDependencies(_)
    }

    def "create - Invalid Response"() {
        def resource = Spy(RelationshipResource)
        def taskManager = Mock(TaskManager)
        resource.getTaskManager() >> taskManager

        when:
        def response = resource.create(null)
        then:
        response.getStatusInfo() == Response.Status.BAD_REQUEST
    }
}
