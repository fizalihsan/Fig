package com.fig.block.procedure;

import com.fig.annotations.ThreadSafe;
import com.fig.block.predicate.TwinCheckPredicate;
import com.fig.util.Neo4jTaskAdapter;
import com.google.common.annotations.VisibleForTesting;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.tuple.Pair;

import java.util.Collection;

/**
 * Re-usable procedure to create task dependencies.
 * User: Fizal
 * Date: 11/24/13
 * Time: 6:26 PM
 */
@ThreadSafe
public class TaskDependencyCreator implements Procedure<Collection<Pair<String, String>>> {
    private static final TwinCheckPredicate<String, String> SELF_DEPENDENCY_CHECK = new TwinCheckPredicate<>();

    private final Neo4jTaskAdapter adapter = new Neo4jTaskAdapter();

    @Override
    public void value(Collection<Pair<String, String>> taskNamePairs) {
        for (Pair<String, String> taskNamePair : taskNamePairs) {
            boolean noSelfDependency = !SELF_DEPENDENCY_CHECK.accept(taskNamePair); //Restrict self-dependency

            //TODO implement logic to restrict cyclic-dependency
            boolean noCyclicDependency = true; //Restrict cyclic-dependency

            if(noSelfDependency && noCyclicDependency){
                getAdapter().createTaskDependency(taskNamePair.getOne(), taskNamePair.getTwo());
            }
        }
    }

    @VisibleForTesting
    Neo4jTaskAdapter getAdapter() {
        return adapter;
    }
}