package com.aiguibin.business.service;

import com.aiguibin.business.mapper.FruitMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service(version = "1.0.0", timeout = 10000)
public class IFruitServiceImpl implements IFruitService {
    @Autowired
    private FruitMapper fruitMapper;
    @Override
    public void addFruit() {
        fruitMapper.addFruit();
    }

    @Override
    public void delFruit() {
        fruitMapper.delFruit();
    }

    @Override
    public void megrFruit() {
        fruitMapper.megrFruit();
    }

    @Override
    public List<?> findFruit() {
        return fruitMapper.findFruit();
    }
}
