package com.aiguibin.platform.arch.util;

import com.aiguibin.platform.arch.entity.DictItemChange;
import java.util.HashMap;
import java.util.Map;

/**
 * 差异比较工具类
 */
public class DiffUtil {
    
    /**
     * 比较字典项变更差异
     * 返回差异字段的映射
     */
    public static Map<String, Object> compareDictItemChange(DictItemChange itemChange) {
        Map<String, Object> diffMap = new HashMap<>();
        
        if (itemChange == null) {
            return diffMap;
        }
        
        // 比较字典键
        if (!equals(itemChange.getOldDctKey(), itemChange.getNewDctKey())) {
            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put("old", itemChange.getOldDctKey());
            keyMap.put("new", itemChange.getNewDctKey());
            diffMap.put("dctKey", keyMap);
        }
        
        // 比较字典值名称
        if (!equals(itemChange.getOldDctValNm(), itemChange.getNewDctValNm())) {
            Map<String, Object> valNmMap = new HashMap<>();
            valNmMap.put("old", itemChange.getOldDctValNm());
            valNmMap.put("new", itemChange.getNewDctValNm());
            diffMap.put("dctValNm", valNmMap);
        }
        
        // 比较字典值
        if (!equals(itemChange.getOldDctVal(), itemChange.getNewDctVal())) {
            Map<String, Object> valMap = new HashMap<>();
            valMap.put("old", itemChange.getOldDctVal());
            valMap.put("new", itemChange.getNewDctVal());
            diffMap.put("dctVal", valMap);
        }
        
        // 比较字典组
        if (!equals(itemChange.getOldDctGrp(), itemChange.getNewDctGrp())) {
            Map<String, Object> grpMap = new HashMap<>();
            grpMap.put("old", itemChange.getOldDctGrp());
            grpMap.put("new", itemChange.getNewDctGrp());
            diffMap.put("dctGrp", grpMap);
        }
        
        // 字典类型变更在主表中处理，这里不需要比较
        
        // 比较字典描述
        if (!equals(itemChange.getOldDctDsc(), itemChange.getNewDctDsc())) {
            Map<String, Object> dscMap = new HashMap<>();
            dscMap.put("old", itemChange.getOldDctDsc());
            dscMap.put("new", itemChange.getNewDctDsc());
            diffMap.put("dctDsc", dscMap);
        }
        
        // 比较状态码
        if (!equals(itemChange.getOldStcd(), itemChange.getNewStcd())) {
            Map<String, Object> stcdMap = new HashMap<>();
            stcdMap.put("old", itemChange.getOldStcd());
            stcdMap.put("new", itemChange.getNewStcd());
            diffMap.put("stcd", stcdMap);
        }
        
        // 比较排序
        if (!equals(itemChange.getOldDctSeq(), itemChange.getNewDctSeq())) {
            Map<String, Object> seqMap = new HashMap<>();
            seqMap.put("old", itemChange.getOldDctSeq());
            seqMap.put("new", itemChange.getNewDctSeq());
            diffMap.put("dctSeq", seqMap);
        }
        
        return diffMap;
    }
    
    /**
     * 比较两个值是否相等
     */
    private static boolean equals(Object oldValue, Object newValue) {
        if (oldValue == null && newValue == null) {
            return true;
        }
        if (oldValue == null || newValue == null) {
            return false;
        }
        return oldValue.equals(newValue);
    }
}