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
 * 上机记录对象 netcafe_usage_record
 *
 * @author ruoyi
 */
public class NetcafeUsageRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    @Excel(name = "记录ID", cellType = ColumnType.NUMERIC)
    private Long id;

    /** 会员ID（临时卡为NULL） */
    @Excel(name = "会员ID")
    private Long memberId;

    /** 机器ID */
    @Excel(name = "机器ID")
    private Long machineId;

    /** 上机时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "上机时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 下机时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "下机时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 上机总时长（分钟） */
    @Excel(name = "上机时长(分钟)")
    private Integer totalMinutes;

    /** 上机总费用 */
    @Excel(name = "上机总费用", cellType = ColumnType.NUMERIC)
    private BigDecimal totalAmount;

    /** 上机状态（0上机中 1已下机） */
    @Excel(name = "上机状态", readConverterExp = "0=上机中,1=已下机")
    private String status;

    /** 余额归零时间（用于强制下线计时） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date forceOfflineTime;

    /** 删除标志 */
    private String delFlag;

    // ---- 关联字段（非数据库列） ----
    /** 会员姓名 */
    private String memberName;

    /** 机器编号 */
    private String machineNo;

    /** 机器类型 */
    private String machineType;

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

    public Long getMachineId()
    {
        return machineId;
    }

    public void setMachineId(Long machineId)
    {
        this.machineId = machineId;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Integer getTotalMinutes()
    {
        return totalMinutes;
    }

    public void setTotalMinutes(Integer totalMinutes)
    {
        this.totalMinutes = totalMinutes;
    }

    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getForceOfflineTime()
    {
        return forceOfflineTime;
    }

    public void setForceOfflineTime(Date forceOfflineTime)
    {
        this.forceOfflineTime = forceOfflineTime;
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

    public String getMachineNo()
    {
        return machineNo;
    }

    public void setMachineNo(String machineNo)
    {
        this.machineNo = machineNo;
    }

    public String getMachineType()
    {
        return machineType;
    }

    public void setMachineType(String machineType)
    {
        this.machineType = machineType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("memberId", getMemberId())
            .append("machineId", getMachineId())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("totalMinutes", getTotalMinutes())
            .append("totalAmount", getTotalAmount())
            .append("status", getStatus())
            .toString();
    }
}
