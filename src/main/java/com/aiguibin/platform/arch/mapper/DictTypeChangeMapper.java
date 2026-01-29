package com.aiguibin.platform.arch.mapper;

import com.aiguibin.platform.arch.dto.ChangeQueryDTO;
import com.aiguibin.platform.arch.entity.DictTypeChange;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;

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
            "approver_num = #{approverNum}, " +
            "approver_name = #{approverName}, " +
            "approve_time = #{approveTime}, " +
            "approve_remark = #{approveRemark}, " +
            "updated_time = NOW() " +
            "WHERE uuid = #{uuid}")
    int updateApproveStatus(@Param("uuid") String uuid, @Param("approveStatus") String approveStatus, 
                           @Param("approverNum") String approverNum, @Param("approverName") String approverName,
                           @Param("approveTime") String approveTime, 
                           @Param("approveRemark") String approveRemark);

    /**
     * 更新执行状态
     */
    @Update("UPDATE biz_ddct_type_change SET " +
            "execute_status = #{executeStatus}, " +
            "execute_user_num = #{executeUserNum}, " +
            "execute_user_name = #{executeUserName}, " +
            "execute_time = #{executeTime}, " +
            "execute_result = #{executeResult}, " +
            "updated_time = NOW() " +
            "WHERE uuid = #{uuid}")
    int updateExecuteStatus(@Param("uuid") String uuid, @Param("executeStatus") String executeStatus, 
                           @Param("executeUserNum") String executeUserNum, @Param("executeUserName") String executeUserName,
                           @Param("executeTime") String executeTime, 
                           @Param("executeResult") String executeResult);

    /**
     * 更新更新时间（表结构无 item_add_count/item_mod_count/item_del_count）
     */
    @Update("UPDATE biz_ddct_type_change SET " +
            "updated_time = NOW() " +
            "WHERE uuid = #{uuid}")
    int touchUpdatedTime(@Param("uuid") String uuid);
}
