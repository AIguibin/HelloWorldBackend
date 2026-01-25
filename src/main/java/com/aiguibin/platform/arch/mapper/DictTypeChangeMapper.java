package com.aiguibin.platform.arch.mapper;

import com.aiguibin.platform.arch.dto.ChangeQueryDTO;
import com.aiguibin.platform.arch.entity.DictTypeChange;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 字典类型变更Mapper
 */
@Mapper
public interface DictTypeChangeMapper extends BaseMapper<DictTypeChange> {

    /**
     * 分页查询变更记录
     */
    @Select("SELECT * FROM biz_ddct_type_change WHERE is_deleted = 0 ORDER BY apply_time DESC LIMIT #{offset}, #{limit}")
    List<DictTypeChange> selectByPage(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 计算查询总数
     */
    @Select("SELECT COUNT(*) FROM biz_ddct_type_change WHERE is_deleted = 0")
    Long selectCount();

    /**
     * 获取待审批的变更列表
     */
    @Select("SELECT * FROM biz_ddct_type_change " +
            "WHERE approve_status = 'PENDING' " +
            "AND is_deleted = 0 " +
            "ORDER BY apply_time ASC")
    List<DictTypeChange> selectPendingApproval();

    /**
     * 获取待执行的变更列表
     */
    @Select("SELECT * FROM biz_ddct_type_change " +
            "WHERE approve_status = 'APPROVED' " +
            "AND execute_status = 'PENDING' " +
            "AND is_deleted = 0 " +
            "ORDER BY approve_time ASC")
    List<DictTypeChange> selectPendingExecute();

    /**
     * 更新审批状态
     */
    @Update("UPDATE biz_ddct_type_change SET " +
            "approve_status = #{approveStatus}, " +
            "approve_user = #{approveUser}, " +
            "approve_time = #{approveTime}, " +
            "approve_remark = #{approveRemark}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    int updateApproveStatus(@Param("id") String id, @Param("approveStatus") String approveStatus, 
                           @Param("approveUser") String approveUser, @Param("approveTime") String approveTime, 
                           @Param("approveRemark") String approveRemark);

    /**
     * 更新执行状态
     */
    @Update("UPDATE biz_ddct_type_change SET " +
            "execute_status = #{executeStatus}, " +
            "execute_user = #{executeUser}, " +
            "execute_time = #{executeTime}, " +
            "execute_result = #{executeResult}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    int updateExecuteStatus(@Param("id") String id, @Param("executeStatus") String executeStatus, 
                           @Param("executeUser") String executeUser, @Param("executeTime") String executeTime, 
                           @Param("executeResult") String executeResult);

    /**
     * 更新变更统计信息
     */
    @Update("UPDATE biz_ddct_type_change SET " +
            "item_add_count = #{itemAddCount}, " +
            "item_mod_count = #{itemModCount}, " +
            "item_del_count = #{itemDelCount}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    int updateChangeStatistics(@Param("id") String id, @Param("itemAddCount") Integer itemAddCount, 
                              @Param("itemModCount") Integer itemModCount, @Param("itemDelCount") Integer itemDelCount);
}
