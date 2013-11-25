package com.fig.block.procedure
import com.fig.util.Neo4jTaskAdapter
import spock.lang.Specification
import org.neo4j.graphdb.Node

import static com.fig.domain.TaskBuilder.task
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/24/13
 * Time: 8:30 PM
 */
class TaskPropertyDeletorSpec extends Specification {
    def "Task property deletor"() {
        given:
        def creator = Spy(TaskPropertyDeletor)
        def adapter = Mock(Neo4jTaskAdapter)
        creator.getAdapter() >> adapter

        def task1 = task("Task1").properties(["key11":"value11", "key12":"value12"]).build();
        def task2 = task("Task2").properties(["key21":"value21"]).build();

        def node1 = Mock(Node)
        def node2 = Mock(Node)
        adapter.getNode("Task1") >> node1
        adapter.getNode("Task2") >> node2
        when:
        creator.value([task1, task2])

        then:
        1 * node1.removeProperty("key11")
        1 * node1.removeProperty("key12")
        1 * node2.removeProperty("key21")
    }
}
