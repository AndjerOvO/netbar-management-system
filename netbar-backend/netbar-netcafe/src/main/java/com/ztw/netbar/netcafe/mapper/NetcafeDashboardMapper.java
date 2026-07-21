package com.ztw.netbar.netcafe.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 统计看板 数据层
 *
 * @author ruoyi
 */
public interface NetcafeDashboardMapper
{
    /**
     * 今日收入（销售订单 total_price 总和）
     */
    public BigDecimal selectTodayIncome();

    /**
     * 当前上机人数（usage_record status=0）
     */
    public int selectOnlineCount();

    /**
     * 机器总数（del_flag=0）
     */
    public int selectTotalMachines();

    /**
     * 近7天每日收入（DATE_FORMAT 分组），无数据的天返回 0
     */
    public List<Map<String, Object>> selectIncomeTrend();
}
