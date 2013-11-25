package com.fig.block.predicate;

import com.fig.annotations.ThreadSafe;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.tuple.Pair;

/**
 * Predicate to check if the given pair is a twin (true as per equals() method)
 * User: Fizal
 * Date: 11/24/13
 * Time: 6:19 PM
 */

@ThreadSafe
public class TwinCheckPredicate <A, B> implements Predicate<Pair<A, B>> {

    /**
     * @param taskNamePair
     * @return true, if two strings are twins or equal to each other.
     */
    @Override
    public boolean accept(Pair<A, B> taskNamePair) {
        return taskNamePair.getOne().equals(taskNamePair.getTwo());
    }
}
