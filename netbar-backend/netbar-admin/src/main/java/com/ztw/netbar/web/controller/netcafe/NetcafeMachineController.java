package com.ztw.netbar.web.controller.netcafe;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.ztw.netbar.netcafe.domain.NetcafeMachine;
import com.ztw.netbar.netcafe.service.INetcafeMachineService;

/**
 * 机器管理 信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/netcafe/machine")
public class NetcafeMachineController extends BaseController
{
    @Autowired
    private INetcafeMachineService machineService;

    /**
     * 获取机器列表
     */
    @PreAuthorize("@ss.hasPermi('netcafe:machine:list')")
    @GetMapping("/list")
    public TableDataInfo list(NetcafeMachine machine)
    {
        startPage();
        List<NetcafeMachine> list = machineService.selectNetcafeMachineList(machine);
        return getDataTable(list);
    }

    /**
     * 根据机器编号获取详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(machineService.selectNetcafeMachineById(id));
    }

    /**
     * 新增机器
     */
    @PreAuthorize("@ss.hasPermi('netcafe:machine:add')")
    @Log(title = "机器管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody NetcafeMachine machine)
    {
        machine.setCreateBy(getUsername());
        return toAjax(machineService.insertNetcafeMachine(machine));
    }

    /**
     * 修改机器
     */
    @PreAuthorize("@ss.hasPermi('netcafe:machine:edit')")
    @Log(title = "机器管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody NetcafeMachine machine)
    {
        machine.setUpdateBy(getUsername());
        return toAjax(machineService.updateNetcafeMachine(machine));
    }

    /**
     * 删除机器
     */
    @PreAuthorize("@ss.hasPermi('netcafe:machine:remove')")
    @Log(title = "机器管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(machineService.deleteNetcafeMachineByIds(ids));
    }
}
