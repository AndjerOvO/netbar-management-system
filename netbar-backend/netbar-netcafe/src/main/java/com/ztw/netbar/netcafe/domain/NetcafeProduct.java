package com.ztw.netbar.netcafe.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ztw.netbar.common.annotation.Excel;
import com.ztw.netbar.common.annotation.Excel.ColumnType;
import com.ztw.netbar.common.core.domain.BaseEntity;

/**
 * 网咖商品对象 netcafe_product
 *
 * @author ruoyi
 */
public class NetcafeProduct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 商品ID */
    @Excel(name = "商品ID", cellType = ColumnType.NUMERIC)
    private Long id;

    /** 商品名称 */
    @Excel(name = "商品名称")
    private String productName;

    /** 商品分类（饮料 零食 外设） */
    @Excel(name = "商品分类", readConverterExp = "饮料=饮料,零食=零食,外设=外设")
    private String category;

    /** 商品售价 */
    @Excel(name = "商品售价", cellType = ColumnType.NUMERIC)
    private BigDecimal price;

    /** 库存数量 */
    @Excel(name = "库存数量")
    private Integer stock;

    /** 库存预警阈值 */
    @Excel(name = "预警阈值")
    private Integer warningStock;

    /** 上架状态（0上架 1下架） */
    @Excel(name = "上架状态", readConverterExp = "0=上架,1=下架")
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

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public Integer getStock()
    {
        return stock;
    }

    public void setStock(Integer stock)
    {
        this.stock = stock;
    }

    public Integer getWarningStock()
    {
        return warningStock;
    }

    public void setWarningStock(Integer warningStock)
    {
        this.warningStock = warningStock;
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
            .append("productName", getProductName())
            .append("category", getCategory())
            .append("price", getPrice())
            .append("stock", getStock())
            .append("warningStock", getWarningStock())
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
