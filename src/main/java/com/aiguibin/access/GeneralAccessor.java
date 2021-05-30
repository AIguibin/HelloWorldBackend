package com.aiguibin.access;

import org.hibernate.criterion.DetachedCriteria;

import java.io.Serializable;
import java.util.List;

public interface GeneralAccessor<T> {
    /**
     * 保存一个实体
     * @param t 实体
     */
    void save(T t);

    void update(T t);
    void delete(T t);
    void saveOrUpdate(T t);
    void delete(Serializable id);

    T getById(Serializable id);

    Integer getTotalCount(DetachedCriteria dc);

    List<T> getPageList(DetachedCriteria dc, Integer start,Integer pageSize);
}
