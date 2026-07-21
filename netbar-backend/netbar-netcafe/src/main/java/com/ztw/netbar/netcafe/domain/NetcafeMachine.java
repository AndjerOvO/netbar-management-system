package com.ztw.netbar.netcafe.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ztw.netbar.common.annotation.Excel;
import com.ztw.netbar.common.annotation.Excel.ColumnType;
import com.ztw.netbar.common.core.domain.BaseEntity;

/**
 * 网咖机器对象 netcafe_machine
 *
 * @author ruoyi
 */
public class NetcafeMachine extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 机器ID */
    @Excel(name = "机器ID", cellType = ColumnType.NUMERIC)
    private Long id;

    /** 机器编号 */
    @Excel(name = "机器编号")
    private String machineNo;

    /** 机器类型（普通区 竞技区 包厢） */
    @Excel(name = "机器类型", readConverterExp = "普通区=普通区,竞技区=竞技区,包厢=包厢")
    private String type;

    /** 每小时单价 */
    @Excel(name = "每小时单价", cellType = ColumnType.NUMERIC)
    private BigDecimal pricePerHour;

    /** 机器状态（0空闲 1使用中 2故障） */
    @Excel(name = "机器状态", readConverterExp = "0=空闲,1=使用中,2=故障")
    private String status;

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

    public String getMachineNo()
    {
        return machineNo;
    }

    public void setMachineNo(String machineNo)
    {
        this.machineNo = machineNo;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public BigDecimal getPricePerHour()
    {
        return pricePerHour;
    }

    public void setPricePerHour(BigDecimal pricePerHour)
    {
        this.pricePerHour = pricePerHour;
    }

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
            .append("machineNo", getMachineNo())
            .append("type", getType())
            .append("pricePerHour", getPricePerHour())
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
