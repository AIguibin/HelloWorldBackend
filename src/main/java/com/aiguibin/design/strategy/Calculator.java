package com.aiguibin.design.strategy;

/**
 * 实现策略模式的核心类
 * 使用计算器类时，如果要进行加法运算，就 New 一个加法类传入，减法也是同理。
 * 看到这里，相信大家一定会有疑惑，为什么要把加、减、乘、除四则运算分别封装到类中？直接在 Calculator 中写 add() 、sub() 等方法不是更方便吗？甚至如果要添加其他的运算方法，每次都要创建一个类，反而更麻烦。
 * 的确，用了策略模式之后代码比普通写法多了一些，但是这里假设一种场景：把写好的计算器代码打包好作为一个库发布出去给其他人用，其他人发现你的计算器中只有加、减、乘、除四个方法，而他想增加平方、开方等功能，怎么办？
 * 如果是用普通写法写的计算器，想要增加功能唯一的办法就是修改你写好的 Calculator ，增加平方和开方两个 method 。
 * 可是你提供的是一个 jar 包啊，jar 包，jar…jar…jar…jar…包……
 * 就算你提供的是源码，你希望其他人可以随意修改你写好的代码吗？一般我们发布出去的开源框架或库都是经过千锤百炼、经过测试的代码，其他人随意修改我们的源码很容易产生不可预知的错误。
 * 如果你用的是策略模式，那么其他人想要增加平方或开平方功能，只需要自己定义一个类实现你的 Operation 接口，然后调用 calculator.setOperation(new 平方类()); 即可。
 * 看到这里相信你已经对策略模式有了一定的好感，甚至惊叹一声：哇，还有这种操作？
 * 顺便提一嘴，这里很好的体现了一个设计模式的基本原则：开闭原则。开闭原则说的是 ” 对修改关闭、对扩展开放 “ 。对修改关闭就是不希望别人修改我们的代码，此路不通，对扩展开放就是希望别人以扩展的方式增加功能，策略模式把开闭原则体现得淋漓尽致。
 *
 * @author AIguibin
 * Date time 2019年04月21日 15:56:43
 */
public class Calculator {
    private Operation operation;

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public int doOperation(int num1, int num2) {
        return this.operation.doOperation(num1, num2);
    }
}