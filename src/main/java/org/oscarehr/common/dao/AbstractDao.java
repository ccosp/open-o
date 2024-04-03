package org.oscarehr.common.dao;

import java.util.List;

import org.oscarehr.common.model.AbstractModel;

public interface AbstractDao<T extends AbstractModel<?>> {
    public static final int MAX_LIST_RETURN_SIZE = 5000;

    void merge(AbstractModel<?> o);

    void persist(AbstractModel<?> o);

    void batchPersist(List<AbstractModel<?>> oList);

    void batchPersist(List<AbstractModel<?>> oList, int batchSize);

    void remove(AbstractModel<?> o);

    void batchRemove(List<AbstractModel<?>> oList);

    void batchRemove(List<AbstractModel<?>> oList, int batchSize);

    void refresh(AbstractModel<?> o);

    T find(Object id);

    T find(int id);

    boolean contains(AbstractModel<?> o);

    List<T> findAll(Integer offset, Integer limit);

    boolean remove(Object id);

    int getCountAll();

    List<Object[]> runNativeQuery(String sql);

    T saveEntity(T entity);

    Class<T> getModelClass();
}
