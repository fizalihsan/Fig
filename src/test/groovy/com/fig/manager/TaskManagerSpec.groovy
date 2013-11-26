package com.fig.manager

import org.neo4j.test.TestGraphDatabaseFactory
import spock.lang.Shared
import spock.lang.Specification

import static com.fig.domain.TaskBuilder.task
import static com.google.common.collect.Sets.newHashSet
import static com.gs.collections.impl.tuple.Tuples.twin
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/26/13
 * Time: 4:45 AM
 */
class TaskManagerSpec extends Specification {

    private static def task1 = task("Task1").properties(["11":"11", "12":"12"]).build()
    private static def task2 = task("Task2").properties(["21":"21", "22":"22"]).build()
    private static def task3 = task("Task3").properties(["31":"31", "32":"32"]).dependsOn(newHashSet("Task1", "Task2")).build()
    private static def task4 = task("Task4").properties(["41":"41", "42":"42"]).dependsOn(newHashSet("Task4")).build()

    @Shared def neo4jHelper
    @Shared def mgr = new TaskManager();

    def setupSpec(){
        neo4jHelper = new Neo4jHelper()
        Neo4jHelper.setInstance(neo4jHelper)
    }

    def setup(){
        def graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase()
        neo4jHelper.setGraphDb(graphDb)
        neo4jHelper.createNodeNameIndex()

        mgr.createTasks([task1, task2, task3, task4])
    }

    def cleanup(){
        neo4jHelper.getGraphDb().shutdown()
    }

    def "create tasks"() {
        when: println ''
        then:
        def tasks = mgr.getTasks(["Task1", "Task2", "Task3", "Task4"] as Set)
        tasks.size() == 4
    }

    def "getTask"() {
        when: def task = mgr.getTask("Task1")
        then: task.name == "Task1"
    }

    def "getTasks"() {
        when: def tasks = mgr.getTasks(["Task1", "Task2", "Task5"] as Set)
        then: tasks.size() == 2
    }

    def "updateTaskProperties"() {
        when: mgr.updateTaskProperties([task("Task1").properties(["11":"1100", "13":"1300"]).build()] as Set);
        then:
        def task = mgr.getTask("Task1")
        def map = task.properties
        map.keySet().sort() == ["11", "12", "13"]
        map["11"] == "1100"
        map["12"] == "12"
        map["13"] == "1300"
    }

    def "deleteTasks"() {
        when: mgr.deleteTasks(["Task1"] as Set);
        then:
        def tasks = mgr.getTasks(["Task1", "Task2", "Task3", "Task4"] as Set)
        tasks.size() == 4 //TODO after implementing delete logic, fix this to 3 nodes instead of 4
    }

    def "createTaskDependencies"() {
        when: mgr.createTaskDependencies([twin("Task4", "Task1")])
        then:
        def task = mgr.getTask("Task4")
        task.dependsOn.size() == 2
        task.dependsOn.contains("Task1")
    }

    def "deleteDependencies"() {
        when: mgr.deleteDependencies([twin("Task4", "Task4")])
        then: mgr.getTask("Task4").dependsOn.isEmpty()
    }
}
