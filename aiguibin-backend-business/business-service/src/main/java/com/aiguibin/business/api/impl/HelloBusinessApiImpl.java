package com.aiguibin.business.api.impl;

import com.aiguibin.business.api.HelloBusinessApi;
import com.aiguibin.business.service.IFruitService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class HelloBusinessApiImpl implements HelloBusinessApi {
    @Autowired
    private IFruitService IFruitService;

    @Override
    public void addFruit() {
        IFruitService.addFruit();
    }

    @Override
    public void delFruit() {
        IFruitService.delFruit();
    }

    @Override
    public void megrFruit() {
        IFruitService.megrFruit();
    }

    @Override
    public List<?> findFruit() {
        return IFruitService.findFruit();
    }
}
