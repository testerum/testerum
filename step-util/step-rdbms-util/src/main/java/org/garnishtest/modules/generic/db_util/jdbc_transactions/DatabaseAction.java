package org.garnishtest.modules.generic.db_util.jdbc_transactions;

import java.sql.Connection;

public interface DatabaseAction {

    void doInTransaction(final Connection connection) throws Exception;

}
