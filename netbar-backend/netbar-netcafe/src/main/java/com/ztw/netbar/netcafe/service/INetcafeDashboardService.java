package com.ztw.netbar.netcafe.service;

import java.util.Map;

/**
 * 统计看板 服务层
 *
 * @author ruoyi
 */
public interface INetcafeDashboardService
{
    /**
     * 获取首页概览数据
     *
     * @return 看板数据
     */
    public Map<String, Object> getOverview();
}
