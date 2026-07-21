package com.ztw.netbar.netcafe.service;

import java.util.List;
import com.ztw.netbar.netcafe.domain.NetcafeProduct;

/**
 * 商品管理 服务层
 *
 * @author ruoyi
 */
public interface INetcafeProductService
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
     * 删除商品信息
     *
     * @param id 商品ID
     * @return 结果
     */
    public int deleteNetcafeProductById(Long id);

    /**
     * 批量删除商品信息
     *
     * @param ids 需要删除的商品ID
     * @return 结果
     */
    public int deleteNetcafeProductByIds(Long[] ids);
}
