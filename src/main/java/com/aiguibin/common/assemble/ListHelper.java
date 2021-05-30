package com.aiguibin.common.assemble;


import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log4j2
public class ListHelper {
    /**
     * List去重
     */
    public static List<?> deleteRepeat4ArrayList(List<Object> list){
        Set<?> set =new HashSet<>(list);
        list.clear();
        list.addAll(set);
        return list;
    }
}
