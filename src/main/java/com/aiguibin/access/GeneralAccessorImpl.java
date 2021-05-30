package com.aiguibin.access;

import org.hibernate.criterion.DetachedCriteria;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

public class GeneralAccessorImpl<T> implements GeneralAccessor<T> {

    @PersistenceContext
    protected EntityManager entityManager;

    @Override
    public void save(T t) {

    }

    @Override
    public void update(T t) {

    }

    @Override
    public void delete(T t) {

    }

    @Override
    public void saveOrUpdate(T t) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public T getById(Serializable id) {
        return null;
    }

    @Override
    public Integer getTotalCount(DetachedCriteria dc) {
        return null;
    }

    @Override
    public List<T> getPageList(DetachedCriteria dc, Integer start, Integer pageSize) {
        return null;
    }
}
