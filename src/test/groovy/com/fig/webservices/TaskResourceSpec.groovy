package com.fig.webservices
import com.fig.manager.TaskManager
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Timeout

import javax.ws.rs.core.Response

import static com.fig.domain.TaskBuilder.task
import static com.fig.util.BindingUtil.toPrettyJson
import static java.util.concurrent.TimeUnit.SECONDS
import static javax.ws.rs.core.Response.Status.BAD_REQUEST
import static javax.ws.rs.core.Response.Status.OK
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/28/13
 * Time: 11:05 AM
 */
class TaskResourceSpec extends Specification {

    @Shared def taskResource = new TaskResource()

    void setup() {
        //cleaning up the graph database to start with clean state
        taskResource.deleteAll()
    }

    @Timeout(value = 10, unit = SECONDS)
    def "create - Create a valid task and query to check if it exists after creation"() {
        String json = """[{"name":"a1"}]""";

        when:
        def response = taskResource.create(json)
        sleep(2000) //intentional delay to let the processing complete
        then:
        response.getStatusInfo() == Response.Status.OK
        def query = taskResource.query("a1")
        query.entity == """[
  {
    "name": "a1",
    "dependsOn": [],
    "properties": {}
  }
]"""
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

    def "update - Update properties on an existing task and query to see the updates in place"() {
        taskResource.create("""[{"name":"a1"}]""")
        sleep(2000) //intentional delay to let the processing complete

        when:
        def response = taskResource.update("""[{"name":"a1", "properties":{"key":"value1"}}]""")
        sleep(2000) //intentional delay to let the processing complete
        then:
        response.getStatusInfo() == Response.Status.OK
        taskResource.query("a1").entity == """[
  {
    "name": "a1",
    "dependsOn": [],
    "properties": {
      "key": "value1"
    }
  }
]"""
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

    def "createDependency - Valid Response"() {
        taskResource.create("""[{"name":"a1"}, {"name":"b1"}, {"name":"c1"}]""")
        sleep(5000) //intentional delay to let the processing complete

        when:
        def response = taskResource.createDependency("""[{"fromTask":"a1", "toTasks":["b1", "c1"]}]""")
        sleep(2000) //intentional delay to let the processing complete

        then:
        response.getStatusInfo() == Response.Status.OK
        taskResource.query("a1").entity == """[
  {
    "name": "a1",
    "dependsOn": [
      "c1",
      "b1"
    ],
    "properties": {}
  }
]"""
    }

    def "createDependency - Invalid Response"() {
        def resource = Spy(TaskResource)
        def taskManager = Mock(TaskManager)
        resource.getTaskManager() >> taskManager

        when:
        def response = resource.createDependency(null)
        then:
        response.getStatusInfo() == Response.Status.BAD_REQUEST
    }
}
