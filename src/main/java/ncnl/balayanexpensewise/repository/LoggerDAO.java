package ncnl.balayanexpensewise.repository;

import ncnl.balayanexpensewise.beans.TransactionLogger;

import java.util.List;

public interface LoggerDAO {
     List<TransactionLogger> getTransactionLogs(String tableName);
}
