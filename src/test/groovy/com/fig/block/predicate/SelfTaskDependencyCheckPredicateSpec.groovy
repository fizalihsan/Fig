package com.fig.block.predicate
import com.fig.domain.TaskDependency
import spock.lang.Specification
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/24/13
 * Time: 7:36 PM
 */
class SelfTaskDependencyCheckPredicateSpec extends Specification{
    def "Self Task Dependency Check"() {
        def predicate = new SelfTaskDependencyCheckPredicate()
        expect:
        predicate.accept(new TaskDependency(from, to)) == output

        where:
        from | to | output
        "abc" | ["abc"]         | false
        "abc" | ["abc", "xyz"]  | false
        "abc" | ["xyz", "abc"]  | false
        "abc" | ["xyz"]         | true
        "abc" | ["xyz", "def"]  | true
    }
}
