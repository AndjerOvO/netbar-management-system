package com.ztw.netbar.netcafe.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ztw.netbar.netcafe.domain.NetcafeProduct;
import com.ztw.netbar.netcafe.mapper.NetcafeProductMapper;
import com.ztw.netbar.netcafe.service.INetcafeProductService;

/**
 * 商品管理 服务层实现
 *
 * @author ruoyi
 */
@Service
public class NetcafeProductServiceImpl implements INetcafeProductService
{
    @Autowired
    private NetcafeProductMapper productMapper;

    /**
     * 根据商品ID查询商品信息
     *
     * @param id 商品ID
     * @return 商品信息
     */
    @Override
    public NetcafeProduct selectNetcafeProductById(Long id)
    {
        return productMapper.selectNetcafeProductById(id);
    }

    /**
     * 查询商品列表
     *
     * @param product 商品信息
     * @return 商品集合
     */
    @Override
    public List<NetcafeProduct> selectNetcafeProductList(NetcafeProduct product)
    {
        return productMapper.selectNetcafeProductList(product);
    }

    /**
     * 新增商品
     *
     * @param product 商品信息
     * @return 结果
     */
    @Override
    public int insertNetcafeProduct(NetcafeProduct product)
    {
        return productMapper.insertNetcafeProduct(product);
    }

    /**
     * 修改商品
     *
     * @param product 商品信息
     * @return 结果
     */
    @Override
    public int updateNetcafeProduct(NetcafeProduct product)
    {
        return productMapper.updateNetcafeProduct(product);
    }

    /**
     * 删除商品对象
     *
     * @param id 商品ID
     * @return 结果
     */
    @Override
    public int deleteNetcafeProductById(Long id)
    {
        return productMapper.deleteNetcafeProductById(id);
    }

    /**
     * 批量删除商品信息
     *
     * @param ids 需要删除的商品ID
     * @return 结果
     */
    @Override
    public int deleteNetcafeProductByIds(Long[] ids)
    {
        return productMapper.deleteNetcafeProductByIds(ids);
    }
}
