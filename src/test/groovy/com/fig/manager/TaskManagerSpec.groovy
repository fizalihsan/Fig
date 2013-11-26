package com.fig.manager

import com.fig.domain.Task
import com.fig.util.Neo4jTaskAdapter
import spock.lang.Specification

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/26/13
 * Time: 4:45 AM
 */
class TaskManagerSpec extends Specification {
    def "getTasks"() {
        def mgr = Spy(TaskManager)
        def adapter = Spy(Neo4jTaskAdapter)
        mgr.getAdapter() >> adapter
        adapter.getTask(_) >>> [ Mock(Task), null, Mock(Task)]

        when: def tasks = mgr.getTasks(["Task1", "Task2", "Task3"] as Set)
        then:
        tasks.size() == 2
    }
}
