package com.fig.block.procedure
import com.fig.util.Neo4jTaskAdapter
import spock.lang.Specification

import static com.fig.domain.TaskBuilder.task
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/24/13
 * Time: 8:04 PM
 */
class TaskDeletorSpec extends Specification{

    def "Task deletion"() {
        given:
        def deletor = Spy(TaskDeletor)
        def adapter = Mock(Neo4jTaskAdapter)
        deletor.getAdapter() >> adapter

        def task1 = task("Task1").build();
        def task2 = task("Task2").build();
        when:
        deletor.value(["Task1", "Task2"])
        then:
        2 * adapter.deleteTask(_)
    }
}