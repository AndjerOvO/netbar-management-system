package com.ztw.netbar.netcafe.mapper;

import java.util.List;
import com.ztw.netbar.netcafe.domain.NetcafeSaleOrder;

/**
 * 销售订单 数据层
 *
 * @author ruoyi
 */
public interface NetcafeSaleOrderMapper
{
    /**
     * 根据订单ID查询
     */
    public NetcafeSaleOrder selectNetcafeSaleOrderById(Long id);

    /**
     * 查询销售订单列表（含关联查询）
     */
    public List<NetcafeSaleOrder> selectNetcafeSaleOrderList(NetcafeSaleOrder order);

    /**
     * 新增销售订单
     */
    public int insertNetcafeSaleOrder(NetcafeSaleOrder order);
}
