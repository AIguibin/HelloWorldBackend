package com.aiguibin.platform.arch.mapper;

import com.aiguibin.platform.arch.entity.DictItemChange;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 字典项变更Mapper
 */
@Mapper
public interface DictItemChangeMapper extends BaseMapper<DictItemChange> {

    /**
     * 批量插入字典项变更
     */
    @Insert("<script>" +
            "INSERT INTO biz_ddct_item_change " +
            "(id, change_id, change_operation, old_dct_seq, new_dct_seq, " +
            "old_dct_grp, new_dct_grp, old_dct_key, new_dct_key, " +
            "old_dct_val_nm, new_dct_val_nm, old_dct_val, new_dct_val, " +
            "old_dct_dsc, new_dct_dsc, old_stcd, new_stcd, " +
            "execute_status, item_order, create_time, update_time, is_deleted) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.id}, #{item.changeId}, #{item.changeOperation}, " +
            "#{item.oldDctSeq}, #{item.newDctSeq}, #{item.oldDctGrp}, #{item.newDctGrp}, " +
            "#{item.oldDctKey}, #{item.newDctKey}, #{item.oldDctValNm}, #{item.newDctValNm}, " +
            "#{item.oldDctVal}, #{item.newDctVal}, #{item.oldDctDsc}, #{item.newDctDsc}, " +
            "#{item.oldStcd}, #{item.newStcd}, #{item.executeStatus}, #{item.itemOrder}, " +
            "NOW(), NOW(), 0)" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<DictItemChange> items);

    /**
     * 根据变更ID查询字典项变更
     */
    @Select("SELECT * FROM biz_ddct_item_change " +
            "WHERE change_id = #{changeId} " +
            "AND is_deleted = 0 " +
            "ORDER BY item_order ASC")
    List<DictItemChange> selectByChangeId(@Param("changeId") String changeId);

    /**
     * 批量更新执行状态
     */
    @Update("<script>" +
            "<foreach collection='list' item='item' separator=';'>" +
            "UPDATE biz_ddct_item_change SET " +
            "execute_status = #{item.executeStatus}, " +
            "execute_result = #{item.executeResult}, " +
            "update_time = NOW() " +
            "WHERE id = #{item.id}" +
            "</foreach>" +
            "</script>")
    int batchUpdateExecuteStatus(@Param("list") List<DictItemChange> items);

    /**
     * 根据变更ID统计不同操作类型的数量
     */
    @Select("SELECT change_operation, COUNT(*) as count " +
            "FROM biz_ddct_item_change " +
            "WHERE change_id = #{changeId} " +
            "AND is_deleted = 0 " +
            "GROUP BY change_operation")
    List<OperationCount> countByOperation(@Param("changeId") String changeId);

    /**
     * 操作数量统计
     */
    class OperationCount {
        private String changeOperation;
        private Integer count;

        public String getChangeOperation() {
            return changeOperation;
        }

        public void setChangeOperation(String changeOperation) {
            this.changeOperation = changeOperation;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }
}
