package com.ztw.netbar.web.controller.netcafe;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ztw.netbar.common.annotation.Log;
import com.ztw.netbar.common.core.controller.BaseController;
import com.ztw.netbar.common.core.domain.AjaxResult;
import com.ztw.netbar.common.core.page.TableDataInfo;
import com.ztw.netbar.common.enums.BusinessType;
import com.ztw.netbar.netcafe.domain.NetcafeUsageRecord;
import com.ztw.netbar.netcafe.service.INetcafeUsageRecordService;

import java.util.HashMap;
import java.util.Map;

/**
 * 上机记录 信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/netcafe/usage")
public class NetcafeUsageRecordController extends BaseController
{
    @Autowired
    private INetcafeUsageRecordService usageRecordService;

    /**
     * 获取上机记录列表（支持按日期筛选）
     */
    @PreAuthorize("@ss.hasPermi('netcafe:usage:list')")
    @GetMapping("/list")
    public TableDataInfo list(NetcafeUsageRecord record)
    {
        startPage();
        List<NetcafeUsageRecord> list = usageRecordService.selectNetcafeUsageRecordList(record);
        return getDataTable(list);
    }

    /**
     * 根据记录ID获取详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(usageRecordService.selectNetcafeUsageRecordById(id));
    }

    /**
     * 上机开台
     */
    @PreAuthorize("@ss.hasPermi('netcafe:usage:start')")
    @Log(title = "上机开台", businessType = BusinessType.INSERT)
    @PostMapping("/start")
    public AjaxResult start(@RequestBody Map<String, Object> params)
    {
        Long machineId = params.get("machineId") != null ? Long.valueOf(params.get("machineId").toString()) : null;
        Long memberId = params.get("memberId") != null ? Long.valueOf(params.get("memberId").toString()) : null;
        if (machineId == null)
        {
            return error("机器ID不能为空");
        }
        HashMap<String, Object> result = usageRecordService.startUsage(machineId, memberId);
        return success(result);
    }

    /**
     * 下机结算
     */
    @PreAuthorize("@ss.hasPermi('netcafe:usage:end')")
    @Log(title = "下机结算", businessType = BusinessType.UPDATE)
    @PutMapping("/end/{recordId}")
    public AjaxResult end(@PathVariable Long recordId)
    {
        HashMap<String, Object> result = usageRecordService.endUsage(recordId);
        return success(result);
    }
}
