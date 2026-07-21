package com.ztw.netbar.web.controller.netcafe;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ztw.netbar.common.annotation.Log;
import com.ztw.netbar.common.core.controller.BaseController;
import com.ztw.netbar.common.core.domain.AjaxResult;
import com.ztw.netbar.common.core.page.TableDataInfo;
import com.ztw.netbar.common.enums.BusinessType;
import com.ztw.netbar.netcafe.domain.NetcafeRechargeRecord;
import com.ztw.netbar.netcafe.service.INetcafeRechargeRecordService;

/**
 * 充值记录 信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/netcafe/recharge")
public class NetcafeRechargeRecordController extends BaseController
{
    @Autowired
    private INetcafeRechargeRecordService rechargeRecordService;

    /**
     * 获取充值记录列表（支持按日期筛选）
     */
    @PreAuthorize("@ss.hasPermi('netcafe:recharge:list')")
    @GetMapping("/list")
    public TableDataInfo list(NetcafeRechargeRecord record)
    {
        startPage();
        List<NetcafeRechargeRecord> list = rechargeRecordService.selectNetcafeRechargeRecordList(record);
        return getDataTable(list);
    }

    /**
     * 根据记录ID获取详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(rechargeRecordService.selectNetcafeRechargeRecordById(id));
    }

    /**
     * 会员充值
     */
    @PreAuthorize("@ss.hasPermi('netcafe:recharge:add')")
    @Log(title = "会员充值", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody NetcafeRechargeRecord record)
    {
        record.setCreateBy(getUsername());
        return toAjax(rechargeRecordService.insertNetcafeRechargeRecord(record));
    }
}
