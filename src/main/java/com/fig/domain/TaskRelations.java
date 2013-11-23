package com.fig.domain;

import org.neo4j.graphdb.RelationshipType;

/**
 * Defines the relationship types between tasks
 * User: Fizal
 * Date: 11/22/13
 * Time: 7:33 PM
 */
public enum TaskRelations implements RelationshipType {
    DEPENDS_ON
}
