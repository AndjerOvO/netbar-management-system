package com.ztw.netbar.netcafe.service;

import java.util.List;
import com.ztw.netbar.netcafe.domain.NetcafeMachine;

/**
 * 机器管理 服务层
 *
 * @author ruoyi
 */
public interface INetcafeMachineService
{
    /**
     * 根据机器ID查询机器信息
     *
     * @param id 机器ID
     * @return 机器信息
     */
    public NetcafeMachine selectNetcafeMachineById(Long id);

    /**
     * 根据机器编号查询机器信息
     *
     * @param machineNo 机器编号
     * @return 机器信息
     */
    public NetcafeMachine selectNetcafeMachineByMachineNo(String machineNo);

    /**
     * 查询机器列表
     *
     * @param machine 机器信息
     * @return 机器集合
     */
    public List<NetcafeMachine> selectNetcafeMachineList(NetcafeMachine machine);

    /**
     * 新增机器
     *
     * @param machine 机器信息
     * @return 结果
     */
    public int insertNetcafeMachine(NetcafeMachine machine);

    /**
     * 修改机器
     *
     * @param machine 机器信息
     * @return 结果
     */
    public int updateNetcafeMachine(NetcafeMachine machine);

    /**
     * 删除机器信息
     *
     * @param id 机器ID
     * @return 结果
     */
    public int deleteNetcafeMachineById(Long id);

    /**
     * 批量删除机器信息
     *
     * @param ids 需要删除的机器ID
     * @return 结果
     */
    public int deleteNetcafeMachineByIds(Long[] ids);
}
