package com.fig.block.procedure

import com.fig.util.Neo4jTaskAdapter
import spock.lang.Specification

import static com.fig.domain.TaskBuilder.task
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/24/13
 * Time: 7:50 PM
 */
class TaskCreatorSpec extends Specification{

    def "Task creator"() {
        given:
        def creator = Spy(TaskCreator)
        def adapter = Mock(Neo4jTaskAdapter)
        creator.getAdapter() >> adapter

        def task1 = task("Task1").build();
        def task2 = task("Task2").build();
        when:
        creator.value([task1, task2])

        then:
        2 * adapter.createTask(_)
        2 * adapter.createTaskDependencies(_)
    }
}
