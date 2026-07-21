package com.ztw.netbar.web.controller.netcafe;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import com.ztw.netbar.common.utils.StringUtils;
import com.ztw.netbar.netcafe.domain.NetcafeMember;
import com.ztw.netbar.netcafe.service.INetcafeMemberService;

/**
 * 会员管理 信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/netcafe/member")
public class NetcafeMemberController extends BaseController
{
    @Autowired
    private INetcafeMemberService memberService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 获取会员列表
     */
    @PreAuthorize("@ss.hasPermi('netcafe:member:list')")
    @GetMapping("/list")
    public TableDataInfo list(NetcafeMember member)
    {
        startPage();
        List<NetcafeMember> list = memberService.selectNetcafeMemberList(member);
        return getDataTable(list);
    }

    /**
     * 根据会员编号获取详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(memberService.selectNetcafeMemberById(id));
    }

    /**
     * 新增会员
     */
    /**
     * 开临时卡
     */
    @PreAuthorize("@ss.hasPermi('netcafe:member:add')")
    @Log(title = "开临时卡", businessType = BusinessType.INSERT)
    @PostMapping("/tempCard")
    public AjaxResult tempCard(@RequestBody Map<String, Object> params)
    {
        String name = (String) params.get("name");
        String idCard = (String) params.get("idCard");
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(idCard))
        {
            return error("姓名和身份证号不能为空");
        }
        // 身份证号唯一性校验
        NetcafeMember exist = memberService.selectNetcafeMemberByIdCard(idCard);
        if (exist != null) { return error("该身份证号已登记"); }

        NetcafeMember member = new NetcafeMember();
        member.setName(name);
        member.setIdCard(idCard);
        member.setPhone((String) params.get("phone"));
        member.setMemberType("1"); // 临时卡
        member.setLevel("0");
        member.setStatus("0");

        // 自动生成卡号 TEMP + 日期 + 序号
        String cardNo = "TEMP" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        member.setMemberNo(cardNo);

        // 密码=身份证后6位 BCrypt加密
        if (idCard.length() >= 6) {
            String raw = idCard.substring(idCard.length() - 6);
            member.setPassword(passwordEncoder.encode(raw));
        }

        // 余额
        Object bal = params.get("balance");
        member.setBalance(bal != null ? new java.math.BigDecimal(bal.toString()) : java.math.BigDecimal.ZERO);
        member.setTotalSpent(java.math.BigDecimal.ZERO);

        // 24小时过期
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.HOUR_OF_DAY, 24);
        member.setExpireTime(cal.getTime());

        member.setCreateBy(getUsername());
        memberService.insertNetcafeMember(member);

        HashMap<String, Object> result = new HashMap<>();
        result.put("cardNo", cardNo);
        result.put("password", idCard.substring(idCard.length() - 6));
        result.put("expireTime", cal.getTime());
        return success(result);
    }

    @PreAuthorize("@ss.hasPermi('netcafe:member:add')")
    @Log(title = "会员管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody NetcafeMember member)
    {
        member.setCreateBy(getUsername());
        return toAjax(memberService.insertNetcafeMember(member));
    }

    /**
     * 修改会员
     */
    @PreAuthorize("@ss.hasPermi('netcafe:member:edit')")
    @Log(title = "会员管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody NetcafeMember member)
    {
        member.setUpdateBy(getUsername());
        return toAjax(memberService.updateNetcafeMember(member));
    }

    /**
     * 删除会员
     */
    @PreAuthorize("@ss.hasPermi('netcafe:member:remove')")
    @Log(title = "会员管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(memberService.deleteNetcafeMemberByIds(ids));
    }

    /**
     * 重置会员密码（恢复为身份证后六位）
     */
    @PreAuthorize("@ss.hasPermi('netcafe:member:edit')")
    @Log(title = "重置密码", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd/{memberId}")
    public AjaxResult resetPwd(@PathVariable Long memberId)
    {
        return toAjax(memberService.resetPassword(memberId));
    }
}
