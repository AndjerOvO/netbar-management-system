package com.ztw.netbar.netcafe.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ztw.netbar.common.exception.ServiceException;
import com.ztw.netbar.netcafe.constant.NetcafeConstants;
import com.ztw.netbar.netcafe.domain.NetcafeMember;
import com.ztw.netbar.netcafe.domain.NetcafeProduct;
import com.ztw.netbar.netcafe.domain.NetcafeSaleItem;
import com.ztw.netbar.netcafe.domain.NetcafeSaleOrder;
import com.ztw.netbar.netcafe.dto.SaleItemDTO;
import com.ztw.netbar.netcafe.mapper.NetcafeMemberMapper;
import com.ztw.netbar.netcafe.mapper.NetcafeProductMapper;
import com.ztw.netbar.netcafe.mapper.NetcafeSaleItemMapper;
import com.ztw.netbar.netcafe.mapper.NetcafeSaleOrderMapper;
import com.ztw.netbar.netcafe.service.INetcafeSaleOrderService;

/**
 * 销售订单 服务层实现
 *
 * @author ruoyi
 */
@Service
public class NetcafeSaleOrderServiceImpl implements INetcafeSaleOrderService
{
    private static final Logger log = LoggerFactory.getLogger(NetcafeSaleOrderServiceImpl.class);

    @Autowired
    private NetcafeSaleOrderMapper saleOrderMapper;

    @Autowired
    private NetcafeSaleItemMapper saleItemMapper;

    @Autowired
    private NetcafeProductMapper productMapper;

    @Autowired
    private NetcafeMemberMapper memberMapper;

    @Override
    public NetcafeSaleOrder selectNetcafeSaleOrderById(Long id)
    {
        NetcafeSaleOrder order = saleOrderMapper.selectNetcafeSaleOrderById(id);
        if (order != null)
        {
            List<NetcafeSaleItem> items = saleItemMapper.selectNetcafeSaleItemByOrderId(id);
            order.setSaleItemList(items);
        }
        return order;
    }

    @Override
    public List<NetcafeSaleOrder> selectNetcafeSaleOrderList(NetcafeSaleOrder order)
    {
        return saleOrderMapper.selectNetcafeSaleOrderList(order);
    }

    /**
     * 创建销售订单（库存扣减 + 余额扣减）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public HashMap<String, Object> createSaleOrder(Long memberId, List<SaleItemDTO> items, String payType)
    {
        // 1. 余额支付时校验会员
        NetcafeMember member = null;
        if (NetcafeConstants.PAY_TYPE_BALANCE.equals(payType))
        {
            if (memberId == null)
            {
                throw new ServiceException("余额支付需要指定会员");
            }
            member = memberMapper.selectNetcafeMemberById(memberId);
            if (member == null)
            {
                throw new ServiceException("会员不存在");
            }
            if (!NetcafeConstants.MEMBER_STATUS_NORMAL.equals(member.getStatus()))
            {
                throw new ServiceException("会员状态异常");
            }
        }

        // 2. 计算总价 + 校验库存
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (SaleItemDTO item : items)
        {
            NetcafeProduct product = productMapper.selectNetcafeProductById(item.getProductId());
            if (product == null)
            {
                throw new ServiceException("商品ID " + item.getProductId() + " 不存在");
            }
            // 库存扣减（防超卖：where stock >= quantity）
            int rows = productMapper.deductStock(item.getProductId(), item.getQuantity());
            if (rows <= 0)
            {
                throw new ServiceException("商品 [" + product.getProductName() + "] 库存不足，当前库存：" + product.getStock() + "，购买数量：" + item.getQuantity());
            }
            totalPrice = totalPrice.add(product.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        totalPrice = totalPrice.setScale(2, java.math.RoundingMode.HALF_UP);

        // 3. 余额支付时扣减余额
        if (NetcafeConstants.PAY_TYPE_BALANCE.equals(payType) && member != null)
        {
            if (member.getBalance().compareTo(totalPrice) < 0)
            {
                throw new ServiceException("余额不足，当前余额：" + member.getBalance() + " 元，需支付：" + totalPrice + " 元");
            }
            int rows = memberMapper.deductMemberBalance(memberId, totalPrice);
            if (rows <= 0)
            {
                throw new ServiceException("余额扣减失败，请重试");
            }
        }

        // 4. 生成订单
        NetcafeSaleOrder order = new NetcafeSaleOrder();
        order.setMemberId(memberId);
        order.setTotalPrice(totalPrice);
        order.setPayType(payType);
        saleOrderMapper.insertNetcafeSaleOrder(order);

        // 5. 生成明细 + 异步库存预警
        for (SaleItemDTO item : items)
        {
            NetcafeProduct product = productMapper.selectNetcafeProductById(item.getProductId());

            NetcafeSaleItem saleItem = new NetcafeSaleItem();
            saleItem.setOrderId(order.getId());
            saleItem.setProductId(item.getProductId());
            saleItem.setQuantity(item.getQuantity());
            saleItem.setPriceAtTime(product.getPrice());
            saleItemMapper.insertNetcafeSaleItem(saleItem);

            // 重新查询库存，判断是否需要预警
            NetcafeProduct refreshed = productMapper.selectNetcafeProductById(item.getProductId());
            if (refreshed.getStock() < refreshed.getWarningStock())
            {
                checkStockWarning(refreshed);
            }
        }

        HashMap<String, Object> result = new HashMap<>();
        result.put("orderId", order.getId());
        result.put("totalPrice", totalPrice);
        return result;
    }

    /**
     * 异步库存预警
     */
    @Async("threadPoolTaskExecutor")
    public void checkStockWarning(NetcafeProduct product)
    {
        log.warn("=== 库存预警 ===");
        log.warn("商品ID: {}, 商品名称: {}", product.getId(), product.getProductName());
        log.warn("当前库存: {}, 预警阈值: {}", product.getStock(), product.getWarningStock());
        log.warn("请及时补货！");
        log.warn("================");
    }
}
