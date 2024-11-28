package ncnl.balayanexpensewise.repository;

import ncnl.balayanexpensewise.beans.Transaction;

import java.util.List;

public interface TransactionDAO {
    void addTransaction(String comboBoxValue, Transaction transaction);
    void deleteTransaction(int transactionId);
    void updateTransaction(Transaction transaction);
//    Transaction getTransactionById(int transactionId);
//    List<Transaction> getAllTransactions();
}
