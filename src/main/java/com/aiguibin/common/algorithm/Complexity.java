package com.aiguibin.common.algorithm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.List;

/**
 * 有关时间复杂度，空间复杂度以及算法思想
 *
 * @author AIguibin
 * Date time 2019年04月20日 09:18:20
 */
public class Complexity {
    //日志声明
    private static final Log logger = LogFactory.getLog(Complexity.class);


    /**
     * 时间复杂度的计算方法
     * 1.一般情况下，算法中基本操作重复执行的次数是问题规模n的某个函数，用T(n)表示，
     * 若有某个辅助函数f(n)，使得T(n)/f(n)的极限值（当n趋近于无穷大时）为不等于零的常数，则称f(n)是T(n)的同数量级函数。
     * 记作T(n)=O(f(n))，称O(f(n)) 为算法的渐进时间复杂度，简称时间复杂度。
     * 随着模块n的增大，算法执行的时间的增长率和 f(n) 的增长率成正比，所以 f(n) 越小，算法的时间复杂度越低，算法的效率越高。
     * 2. 在计算时间复杂度的时候，先找出算法的基本操作，然后根据相应的各语句确定它的执行次数，
     * 再找出 T(n) 的同数量级（它的同数量级有以下：1，log2n，n，n log2n ，n的平方，n的三次方，2的n次方，n!），
     * 找出后，f(n) = 该数量级，若 T(n)/f(n) 求极限可得到一常数c，则时间复杂度T(n) = O(f(n))
     * 3. 分析
     * 则有T(n)=n^2+n^3 根据上面括号里的同数量级，我们可以确定 n的三次方 为T（n）的同数量级
     * 则有f(n)=n^3然后根据 T(n)/f(n) 求极限可得到常数c
     * 则该算法的时间复杂度：T(n) = O(n^3) 注：n^3即是n的3次方。
     * 容易计算的方法是：看看有几重for循环，只有一重则时间复杂度为O(n)，二重则为O(n^2)，
     * 依此类推，如果有二分则为O(logn)，二分例如快速幂、二分查找，如果一个for循环套一个二分，
     * 那么时间复杂度则为O(nlogn)。
     *
     * @param n 数据
     * @author AIguibin
     * Date time 2019/4/20 10:05
     */
    public static void timeComplexity(int n) {
        int a[][] = null;
        int b[][] = null;
        int c[][] = null;
        for (int i = 1; i <= n; ++i) {
            for (int j = 1; j <= n; ++j) {
                c[i][j] = 0;//该步骤属于基本操作执行次数：n的平方次
                for (int k = 1; k <= n; ++k)
                    c[i][j] += a[i][k] * b[k][j];//该步骤属于基本操作执行次数：n的三次方次
            }
        }
    }

    /**
     * 将数组中为 0 的元素都移动到数组末尾，且非零元素顺序不变
     * 数组的 toString 用 Arrays.toString()方法
     *
     * @param array 原数组
     * @return 新的数组
     * @author AIguibin
     * Date time 2019/4/20 9:22
     */
    public static int[] moveZero2Array(int[] array) {
        int k = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] != 0) {
                array[k] = array[i];
                k++;
            }
        }
        for (int i = k; i < array.length; i++) {
            array[i] = 0;
        }
        return array;
    }

    /**
     * 问题：将数组中为 0 的元素都移动到数组末尾，且非零元素顺序不变
     *
     * 算法复杂度分为时间复杂度和空间复杂度:
     * 时间复杂度是指执行算法所需要的计算工作量；
     * 空间复杂度是指执行这个算法所需要的内存空间。
     * 空间复杂度：算法在运行过程中临时占用存储空间大小的量度，S(n)=O(f(n));
     * 时间复杂度：
     *
     * @param array 原数组
     * @return 集合
     * @author AIguibin
     * Date time 2019/4/20 9:45
     */

    public static List<Integer> moveZero2List(int[] array) {
        return null;
    }


    public static void main(String[] args) {
        int[] array = {0, 1, 0, 3, 16};
        String s = Arrays.toString(moveZero2Array(array));
        System.out.println(s);
    }
}
