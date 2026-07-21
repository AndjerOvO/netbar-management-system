package com.ztw.netbar.netcafe.mapper;

import java.util.List;
import com.ztw.netbar.netcafe.domain.NetcafeMachine;

/**
 * 机器管理 数据层
 *
 * @author ruoyi
 */
public interface NetcafeMachineMapper
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
     * 逻辑删除机器
     *
     * @param id 机器ID
     * @return 结果
     */
    public int deleteNetcafeMachineById(Long id);

    /**
     * 批量逻辑删除机器
     *
     * @param ids 需要删除的机器ID
     * @return 结果
     */
    public int deleteNetcafeMachineByIds(Long[] ids);

    /**
     * 更新机器状态
     *
     * @param id 机器ID
     * @param status 新状态
     * @return 结果
     */
    public int updateNetcafeMachineStatus(@org.apache.ibatis.annotations.Param("id") Long id, @org.apache.ibatis.annotations.Param("status") String status);
}
