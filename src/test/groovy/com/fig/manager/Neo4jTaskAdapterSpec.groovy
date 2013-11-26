package com.fig.manager

import org.neo4j.graphdb.Transaction
import org.neo4j.test.TestGraphDatabaseFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification

import static com.fig.domain.FigConstants.TASK_NAME
import static com.fig.domain.TaskBuilder.task
import static com.fig.domain.TaskRelations.DEPENDS_ON
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/25/13
 * Time: 8:06 PM
 */
class Neo4jTaskAdapterSpec extends Specification {
    private static final Logger LOG = LoggerFactory.getLogger(Neo4jTaskAdapterSpec.class);
    @Shared Neo4jHelper neo4jUtil
    @Shared Transaction txn
    @Shared def adapter = new Neo4jTaskAdapter()

    def setup(){
        LOG.debug("Creating test graph database")
        neo4jUtil = new Neo4jHelper()
        Neo4jHelper.setInstance(neo4jUtil)
        def graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase()
        neo4jUtil.setGraphDb(graphDb)
        neo4jUtil.createNodeNameIndex()
        txn = neo4jUtil.beginTransaction()
    }

    def cleanup(){
        LOG.debug("Destroying test graph database")
        txn.finish()
        neo4jUtil.getGraphDb().shutdown()
    }

    def "Create new task without properties"() {
        def taskName = "Task1"
        def task = task(taskName).build()

        when: adapter.createTask(task)
        then:
        def node = neo4jUtil.getNodeNameIndex().get(TASK_NAME, taskName).getSingle()
        node != null
        def properties = node.getPropertyKeys()
        properties.size == 1
    }

    def "Create new task with properties"() {
        def taskName = "Task1"
        def task = task(taskName).properties(["key1":"value1"]).build()

        when: adapter.createTask(task)
        then:
        def node = neo4jUtil.getNodeNameIndex().get(TASK_NAME, taskName).getSingle()
        node != null
        node.getPropertyKeys().size() == 2
    }

    def "Re-create an existing task"() {
        def task = task("Task1").build()
        adapter.createTask(task)

        when: adapter.createTask(task)
        then:
        thrown(RuntimeException)
    }

    def "Update properties of an existing task"() {
        def taskName = "Task1"
        def newtask = task(taskName).properties(["key1":"value1"]).build()
        adapter.createTask(newtask)

        when:
        def updatedTask = task(taskName).properties(["key1":"value11", "key2":"value22"]).build()
        adapter.updateTaskProperties(updatedTask)

        then:
        def expectedNode = adapter.getNode(taskName)
        expectedNode.getPropertyKeys().size() == 3
        expectedNode.getProperty("key1") == "value11"
        expectedNode.getProperty("key2") == "value22"
    }

    def "Delete properties of an non-existant task"() {
        def taskName = "Task1"

        when:
        def updatedTask = task(taskName).properties(["key1":"value11", "key2":"value22"]).build()
        adapter.deleteTaskProperties(updatedTask)

        then:
        thrown(RuntimeException)
    }

    def "Delete properties of an existing task"() {
        def taskName = "Task1"
        def newtask = task(taskName).properties(["key1":"value1", "key2":"value2", "key3":"value3"]).build()
        adapter.createTask(newtask)

        when:
        def tempTask = task(taskName).properties(["key1":"value1", "key4":"value4", TASK_NAME:taskName]).build()
        adapter.deleteTaskProperties(tempTask)

        then:
        def expectedNode = adapter.getNode(taskName)
        expectedNode.getPropertyKeys().size() == 3
        expectedNode.hasProperty(TASK_NAME)
        expectedNode.hasProperty("key1") == false
        expectedNode.getProperty("key2") == "value2"
        expectedNode.getProperty("key3") == "value3"
        expectedNode.hasProperty("key4") == false
    }

    def "Update properties of an non-existant task"() {
        def taskName = "Task1"

        when:
        def updatedTask = task(taskName).properties(["key1":"value11", "key2":"value22"]).build()
        adapter.updateTaskProperties(updatedTask)

        then:
        thrown(RuntimeException)
    }

    /*def "test deleteTask"() {
        given:

        when:
        // TODO implement stimulus
        then:
        // TODO implement assertions
    }*/

    def "doesTaskExistInDb - Non-existant task"() {
        def newtask = task("Task1").build()
        when: def doesTaskExist = adapter.doesTaskExistInDb(newtask)
        then: doesTaskExist == false
    }

    def "doesTaskExistInDb - Existing task"() {
        def newtask = task("Task1").build()
        adapter.createTask(newtask)
        when: def doesTaskExist = adapter.doesTaskExistInDb(newtask)
        then: doesTaskExist == true
    }

    def "Create Task Dependencies - No dependency provided"() {
        def newtask1 = task("Task1").build()
        def newtask2 = task("Task2").build()
        adapter.createTask(newtask1)
        adapter.createTask(newtask2)
        when: adapter.createTaskDependencies(newtask1)
        then:
        adapter.getNode("Task1").getRelationships().collect().isEmpty()
    }

    def "Create Task Dependencies - With dependency "() {
        def newtask1 = task("Task1").build()
        def newtask2 = task("Task2").dependsOn(["Task1"] as Set).build()
        adapter.createTask(newtask1)
        adapter.createTask(newtask2)
        when: adapter.createTaskDependencies(newtask2)
        then:
        def relationship = adapter.getNode("Task2").relationships.first()
        relationship != null
        relationship.isType(DEPENDS_ON)
        def endNode = relationship.getEndNode()
        adapter.getTaskName(endNode).equals("Task1")
    }

    def "createTaskDependency - Non-existant task 1"() {
        def (task1, task2) = ["Task1", "Task2"]
        when: adapter.createTaskDependency(task1, task2)
        then: thrown(RuntimeException)
    }

    def "createTaskDependency - Non-existant task 2"() {
        def (task1, task2) = ["Task1", "Task2"]
        adapter.createTask(task(task1).build())
        when: adapter.createTaskDependency(task1, task2)
        then: thrown(RuntimeException)
    }

    def "createTaskDependency - Existant tasks"() {
        def (task1, task2) = ["Task1", "Task2"]
        adapter.createTask(task(task1).build())
        adapter.createTask(task(task2).build())
        when: adapter.createTaskDependency(task1, task2)
        then:
        def relationship = adapter.getNode(task1).relationships.first()
        relationship!=null
        relationship.isType(DEPENDS_ON)
        adapter.getTaskName(relationship.getEndNode()).equals("Task2")
    }

    def "deleteTaskDependency - Non-existant task 1"() {
        def (task1, task2) = ["Task1", "Task2"]
        when: adapter.deleteTaskDependency(task1, task2)
        then: thrown(RuntimeException)
    }

    def "deleteTaskDependency - Non-existant task 2"() {
        def (task1, task2) = ["Task1", "Task2"]
        adapter.createTask(task(task1).build())
        when: adapter.deleteTaskDependency(task1, task2)
        then: thrown(RuntimeException)
    }

    def "deleteTaskDependency - Existing tasks with no dependency"() {
        def (task1, task2) = ["Task1", "Task2"]
        adapter.createTask(task(task1).build())
        adapter.createTask(task(task2).build())
        when: boolean deleted = adapter.deleteTaskDependency(task1, task2)
        then: !deleted

    }

    def "deleteTaskDependency - Existing tasks with dependency"() {
        def (task1, task2) = ["Task1", "Task2"]
        adapter.createTask(task(task1).build())
        adapter.createTask(task(task2).dependsOn([task1] as Set).build())
        adapter.createTaskDependency(task1, task2)

        when: boolean deleted = adapter.deleteTaskDependency(task1, task2)
        then:
        deleted
        adapter.getNode(task2).relationships.collect().isEmpty()

    }

    def "Get non-existant node"() {
        when: adapter.getNode("Task1")
        then: thrown(RuntimeException)
    }

    def "Get existant node"() {
        def taskname = "Task1"
        adapter.createTask(task(taskname).build())
        when: def node = adapter.getNode(taskname)
        then: adapter.getTaskName(node).equals(taskname)
    }

    def "getNodeIfExists - Get non-existant node"() {
        when: def node = adapter.getNodeIfExists("Task1")
        then: node == null
    }

    def "getNodeIfExists - Get existant node"() {
        def taskname = "Task1"
        adapter.createTask(task(taskname).build())
        when: def node = adapter.getNodeIfExists(taskname)
        then: adapter.getTaskName(node).equals(taskname)
    }

    def "getTaskName"() {
        def taskname = "Task1"
        adapter.createTask(task(taskname).build())
        when: def expected = adapter.getTaskName(adapter.getNode(taskname))
        then: expected == taskname
    }

    def "getDependentTaskNames - Node with no dependencies"() {
        def taskname = "Task1"
        adapter.createTask(task(taskname).build())
        def node = adapter.getNode(taskname)
        when: def dependents = adapter.getDependentTaskNames(node)
        then: dependents.isEmpty()
    }

    def "getDependentTaskNames - Node with dependencies"() {
        def (task1, task2 ) = ["Task1", "Task2"]
        adapter.createTask(task(task1).build())
        adapter.createTask(task(task2).build())
        adapter.createTaskDependency(task2, task1)
        def node = adapter.getNode(task2);
        when: def dependents = adapter.getDependentTaskNames(node)
        then: dependents.size() ==1; dependents.contains(task1)
    }

    def "getTaskProperties - Task with no properties"() {
        def taskname = "Task1"
        adapter.createTask(task(taskname).build())
        def node = adapter.getNode(taskname)
        when: def props = adapter.getTaskProperties(node)
        then: props.isEmpty()
    }

    def "getTaskProperties - Task with properties"() {
        def taskname = "Task1"
        adapter.createTask(task(taskname).properties(["key1": "value1"]).build())
        def node = adapter.getNode(taskname)
        when: def props = adapter.getTaskProperties(node)
        then: props.size(); props.get("key1") == "value1"
    }

    def "getTask - Non-existant task"() {
        when: def temptask = adapter.getTask("Task1")
        then: temptask == null
    }

    def "getTask - Existant task"() {
        def (taskname1, taskname2 ) = ["Task1", "Task2"]
        def (task1, task2 ) = [task(taskname1).build(), task(taskname2).dependsOn([taskname1] as Set).properties(["key1": "value1"]).build()]
        adapter.createTask(task1)
        adapter.createTask(task2)
        adapter.createTaskDependency(taskname2, taskname1)
        when: def expected = adapter.getTask("Task2")
        then:
        expected.name == task2.name
        expected.dependsOn == task2.dependsOn
        expected.properties == task2.properties
    }
}
