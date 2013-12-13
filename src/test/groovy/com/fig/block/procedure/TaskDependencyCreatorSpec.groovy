package com.fig.block.procedure
import com.fig.domain.TaskDependency
import com.fig.manager.Neo4jTaskAdapter
import spock.lang.Specification
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/24/13
 * Time: 8:07 PM
 */
class TaskDependencyCreatorSpec extends Specification{
    def "Task dependency creator"() {
        given:
        def creator = Spy(TaskDependencyCreator)
        def adapter = Mock(Neo4jTaskAdapter)
        creator.getAdapter() >> adapter

        def pair1 = new TaskDependency("abc", ["abc", "xyz"]);
        def pair2 = new TaskDependency("abc", ["xyz", "def"]);
        when:
        creator.value([pair1, pair2])

        then:
        4 * adapter.createTaskDependency(_, _)
    }
}
