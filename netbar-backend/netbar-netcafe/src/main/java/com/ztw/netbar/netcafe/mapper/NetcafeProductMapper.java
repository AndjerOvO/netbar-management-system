package com.ztw.netbar.netcafe.mapper;

import java.util.List;
import com.ztw.netbar.netcafe.domain.NetcafeProduct;

/**
 * 商品管理 数据层
 *
 * @author ruoyi
 */
public interface NetcafeProductMapper
{
    /**
     * 根据商品ID查询商品信息
     *
     * @param id 商品ID
     * @return 商品信息
     */
    public NetcafeProduct selectNetcafeProductById(Long id);

    /**
     * 查询商品列表
     *
     * @param product 商品信息
     * @return 商品集合
     */
    public List<NetcafeProduct> selectNetcafeProductList(NetcafeProduct product);

    /**
     * 新增商品
     *
     * @param product 商品信息
     * @return 结果
     */
    public int insertNetcafeProduct(NetcafeProduct product);

    /**
     * 修改商品
     *
     * @param product 商品信息
     * @return 结果
     */
    public int updateNetcafeProduct(NetcafeProduct product);

    /**
     * 逻辑删除商品
     *
     * @param id 商品ID
     * @return 结果
     */
    public int deleteNetcafeProductById(Long id);

    /**
     * 批量逻辑删除商品
     *
     * @param ids 需要删除的商品ID
     * @return 结果
     */
    public int deleteNetcafeProductByIds(Long[] ids);

    /**
     * 库存扣减（防超卖：where stock >= #{quantity}）
     *
     * @param productId 商品ID
     * @param quantity 扣减数量
     * @return 受影响行数（0表示库存不足）
     */
    public int deductStock(@org.apache.ibatis.annotations.Param("productId") Long productId, @org.apache.ibatis.annotations.Param("quantity") Integer quantity);
}
