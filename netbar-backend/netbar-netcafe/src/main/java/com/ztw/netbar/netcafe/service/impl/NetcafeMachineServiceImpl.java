package com.ztw.netbar.netcafe.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ztw.netbar.netcafe.domain.NetcafeMachine;
import com.ztw.netbar.netcafe.mapper.NetcafeMachineMapper;
import com.ztw.netbar.netcafe.service.INetcafeMachineService;

/**
 * 机器管理 服务层实现
 *
 * @author ruoyi
 */
@Service
public class NetcafeMachineServiceImpl implements INetcafeMachineService
{
    @Autowired
    private NetcafeMachineMapper machineMapper;

    /**
     * 根据机器ID查询机器信息
     *
     * @param id 机器ID
     * @return 机器信息
     */
    @Override
    public NetcafeMachine selectNetcafeMachineById(Long id)
    {
        return machineMapper.selectNetcafeMachineById(id);
    }

    /**
     * 根据机器编号查询机器信息
     *
     * @param machineNo 机器编号
     * @return 机器信息
     */
    @Override
    public NetcafeMachine selectNetcafeMachineByMachineNo(String machineNo)
    {
        return machineMapper.selectNetcafeMachineByMachineNo(machineNo);
    }

    /**
     * 查询机器列表
     *
     * @param machine 机器信息
     * @return 机器集合
     */
    @Override
    public List<NetcafeMachine> selectNetcafeMachineList(NetcafeMachine machine)
    {
        return machineMapper.selectNetcafeMachineList(machine);
    }

    /**
     * 新增机器
     *
     * @param machine 机器信息
     * @return 结果
     */
    @Override
    public int insertNetcafeMachine(NetcafeMachine machine)
    {
        return machineMapper.insertNetcafeMachine(machine);
    }

    /**
     * 修改机器
     *
     * @param machine 机器信息
     * @return 结果
     */
    @Override
    public int updateNetcafeMachine(NetcafeMachine machine)
    {
        return machineMapper.updateNetcafeMachine(machine);
    }

    /**
     * 删除机器对象
     *
     * @param id 机器ID
     * @return 结果
     */
    @Override
    public int deleteNetcafeMachineById(Long id)
    {
        return machineMapper.deleteNetcafeMachineById(id);
    }

    /**
     * 批量删除机器信息
     *
     * @param ids 需要删除的机器ID
     * @return 结果
     */
    @Override
    public int deleteNetcafeMachineByIds(Long[] ids)
    {
        return machineMapper.deleteNetcafeMachineByIds(ids);
    }
}
