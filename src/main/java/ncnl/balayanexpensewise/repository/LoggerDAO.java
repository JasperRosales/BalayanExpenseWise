package ncnl.balayanexpensewise.repository;

import ncnl.balayanexpensewise.beans.TransactionLogger;

import java.util.List;

public interface LoggerDAO {
     void logTransaction(String tableName, TransactionLogger transactionLogger);
     List<TransactionLogger> getTransactionLogs(String tableName);
}
