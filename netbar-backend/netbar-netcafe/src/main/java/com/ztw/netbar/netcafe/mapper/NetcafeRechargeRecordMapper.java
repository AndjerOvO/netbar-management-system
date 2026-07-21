package com.ztw.netbar.netcafe.mapper;

import java.util.List;
import com.ztw.netbar.netcafe.domain.NetcafeRechargeRecord;

/**
 * 充值记录 数据层
 *
 * @author ruoyi
 */
public interface NetcafeRechargeRecordMapper
{
    /**
     * 根据记录ID查询
     */
    public NetcafeRechargeRecord selectNetcafeRechargeRecordById(Long id);

    /**
     * 查询充值记录列表（含关联查询）
     */
    public List<NetcafeRechargeRecord> selectNetcafeRechargeRecordList(NetcafeRechargeRecord record);

    /**
     * 新增充值记录
     */
    public int insertNetcafeRechargeRecord(NetcafeRechargeRecord record);
}
