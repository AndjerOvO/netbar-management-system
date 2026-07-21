package com.ztw.netbar.netcafe.service;

import java.util.HashMap;
import java.util.List;
import com.ztw.netbar.netcafe.domain.NetcafeSaleOrder;
import com.ztw.netbar.netcafe.dto.SaleItemDTO;

/**
 * 销售订单 服务层
 *
 * @author ruoyi
 */
public interface INetcafeSaleOrderService
{
    /**
     * 查询订单详情（含明细）
     */
    public NetcafeSaleOrder selectNetcafeSaleOrderById(Long id);

    /**
     * 查询订单列表
     */
    public List<NetcafeSaleOrder> selectNetcafeSaleOrderList(NetcafeSaleOrder order);

    /**
     * 创建销售订单（库存扣减 + 余额扣减）
     *
     * @param memberId 会员ID（可为空）
     * @param items    商品明细
     * @param payType  支付方式
     * @return 订单信息
     */
    public HashMap<String, Object> createSaleOrder(Long memberId, List<SaleItemDTO> items, String payType);
}
