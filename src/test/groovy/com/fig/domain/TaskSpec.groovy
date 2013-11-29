package com.fig.domain

import spock.lang.Specification

import static com.fig.domain.TaskBuilder.task
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/29/13
 * Time: 5:53 PM
 */
class TaskSpec extends Specification {

    def "remove null properties"() {
        expect:
        input.dropNullValueProperties()
        input.getProperties().size() == output
        where:
        input                                        | output
        task("a1").properties(["key":null]).build()  | 0
        task("a1").properties(["key1":null, "key2":null]).build()  | 0
        task("a1").properties(["key1":null, "key2":"value2", "key3":null]).build()  | 1
    }
}
