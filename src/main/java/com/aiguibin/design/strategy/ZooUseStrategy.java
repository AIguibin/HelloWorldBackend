package com.aiguibin.design.strategy;

/**
 * 策略模式的使用样例
 *
 * @author AIguibin
 * Date time 2019年04月21日 15:50:36
 */
public class ZooUseStrategy {

    public static void main(String[] args) {
        Calculator calculatorAdd = new Calculator();
        calculatorAdd.setOperation(new OperationAdd());
        int resultAdd = calculatorAdd.doOperation(1,2);
        System.out.println(resultAdd);
        Calculator calculatorSub = new Calculator();
        calculatorSub.setOperation(new OperationSub());
        int resultSub = calculatorSub.doOperation(1,2);
        System.out.println(resultSub);
    }
}
