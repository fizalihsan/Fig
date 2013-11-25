package com.fig.block.procedure
import com.fig.util.Neo4jTaskAdapter
import spock.lang.Specification

import static com.gs.collections.impl.tuple.Tuples.pair
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

        def pair1 = pair("abc1", "xyz1");
        def pair2 = pair("abc2", "xyz2");
        when:
        creator.value([pair1, pair2])

        then:
        1 * adapter.deleteTaskDependency("abc1", "xyz1")
        1 * adapter.deleteTaskDependency("abc2", "xyz2")
    }
}
