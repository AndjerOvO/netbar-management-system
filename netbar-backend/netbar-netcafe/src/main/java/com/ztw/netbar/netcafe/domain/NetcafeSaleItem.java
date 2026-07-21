package com.ztw.netbar.netcafe.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ztw.netbar.common.core.domain.BaseEntity;

/**
 * 销售订单明细对象 netcafe_sale_item
 *
 * @author ruoyi
 */
public class NetcafeSaleItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 明细ID */
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 商品ID */
    private Long productId;

    /** 购买数量 */
    private Integer quantity;

    /** 下单时商品单价 */
    private BigDecimal priceAtTime;

    /** 删除标志 */
    private String delFlag;

    // ---- 关联字段 ----
    /** 商品名称 */
    private String productName;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public Long getProductId()
    {
        return productId;
    }

    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public Integer getQuantity()
    {
        return quantity;
    }

    public void setQuantity(Integer quantity)
    {
        this.quantity = quantity;
    }

    public BigDecimal getPriceAtTime()
    {
        return priceAtTime;
    }

    public void setPriceAtTime(BigDecimal priceAtTime)
    {
        this.priceAtTime = priceAtTime;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("orderId", getOrderId())
            .append("productId", getProductId())
            .append("quantity", getQuantity())
            .append("priceAtTime", getPriceAtTime())
            .toString();
    }
}
