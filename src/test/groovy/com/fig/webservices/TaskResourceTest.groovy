package com.fig.webservices
import com.fig.manager.TaskManager
import spock.lang.Specification

import javax.ws.rs.core.Response

import static com.fig.domain.TaskBuilder.task
import static com.fig.util.BindingUtil.toPrettyJson
import static javax.ws.rs.core.Response.Status.BAD_REQUEST
import static javax.ws.rs.core.Response.Status.OK
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/28/13
 * Time: 11:05 AM
 */
class TaskResourceTest extends Specification {

    def "create - Valid Response"() {
        String json = """[{"name":"a1"}]""";
        def resource = Spy(TaskResource)
        def taskManager = Mock(TaskManager)
        resource.getTaskManager() >> taskManager

        when:
        def response = resource.create(json)
        then:
        response.getStatusInfo() == Response.Status.OK
        1 * taskManager.createTasks(_)
    }

    def "create - Invalid Response"() {
        def resource = Spy(TaskResource)
        def taskManager = Mock(TaskManager)
        resource.getTaskManager() >> taskManager

        when:
        def response = resource.create(null)
        then:
        response.getStatusInfo() == Response.Status.BAD_REQUEST
    }

    static def taskB = task("b").properties(["key": "value"]).dependsOn(["y", "z"] as Set).build()
    static def taskX = task("x").properties(["key1": "value1", "key2": "value2"]).dependsOn(["y", "z"] as Set).build()
    static def taskY = task("y").build()
    static def taskZ = task("z").build()
    def "query"(){
        def resource = Spy(TaskResource)
        def taskManager = Spy(TaskManager)
        resource.getTaskManager() >> taskManager
        taskManager.getTask("a") >> null
        taskManager.getTask("b") >> taskB
        taskManager.getTask("x") >> taskX
        taskManager.getTask("y") >> taskY
        taskManager.getTask("z") >> taskZ

        expect:
        Response response = resource.query(input)
        response.getStatus() == status.getStatusCode()
        def entity = response.getEntity().toString()
        entity.equals(jsonMessage) || entity.endsWith(jsonMessage)
        tags.each { tag -> entity.contains(tag) }

        where:
        input | status      | tags                                      | jsonMessage
        null  | BAD_REQUEST | ["reason", "message"]                     | "{\"reason\":\"No task name provided in the request\",\"message\":\"Unable to query for tasks\"}"
        ""    | BAD_REQUEST | ["reason", "message"]                     | "{\"reason\":\"No task name provided in the request\",\"message\":\"Unable to query for tasks\"}"
        "a"   | OK          | ["requestId", "requestedTime", "message"] | "\"message\":\"Task not found in the database by name: [a]\"}"
        "b"   | OK          | ["requestId", "requestedTime", "message"] | toPrettyJson([taskB])
        "x"   | OK          | ["requestId", "requestedTime", "message"] | toPrettyJson([taskX])
        "y,z" | OK          | ["requestId", "requestedTime", "message"] | toPrettyJson([taskZ, taskY])
    }

    def "update - Valid Response"() {
        String json = """[{"name":"a1"}]""";
        def resource = Spy(TaskResource)
        def taskManager = Mock(TaskManager)
        resource.getTaskManager() >> taskManager

        when:
        def response = resource.update(json)
        then:
        response.getStatusInfo() == Response.Status.OK
        1 * taskManager.updateTaskProperties(_)
    }

    def "update - Invalid Response"() {
        def resource = Spy(TaskResource)
        def taskManager = Mock(TaskManager)
        resource.getTaskManager() >> taskManager

        when:
        def response = resource.update(null)
        then:
        response.getStatusInfo() == Response.Status.BAD_REQUEST
    }

    def "parseTaskNames"() {
        def resource = Spy(TaskResource)
        expect: resource.parseTaskNames(input).size() == output
        where:
        input     | output
        null      | 0
        ""        | 0
        ",,,"     | 0
        "a,b"     | 2
        "a,,b"    | 2
        "a,,b   " | 2
    }
}
