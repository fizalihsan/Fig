package com.fig.block.predicate;

import com.fig.annotations.ThreadSafe;
import com.fig.domain.TaskDependency;
import com.gs.collections.api.block.predicate.Predicate;

/**
 * Predicate to check if the given task dependency has any dependency to self.
 * User: Fizal
 * Date: 11/24/13
 * Time: 6:19 PM
 */

@ThreadSafe
public class SelfTaskDependencyCheckPredicate implements Predicate<TaskDependency> {

    /**
     * @param taskNamePair
     * @return true, if there is no self dependency
     */
    @Override
    public boolean accept(TaskDependency dependency) {
        for (String toTask : dependency.getToTasks()) {
            if(toTask.equals(dependency.getFromTask())){
                return false;
            }
        }
        return true;
    }
}
