package com.fig.manager;

import com.fig.annotations.ThreadSafe;
import com.google.common.base.Joiner;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.Traversal;

/**
 * Convenience class to custom print the nodes and relationships in a traversal path
 * User: Fizal
 * Date: 11/26/13
 * Time: 6:41 PM
 */
@ThreadSafe
class PathPrinter implements Traversal.PathDescriptor<Path>{
    private final String nodePropertyKey;

    public PathPrinter( String nodePropertyKey ){
        this.nodePropertyKey = nodePropertyKey;
    }

    @Override
    public String nodeRepresentation( Path path, Node node ){
        return Joiner.on("").join("(", node.getProperty( nodePropertyKey, "" ), ")");
    }

    @Override
    public String relationshipRepresentation( Path path, Node from, Relationship relationship ){
        String prefix, suffix;
        if ( from.equals( relationship.getEndNode() ) ){
            prefix = "<--"; suffix = "--";
        }else{
            prefix = "--"; suffix = "-->";
        }
        return Joiner.on("").join(prefix, "[", relationship.getType().name(), "]", suffix);
    }
}