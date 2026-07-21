package com.ztw.netbar.netcafe.service;

import java.util.HashMap;
import java.util.List;
import com.ztw.netbar.netcafe.domain.NetcafeUsageRecord;

/**
 * 上机记录 服务层
 *
 * @author ruoyi
 */
public interface INetcafeUsageRecordService
{
    /**
     * 查询上机记录详情
     */
    public NetcafeUsageRecord selectNetcafeUsageRecordById(Long id);

    /**
     * 查询上机记录列表
     */
    public List<NetcafeUsageRecord> selectNetcafeUsageRecordList(NetcafeUsageRecord record);

    /**
     * 上机开台
     *
     * @param machineId 机器ID
     * @param memberId  会员ID（可为空）
     * @return 包含 recordId 和 startTime 的 map
     */
    public HashMap<String, Object> startUsage(Long machineId, Long memberId);

    /**
     * 下机结算
     *
     * @param recordId 记录ID
     * @return 结算结果
     */
    public HashMap<String, Object> endUsage(Long recordId);
}
