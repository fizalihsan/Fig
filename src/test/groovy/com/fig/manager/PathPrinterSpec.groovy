package com.fig.manager

import com.fig.domain.TaskRelations
import org.neo4j.graphdb.Relationship
import spock.lang.Specification
import org.neo4j.graphdb.Node
import static com.fig.domain.FigConstants.TASK_NAME
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/26/13
 * Time: 9:01 PM
 */
class PathPrinterSpec extends Specification {
    private static final def PRINTER = new PathPrinter(TASK_NAME)

    def "nodeRepresentation"() {
        def node = Mock(Node)
        node.getProperty(TASK_NAME, "") >> "nodename"

        when: def representation = PRINTER.nodeRepresentation(null, node)
        then: representation == "(nodename)"
    }

    def "relationshipRepresentation - Is end node"() {
        def node = Mock(Node)
        node.getProperty(TASK_NAME, "") >> "nodename"
        def rel = Mock(Relationship)
        def endNode = Mock(Node)
        rel.getEndNode() >> endNode
        rel.getType() >> TaskRelations.DEPENDS_ON

        node.equals(endNode) >> true

        when: def representation = PRINTER.relationshipRepresentation(null, node, rel)
        then: representation == "<--[DEPENDS_ON]--"
    }

    def "relationshipRepresentation - Is not end node"() {
        def node = Mock(Node)
        node.getProperty(TASK_NAME, "") >> "nodename"
        def rel = Mock(Relationship)
        def endNode = Mock(Node)
        rel.getEndNode() >> endNode
        rel.getType() >> TaskRelations.DEPENDS_ON

        node.equals(endNode) >> false

        when: def representation = PRINTER.relationshipRepresentation(null, node, rel)
        then: representation == "--[DEPENDS_ON]-->"
    }
}
