package com.fig.block.procedure
import com.fig.util.Neo4jTaskAdapter
import com.gs.collections.api.block.procedure.Procedure
import org.neo4j.graphdb.Transaction
import spock.lang.Specification

import static com.fig.domain.TaskBuilder.task
/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/24/13
 * Time: 8:41 PM
 */
class TransactionWrapperSpec extends Specification {
    def "Successful transaction"() {
        given:
        def txnWrapper = Spy(TransactionWrapper)
        def adapter = Spy(Neo4jTaskAdapter)
        def txn = Mock(Transaction)
        def procedure = Mock(Procedure)
        txnWrapper.getAdapter() >> adapter
        txnWrapper.getProcedure() >> procedure
        adapter.beginTransaction() >> txn

        def task1 = task("Task1").build();

        when:
        txnWrapper.value([task1])

        then:
        1 * txn.success()
        1 * txn.finish()
    }

    def "Failed transaction"() {
        given:
        def txnWrapper = Spy(TransactionWrapper)
        def adapter = Spy(Neo4jTaskAdapter)
        def txn = Mock(Transaction)
        def procedure = Mock(Procedure)
        txnWrapper.getAdapter() >> adapter
        txnWrapper.getProcedure() >> procedure
        adapter.beginTransaction() >> txn
        procedure.value(_) >> {throw new Exception()}

        def task1 = task("Task1").build();

        when:
        txnWrapper.value([task1])

        then:
        1 * txn.failure()
        thrown(RuntimeException)
        1 * txn.finish()
    }
}
