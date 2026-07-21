package com.ztw.netbar.web.controller.netcafe;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ztw.netbar.common.core.controller.BaseController;
import com.ztw.netbar.common.core.domain.AjaxResult;
import com.ztw.netbar.netcafe.service.INetcafeDashboardService;

/**
 * 统计看板 信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/netcafe/dashboard")
public class NetcafeDashboardController extends BaseController
{
    @Autowired
    private INetcafeDashboardService dashboardService;

    /**
     * 获取首页概览数据
     */
    @PreAuthorize("@ss.hasPermi('netcafe:dashboard:overview')")
    @GetMapping("/overview")
    public AjaxResult overview()
    {
        Map<String, Object> data = dashboardService.getOverview();
        return success(data);
    }
}
