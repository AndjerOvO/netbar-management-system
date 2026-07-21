package com.ztw.netbar.netcafe.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ztw.netbar.common.annotation.Excel;
import com.ztw.netbar.common.annotation.Excel.ColumnType;
import com.ztw.netbar.common.core.domain.BaseEntity;

/**
 * 网咖会员对象 netcafe_member
 *
 * @author ruoyi
 */
public class NetcafeMember extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 会员ID */
    @Excel(name = "会员ID", cellType = ColumnType.NUMERIC)
    private Long id;

    /** 会员编号 */
    @Excel(name = "会员编号")
    private String memberNo;

    /** 身份证号 */
    @Excel(name = "身份证号")
    private String idCard;

    /** 会员姓名 */
    @Excel(name = "会员姓名")
    private String name;

    /** 手机号码 */
    @Excel(name = "手机号码")
    private String phone;

    /** 会员密码 */
    private String password;

    /** 账户余额 */
    @Excel(name = "账户余额", cellType = ColumnType.NUMERIC)
    private BigDecimal balance;

    /** 累计消费金额 */
    @Excel(name = "累计消费金额", cellType = ColumnType.NUMERIC)
    private BigDecimal totalSpent;

    /** 会员等级（0普通 1黄金 2钻石） */
    @Excel(name = "会员等级", readConverterExp = "0=普通,1=黄金,2=钻石")
    private String level;

    /** 会员类型（0正式会员 1临时卡） */
    @Excel(name = "会员类型", readConverterExp = "0=正式会员,1=临时卡")
    private String memberType;

    /** 会员状态（0正常 1冻结 2注销） */
    @Excel(name = "会员状态", readConverterExp = "0=正常,1=冻结,2=注销")
    private String status;

    /** 有效期（临时卡24小时过期） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    /** 删除标志（0代表存在 1代表删除） */
    private String delFlag;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getMemberNo()
    {
        return memberNo;
    }

    public void setMemberNo(String memberNo)
    {
        this.memberNo = memberNo;
    }

    public String getIdCard()
    {
        return idCard;
    }

    public void setIdCard(String idCard)
    {
        this.idCard = idCard;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public BigDecimal getBalance()
    {
        return balance;
    }

    public void setBalance(BigDecimal balance)
    {
        this.balance = balance;
    }

    public BigDecimal getTotalSpent()
    {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent)
    {
        this.totalSpent = totalSpent;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getMemberType() { return memberType; }
    public void setMemberType(String memberType) { this.memberType = memberType; }

    public Date getExpireTime() { return expireTime; }
    public void setExpireTime(Date expireTime) { this.expireTime = expireTime; }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("memberNo", getMemberNo())
            .append("idCard", getIdCard())
            .append("name", getName())
            .append("phone", getPhone())
            .append("balance", getBalance())
            .append("totalSpent", getTotalSpent())
            .append("level", getLevel())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
