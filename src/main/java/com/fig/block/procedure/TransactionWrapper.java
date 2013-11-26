package com.fig.block.procedure;

import com.fig.annotations.ThreadSafe;
import com.fig.annotations.Transactional;
import com.fig.manager.Neo4jTaskAdapter;
import com.google.common.annotations.VisibleForTesting;
import com.gs.collections.api.block.procedure.Procedure;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common procedure to wrap any action within a transaction.
 * User: Fizal
 * Date: 11/24/13
 * Time: 6:34 PM
 */
@ThreadSafe
public class TransactionWrapper <T> implements Procedure<T> {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionWrapper.class);
    private final Neo4jTaskAdapter adapter = new Neo4jTaskAdapter();
    private final Procedure<T> procedure;

    public TransactionWrapper(Procedure<T> procedure) {
        this.procedure = procedure;
    }

    @Transactional
    @Override
    public void value(T value) {
        final Transaction tx = getAdapter().beginTransaction();
        try {
            getProcedure().value(value);
            tx.success();
        } catch(Exception e) {
            tx.failure();
            throw new RuntimeException("Error in transaction - Rolling back: ", e);
        } finally {
            tx.finish();
        }
    }

    @VisibleForTesting
    Procedure<T> getProcedure() {
        return procedure;
    }

    @VisibleForTesting
    Neo4jTaskAdapter getAdapter() {
        return adapter;
    }
}

