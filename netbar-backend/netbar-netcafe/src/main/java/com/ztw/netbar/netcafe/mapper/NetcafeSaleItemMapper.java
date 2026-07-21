package com.ztw.netbar.netcafe.mapper;

import java.util.List;
import com.ztw.netbar.netcafe.domain.NetcafeSaleItem;

/**
 * 销售订单明细 数据层
 *
 * @author ruoyi
 */
public interface NetcafeSaleItemMapper
{
    /**
     * 根据订单ID查询明细列表
     */
    public List<NetcafeSaleItem> selectNetcafeSaleItemByOrderId(Long orderId);

    /**
     * 批量新增销售明细
     */
    public int insertNetcafeSaleItem(NetcafeSaleItem item);
}
