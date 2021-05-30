package com.aiguibin.access;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

public class GeneralAccessorImpl<T> implements GeneralAccessor<T> {

    @Autowired
    private SessionFactory sessionFactory;

    public Session session(){
        return this.sessionFactory.getCurrentSession();
    }


    @Override
    public void save(T t) {
        session().save(t);
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
