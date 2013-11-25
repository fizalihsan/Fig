package com.fig.block.procedure
import com.fig.util.Neo4jTaskAdapter
import spock.lang.Specification

import static com.fig.domain.TaskBuilder.task
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/24/13
 * Time: 8:37 PM
 */
class TaskPropertyUpdatorSpec extends Specification {
    def "Task property updator"() {
        given:
        def creator = Spy(TaskPropertyUpdator)
        def adapter = Mock(Neo4jTaskAdapter)
        creator.getAdapter() >> adapter

        def task1 = task("Task1").build();
        def task2 = task("Task2").build();
        when:
        creator.value([task1, task2])

        then:
        1 * adapter.updateTaskProperties(task1)
        1 * adapter.updateTaskProperties(task2)
    }
}
