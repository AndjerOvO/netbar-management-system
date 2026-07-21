package com.ztw.netbar.web.controller.netcafe;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.ztw.netbar.netcafe.domain.NetcafeSaleOrder;
import com.ztw.netbar.netcafe.dto.SaleItemDTO;
import com.ztw.netbar.netcafe.service.INetcafeSaleOrderService;

import java.util.HashMap;
import java.util.Map;

/**
 * 销售订单 信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/netcafe/sale")
public class NetcafeSaleOrderController extends BaseController
{
    @Autowired
    private INetcafeSaleOrderService saleOrderService;

    /**
     * 获取销售订单列表（支持按日期筛选）
     */
    @PreAuthorize("@ss.hasPermi('netcafe:sale:list')")
    @GetMapping("/list")
    public TableDataInfo list(NetcafeSaleOrder order)
    {
        startPage();
        List<NetcafeSaleOrder> list = saleOrderService.selectNetcafeSaleOrderList(order);
        return getDataTable(list);
    }

    /**
     * 根据订单ID获取详细信息（含明细）
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(saleOrderService.selectNetcafeSaleOrderById(id));
    }

    /**
     * 创建销售订单（商品零售出库）
     */
    @PreAuthorize("@ss.hasPermi('netcafe:sale:create')")
    @Log(title = "商品销售", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public AjaxResult create(@RequestBody Map<String, Object> params)
    {
        Long memberId = params.get("memberId") != null ? Long.valueOf(params.get("memberId").toString()) : null;
        String payType = params.get("payType") != null ? params.get("payType").toString() : "0";

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itemsMap = (List<Map<String, Object>>) params.get("items");
        if (itemsMap == null || itemsMap.isEmpty())
        {
            return error("购买商品不能为空");
        }

        java.util.ArrayList<SaleItemDTO> items = new java.util.ArrayList<>();
        for (Map<String, Object> item : itemsMap)
        {
            SaleItemDTO dto = new SaleItemDTO();
            dto.setProductId(Long.valueOf(item.get("productId").toString()));
            dto.setQuantity(Integer.valueOf(item.get("quantity").toString()));
            items.add(dto);
        }

        HashMap<String, Object> result = saleOrderService.createSaleOrder(memberId, items, payType);
        return success(result);
    }
}
