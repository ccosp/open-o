package org.oscarehr.common.dao;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public interface QueueDao extends AbstractDao<Queue> {
    HashMap getHashMapOfQueues();
    List<Hashtable> getQueues();
    String getLastId();
    String getQueueName(int id);
    String getQueueid(String name);
    boolean addNewQueue(String qn);
}
