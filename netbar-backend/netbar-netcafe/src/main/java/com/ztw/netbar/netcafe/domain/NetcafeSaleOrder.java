package com.ztw.netbar.netcafe.domain;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ztw.netbar.common.annotation.Excel;
import com.ztw.netbar.common.annotation.Excel.ColumnType;
import com.ztw.netbar.common.core.domain.BaseEntity;

/**
 * 销售订单对象 netcafe_sale_order
 *
 * @author ruoyi
 */
public class NetcafeSaleOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    @Excel(name = "订单ID", cellType = ColumnType.NUMERIC)
    private Long id;

    /** 会员ID（散客为NULL） */
    @Excel(name = "会员ID")
    private Long memberId;

    /** 订单总价 */
    @Excel(name = "订单总价", cellType = ColumnType.NUMERIC)
    private BigDecimal totalPrice;

    /** 支付方式（0现金 1余额 2微信） */
    @Excel(name = "支付方式", readConverterExp = "0=现金,1=余额,2=微信")
    private String payType;

    /** 删除标志 */
    private String delFlag;

    // ---- 关联字段 ----
    /** 会员姓名 */
    private String memberName;

    /** 订单明细 */
    private List<NetcafeSaleItem> saleItemList;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getMemberId()
    {
        return memberId;
    }

    public void setMemberId(Long memberId)
    {
        this.memberId = memberId;
    }

    public BigDecimal getTotalPrice()
    {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice)
    {
        this.totalPrice = totalPrice;
    }

    public String getPayType()
    {
        return payType;
    }

    public void setPayType(String payType)
    {
        this.payType = payType;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getMemberName()
    {
        return memberName;
    }

    public void setMemberName(String memberName)
    {
        this.memberName = memberName;
    }

    public List<NetcafeSaleItem> getSaleItemList()
    {
        return saleItemList;
    }

    public void setSaleItemList(List<NetcafeSaleItem> saleItemList)
    {
        this.saleItemList = saleItemList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("memberId", getMemberId())
            .append("totalPrice", getTotalPrice())
            .append("payType", getPayType())
            .toString();
    }
}
