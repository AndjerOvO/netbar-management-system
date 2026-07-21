package com.ztw.netbar.netcafe.service;

import java.util.List;
import com.ztw.netbar.netcafe.domain.NetcafeRechargeRecord;

/**
 * 充值记录 服务层
 *
 * @author ruoyi
 */
public interface INetcafeRechargeRecordService
{
    /**
     * 查询充值记录详情
     */
    public NetcafeRechargeRecord selectNetcafeRechargeRecordById(Long id);

    /**
     * 查询充值记录列表
     */
    public List<NetcafeRechargeRecord> selectNetcafeRechargeRecordList(NetcafeRechargeRecord record);

    /**
     * 充值（增加余额 + 写记录）
     *
     * @param record 充值记录（含 memberId, amount, payType）
     * @return 结果
     */
    public int insertNetcafeRechargeRecord(NetcafeRechargeRecord record);
}
