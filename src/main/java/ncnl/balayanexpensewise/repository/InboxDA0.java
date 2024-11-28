package ncnl.balayanexpensewise.repository;

import ncnl.balayanexpensewise.beans.Inbox;

public interface InboxDA0 {
    void addInboxRecord(Inbox inbox);
    void updateInboxRecord(int id, String remarks);
    void deleteInboxRecord(int id);

}
