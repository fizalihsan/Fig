package com.fig.block.predicate

import spock.lang.Specification

import static com.gs.collections.impl.tuple.Tuples.pair
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/24/13
 * Time: 7:36 PM
 */
class TwinCheckPredicateSpec extends Specification{
    def "Twin check"() {
        def predicate = new TwinCheckPredicate<String, String>()
        expect:
        predicate.accept(pair(first, second)) == output

        where:
        first | second | output
        "abc" | "abc"  | true
        "abc" | "xyz"  | false
    }
}
