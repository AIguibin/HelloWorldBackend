package com.aiguibin.online.table.config;

import com.aiguibin.online.table.entity.DictItem;
import com.aiguibin.online.table.entity.DictType;
import com.aiguibin.online.table.entity.User;
import com.aiguibin.online.table.mapper.DictItemMapper;
import com.aiguibin.online.table.mapper.DictTypeMapper;
import com.aiguibin.online.table.mapper.UserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataBootstrap implements CommandLineRunner {
    @Resource
    private UserMapper userMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private DictTypeMapper dictTypeMapper;
    @Resource
    private DictItemMapper dictItemMapper;

    @Override
    public void run(String... args) {
        // 初始化管理员用户
        Long count = userMapper.selectCount(null);
        if (count == null || count == 0L) {
            User u = new User();
            u.setUsernumb("admin");
            u.setUsername("admin");
            u.setPassword(passwordEncoder.encode("666666"));
            u.setRole("ADMIN");
            userMapper.insert(u);
        }
        
        // 初始化字典数据
        initDictData();
    }
    
    /**
     * 初始化字典数据
     */
    private void initDictData() {
        String operator = "system";
        LocalDateTime now = LocalDateTime.now();
        
        // 1. 初始化字典类型
        initDictType("CURRENT_STATUS", "当前状态", "代码脚本变更记录的当前状态", operator, now);
        initDictType("DEVELOP_TYPE", "开发类别", "开发工作的类别", operator, now);
        initDictType("YES_NO", "是/否", "通用的是/否选项", operator, now);
        
        // 2. 初始化当前状态字典项
        initDictItem("CURRENT_STATUS", "PENDING_APPROVAL", "待审批", null, null, 1, 1, "", operator, now);
        initDictItem("CURRENT_STATUS", "PENDING_REVIEW", "待评审", null, null, 2, 1, "", operator, now);
        initDictItem("CURRENT_STATUS", "PENDING_MERGE", "待合版", null, null, 3, 1, "", operator, now);
        initDictItem("CURRENT_STATUS", "MERGED", "已合版", null, null, 4, 1, "", operator, now);
        
        // 3. 初始化开发类别字典项
        initDictItem("DEVELOP_TYPE", "FRONTEND", "前端", null, null, 1, 1, "", operator, now);
        initDictItem("DEVELOP_TYPE", "BACKEND", "后端", null, null, 2, 1, "", operator, now);
        initDictItem("DEVELOP_TYPE", "SCRIPT", "脚本", null, null, 3, 1, "", operator, now);
        initDictItem("DEVELOP_TYPE", "CONFIG", "配置", null, null, 4, 1, "", operator, now);
        
        // 4. 初始化是/否字典项
        initDictItem("YES_NO", "YES", "是", null, null, 1, 1, "", operator, now);
        initDictItem("YES_NO", "NO", "否", null, null, 2, 1, "", operator, now);
    }
    
    /**
     * 初始化字典类型
     */
    private void initDictType(String code, String name, String description, String operator, LocalDateTime now) {
        DictType dictType = dictTypeMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<DictType>()
                .eq(DictType::getDictTypeCode, code)
                .eq(DictType::getIsDeleted, 0));
        
        if (dictType == null) {
            dictType = new DictType();
            dictType.setDictTypeCode(code);
            dictType.setDictTypeName(name);
            dictType.setDescription(description);
            dictType.setSort(0);
            dictType.setIsEnabled(1);
            dictType.setCreateUser(operator);
            dictType.setUpdateUser(operator);
            dictType.setCreateTime(now);
            dictType.setUpdateTime(now);
            dictType.setIsDeleted(0);
            
            dictTypeMapper.insert(dictType);
        }
    }
    
    /**
     * 初始化字典项
     */
    private void initDictItem(String typeCode, String value, String label, String groupCode, String groupName, 
                             int sort, int isEnabled, String description, String operator, LocalDateTime now) {
        // 根据字典类型编码获取字典类型ID
        DictType dictType = dictTypeMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<DictType>()
                .eq(DictType::getDictTypeCode, typeCode)
                .eq(DictType::getIsDeleted, 0));
        
        if (dictType != null) {
            // 检查字典项是否已存在
            DictItem dictItem = dictItemMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<DictItem>()
                    .eq(DictItem::getDictTypeCode, typeCode)
                    .eq(DictItem::getDictValue, value)
                    .eq(DictItem::getIsDeleted, 0));
            
            if (dictItem == null) {
                dictItem = new DictItem();
                dictItem.setDictTypeId(dictType.getId());
                dictItem.setDictTypeCode(typeCode);
                dictItem.setDictValue(value);
                dictItem.setDictLabel(label);
                dictItem.setGroupCode(groupCode);
                dictItem.setGroupName(groupName);
                dictItem.setSort(sort);
                dictItem.setIsEnabled(isEnabled);
                dictItem.setDescription(description);
                dictItem.setCreateUser(operator);
                dictItem.setUpdateUser(operator);
                dictItem.setCreateTime(now);
                dictItem.setUpdateTime(now);
                dictItem.setIsDeleted(0);
                
                dictItemMapper.insert(dictItem);
            }
        }
    }
}