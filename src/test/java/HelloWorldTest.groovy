import spock.lang.Specification
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/22/13
 * Time: 11:20 PM
 */
class HelloWorldSpec extends Specification {

    /**
     * Following are the 4 fixture methods
     */
    def setup() { println '  setup' }          // run before every feature method like @org.junit.Before
    def cleanup() { println '  cleanup' }        // run after every feature method like @org.unit.After
    def setupSpec() { println 'setupSpec' }     // run before the first feature method like @org.junit.BeforeClass
    def cleanupSpec() { println 'cleanupSpec' }   // run after the last feature method like @org.junit.AfterClass

    //Feature method names are string literals. Give meaningful names to them.
    def "Example 1: expect-where model"() {
        expect: "stimulus. Use block descriptions like this to describe a block"
        a == b
        where: "response expected"
        a | b
        1 | 1
    }

    //Features methods should at least have one of the six blocks:
    //setup, expect, where, when, then, cleanup
    def "Example 2: expect-where model with explicit setup block"() {
        setup: "explicit set up block. should be 1st block in a feature. setup: label is optional."
        def stack = new Stack()
        expect: a == b
        where: "where block is always the last block and not repeated"
        a | b; 1 | 1;
    }

    def "Example 3: expect-where model with implicit setup block"() {
        println 'implicit set up' //should be 1st block in a feature. setup: label is optional.
        expect: a == b
        cleanup: 'custom clean up method'
        where: a | b; 1 | 1;
    }

    def "Example 4: when-then model"(){ //when and then always appear together
        setup: "create new stack"
        def stack = new Stack()
        when: "stimulus"
        //may contain any arbitrary code
        stack.pop()

        then: "response - only conditions, exception conditions, interactions, and variable definitions are allowed here"
        true != false //Condition: Evaluated according to Groovy truthness
        thrown(EmptyStackException) //Exception condition - only 1 is allowed in a block
        stack.empty //other conditions can follow exception condition

        cleanup: "custom clean up - should be the last block in when-then model."
    }

    def "Example 5: when-then model continued"(){
        setup:
        def stack = new Stack()
        when: "when and then always appear together"
        stack.pop()

        then: "Alternative exception condition. Another way is using notThrown(exception)"
        EmptyStackException e = thrown()
        e.cause == null
    }

    def "Example 6: given-when-then model "(){ //when and then always appear together
        given: "BDD stories are well described in given-when-then model. given: is just an alias for setup:"
        def stack = new Stack()
        and: "and: is used to describe individual parts of a block"
        when:
        stack.push(1)
        and: "another and: block inside when:"
        println 'hello'

        then: "Another way of exception condition is using notThrown(exception)"
        notThrown(NullPointerException)
    }

    def "Example 7: expect only model"(){
        expect: "more limited compared to 'then' block. may only contain conditions and variable definitions."
        12 > 10 // good for single line expressions. Use this model to express purely functional methods.
    }

    def "Directives"(){
        /*
        @Timeout - Sets a timeout for execution of a feature or fixture method.
        @Ignore - Ignores a feature method.
        @IgnoreRest - Ignores all feature methods not carrying this annotation. Useful for quickly running just a single method.
        @FailsWith - Expects a feature method to complete abruptly. It has two use cases:
                    First, to document known bugs that cannot be resolved immediately.
                    Second, to replace exception conditions in certain corner cases where the latter cannot be used
                    (like specifying the behavior of exception conditions). In all other cases, exception conditions are preferable.
        */
    }

    def "Example 8: Simple Parameterization"(){
        expect: a + b == c
        where: "triggers 3 iterations with element (i) from each list"
        a << [1,2,3]
        b << [2,3,4]
        c << [3,5,7]
    }

    def "Example: Interactions example - events are published to all subscribers"() {
        setup: "Interactions outside of then: are global whose scope is within the feature method"
        def subscriber1 = Mock(Subscriber) //dynamic mocking
        Subscriber subscriber2 = Mock() //static mocking
        def publisher = new PublisherImpl()
        publisher.add(subscriber1)
        publisher.add(subscriber2)
        subscriber1.isAlive() >> true

        when:
        publisher.send("event")
        subscriber1.isAlive()

        then: "Optional and required interactions"

        //cardinality is mandatory for required interactions
        // n * subscriber.receive(event)      // exactly n times
        // (n.._) * subscriber.receive(event) // at least n times
        // (_..n) * subscriber.receive(event) // at most n times
        1 * subscriber1.receive("event") //asserts the required interaction happened exactly once
        1 * _.receive("event") // asserts the receive is called on any mock object

        subscriber1.isAlive() >> true // optional interaction
    }
}


interface Publisher{
    void add(Subscriber subscriber)
    void send(event)
}

interface Subscriber{
    void receive(event)
    boolean isAlive()
}

class PublisherImpl implements Publisher{
    def subscribers = []
    @Override
    void add(Subscriber subscriber) {
        subscribers.add(subscriber)
    }

    @Override
    void send(Object event) {
        subscribers.each {subscriber -> subscriber.receive(event)}
    }
}