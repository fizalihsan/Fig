package com.fig.block.procedure
import com.fig.util.Neo4jTaskAdapter
import spock.lang.Specification

import static com.gs.collections.impl.tuple.Tuples.pair
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

        def pair1 = pair("abc", "xyz");
        def pair2 = pair("abc", "abc");
        when:
        creator.value([pair1, pair2])

        then:
        0 * adapter.createTaskDependency("abc", "abc")
        1 * adapter.createTaskDependency("abc", "xyz")
    }
}
