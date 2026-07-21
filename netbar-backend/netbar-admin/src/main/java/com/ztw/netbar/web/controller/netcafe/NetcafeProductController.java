package com.ztw.netbar.web.controller.netcafe;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ztw.netbar.common.annotation.Log;
import com.ztw.netbar.common.core.controller.BaseController;
import com.ztw.netbar.common.core.domain.AjaxResult;
import com.ztw.netbar.common.core.page.TableDataInfo;
import com.ztw.netbar.common.enums.BusinessType;
import com.ztw.netbar.netcafe.domain.NetcafeProduct;
import com.ztw.netbar.netcafe.service.INetcafeProductService;

/**
 * 商品管理 信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/netcafe/product")
public class NetcafeProductController extends BaseController
{
    @Autowired
    private INetcafeProductService productService;

    /**
     * 获取商品列表
     */
    @PreAuthorize("@ss.hasPermi('netcafe:product:list')")
    @GetMapping("/list")
    public TableDataInfo list(NetcafeProduct product)
    {
        startPage();
        List<NetcafeProduct> list = productService.selectNetcafeProductList(product);
        return getDataTable(list);
    }

    /**
     * 根据商品编号获取详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(productService.selectNetcafeProductById(id));
    }

    /**
     * 新增商品
     */
    @PreAuthorize("@ss.hasPermi('netcafe:product:add')")
    @Log(title = "商品管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody NetcafeProduct product)
    {
        product.setCreateBy(getUsername());
        return toAjax(productService.insertNetcafeProduct(product));
    }

    /**
     * 修改商品
     */
    @PreAuthorize("@ss.hasPermi('netcafe:product:edit')")
    @Log(title = "商品管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody NetcafeProduct product)
    {
        product.setUpdateBy(getUsername());
        return toAjax(productService.updateNetcafeProduct(product));
    }

    /**
     * 删除商品
     */
    @PreAuthorize("@ss.hasPermi('netcafe:product:remove')")
    @Log(title = "商品管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(productService.deleteNetcafeProductByIds(ids));
    }
}
