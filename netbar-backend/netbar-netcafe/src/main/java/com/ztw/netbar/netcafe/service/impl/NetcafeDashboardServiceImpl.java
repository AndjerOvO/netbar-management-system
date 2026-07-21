package com.ztw.netbar.netcafe.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ztw.netbar.netcafe.mapper.NetcafeDashboardMapper;
import com.ztw.netbar.netcafe.service.INetcafeDashboardService;

/**
 * 统计看板 服务层实现
 *
 * @author ruoyi
 */
@Service
public class NetcafeDashboardServiceImpl implements INetcafeDashboardService
{
    @Autowired
    private NetcafeDashboardMapper dashboardMapper;

    @Override
    public Map<String, Object> getOverview()
    {
        BigDecimal todayIncome = dashboardMapper.selectTodayIncome();
        int onlineCount = dashboardMapper.selectOnlineCount();
        int totalMachines = dashboardMapper.selectTotalMachines();

        double usageRate = 0.0;
        if (totalMachines > 0)
        {
            usageRate = new BigDecimal(onlineCount)
                .divide(new BigDecimal(totalMachines), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        }

        List<Map<String, Object>> trendRows = dashboardMapper.selectIncomeTrend();
        List<Double> trend = new ArrayList<>();
        for (Map<String, Object> row : trendRows)
        {
            Object amount = row.get("amount");
            if (amount != null)
            {
                trend.add(((Number) amount).doubleValue());
            }
            else
            {
                trend.add(0.0);
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("todayIncome", todayIncome.doubleValue());
        result.put("onlineCount", onlineCount);
        result.put("totalMachines", totalMachines);
        result.put("usageRate", usageRate);
        result.put("trend", trend);
        return result;
    }
}
