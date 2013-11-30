package com.fig.block.procedure
import com.fig.domain.TaskDependency
import com.fig.manager.Neo4jTaskAdapter
import spock.lang.Specification
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/24/13
 * Time: 8:24 PM
 */
class TaskDependencyDeletorSpec extends Specification {
    def "Task dependency deletor"() {
        given:
        def creator = Spy(TaskDependencyDeletor)
        def adapter = Mock(Neo4jTaskAdapter)
        creator.getAdapter() >> adapter

        def pair1 = new TaskDependency("abc1", ["xyz1"]);
        def pair2 = new TaskDependency("abc2", ["xyz2"]);
        when:
        creator.value([pair1, pair2])

        then:
        1 * adapter.deleteTaskDependency("abc1", "xyz1")
        1 * adapter.deleteTaskDependency("abc2", "xyz2")
    }
}
