package com.aiguibin.design.strategy;
/**
 * 策略模式的实现
 *
 * @author AIguibin
 * Date time 2019年04月21日 15:55:37
 */
public class OperationAdd implements Operation{
    @Override
    public int doOperation(int num1, int num2) {
        return num1 + num2;
    }
}