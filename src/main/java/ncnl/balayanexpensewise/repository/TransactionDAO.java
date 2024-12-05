package ncnl.balayanexpensewise.repository;

import ncnl.balayanexpensewise.beans.Transaction;

import java.util.List;

public interface TransactionDAO {
    void addTransaction(String comboBoxValue, Transaction transaction);


}
