package com.ztw.netbar.netcafe.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ztw.netbar.common.annotation.Excel;
import com.ztw.netbar.common.annotation.Excel.ColumnType;
import com.ztw.netbar.common.core.domain.BaseEntity;

/**
 * 充值记录对象 netcafe_recharge_record
 *
 * @author ruoyi
 */
public class NetcafeRechargeRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 充值记录ID */
    @Excel(name = "记录ID", cellType = ColumnType.NUMERIC)
    private Long id;

    /** 会员ID */
    @Excel(name = "会员ID")
    private Long memberId;

    /** 充值金额 */
    @Excel(name = "充值金额", cellType = ColumnType.NUMERIC)
    private BigDecimal amount;

    /** 支付方式（0现金 1微信） */
    @Excel(name = "支付方式", readConverterExp = "0=现金,1=微信")
    private String payType;

    /** 操作人 */
    @Excel(name = "操作人")
    private String operator;

    /** 删除标志 */
    private String delFlag;

    // ---- 关联字段 ----
    /** 会员姓名 */
    private String memberName;

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

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public String getPayType()
    {
        return payType;
    }

    public void setPayType(String payType)
    {
        this.payType = payType;
    }

    public String getOperator()
    {
        return operator;
    }

    public void setOperator(String operator)
    {
        this.operator = operator;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("memberId", getMemberId())
            .append("amount", getAmount())
            .append("payType", getPayType())
            .append("operator", getOperator())
            .toString();
    }
}
