package com.ztw.netbar.netcafe.mapper;

import java.util.List;
import com.ztw.netbar.netcafe.domain.NetcafeUsageRecord;

/**
 * 上机记录 数据层
 *
 * @author ruoyi
 */
public interface NetcafeUsageRecordMapper
{
    /**
     * 根据记录ID查询
     */
    public NetcafeUsageRecord selectNetcafeUsageRecordById(Long id);

    /**
     * 查询上机记录列表（含关联查询）
     */
    public List<NetcafeUsageRecord> selectNetcafeUsageRecordList(NetcafeUsageRecord record);

    /**
     * 新增上机记录
     */
    public int insertNetcafeUsageRecord(NetcafeUsageRecord record);

    /**
     * 更新上机记录
     */
    public int updateNetcafeUsageRecord(NetcafeUsageRecord record);

    /**
     * 查询当前在用的上机记录（通过机器ID）
     */
    public NetcafeUsageRecord selectActiveRecordByMachineId(Long machineId);

    /**
     * 查询需要强制下线的记录（余额=0且force_offline_time超过5分钟）
     */
    public java.util.List<NetcafeUsageRecord> selectForceOfflineRecords();

    /**
     * 更新force_offline_time（余额归零时调用）
     */
    public int updateForceOfflineTime(@org.apache.ibatis.annotations.Param("id") Long id, @org.apache.ibatis.annotations.Param("forceOfflineTime") java.util.Date forceOfflineTime);

    /**
     * 清除force_offline_time（余额恢复时调用）
     */
    public int clearForceOfflineTime(@org.apache.ibatis.annotations.Param("memberId") Long memberId);
}
