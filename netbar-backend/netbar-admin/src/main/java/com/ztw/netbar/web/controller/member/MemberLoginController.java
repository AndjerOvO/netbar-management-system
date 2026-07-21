package com.ztw.netbar.web.controller.member;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.TimeUnit;
import com.ztw.netbar.common.core.domain.AjaxResult;
import com.ztw.netbar.common.core.redis.RedisCache;
import com.ztw.netbar.netcafe.constant.NetcafeConstants;
import com.ztw.netbar.netcafe.domain.NetcafeProduct;
import com.ztw.netbar.netcafe.dto.SaleItemDTO;
import com.ztw.netbar.netcafe.service.INetcafeProductService;
import com.ztw.netbar.netcafe.service.INetcafeSaleOrderService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.ztw.netbar.netcafe.domain.NetcafeMachine;
import com.ztw.netbar.netcafe.domain.NetcafeMember;
import com.ztw.netbar.netcafe.domain.NetcafeUsageRecord;
import com.ztw.netbar.netcafe.domain.NetcafeRechargeRecord;
import com.ztw.netbar.netcafe.service.INetcafeMachineService;
import com.ztw.netbar.netcafe.service.INetcafeMemberService;
import com.ztw.netbar.netcafe.service.INetcafeUsageRecordService;
import com.ztw.netbar.netcafe.service.INetcafeRechargeRecordService;
import com.ztw.netbar.netcafe.util.MemberJwtUtil;

/**
 * 会员端登录与开台接口
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/member")
public class MemberLoginController
{
    @Autowired
    private INetcafeMemberService memberService;

    @Autowired
    private INetcafeUsageRecordService usageRecordService;

    @Autowired
    private INetcafeMachineService machineService;

    @Autowired
    private INetcafeRechargeRecordService rechargeRecordService;

    @Autowired
    private INetcafeProductService productService;

    @Autowired
    private INetcafeSaleOrderService saleOrderService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /** 心跳Key前缀 */
    private static final String HEARTBEAT_KEY = "heartbeat:usage:";

    /**
     * 会员登录
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody Map<String, Object> params)
    {
        String idCard = (String) params.get("memberNo");
        String password = (String) params.get("password");

        if (idCard == null || password == null)
        {
            return AjaxResult.error("身份证号和密码不能为空");
        }

        // 根据卡号查询（正式会员用memberNo，临时卡用卡号）
        NetcafeMember member = memberService.selectNetcafeMemberByMemberNo(idCard);
        if (member == null)
        {
            return AjaxResult.error("卡号不存在，请确认后重试");
        }

        // 校验状态
        if (!NetcafeConstants.MEMBER_STATUS_NORMAL.equals(member.getStatus()))
        {
            String statusMsg = "1".equals(member.getStatus()) ? "账号已冻结" : "账号已注销";
            return AjaxResult.error(statusMsg + "，请联系店员处理");
        }

        // 临时卡过期校验
        if ("1".equals(member.getMemberType()) && member.getExpireTime() != null)
        {
            if (member.getExpireTime().before(new java.util.Date()))
            {
                return AjaxResult.error("临时卡已过期，请联系店员续期或开新卡");
            }
        }

        // 校验密码（BCrypt）
        if (member.getPassword() == null || !passwordEncoder.matches(password, member.getPassword()))
        {
            return AjaxResult.error("密码错误，请确认后重试");
        }

        // 生成Token（24小时有效）
        String token = MemberJwtUtil.createToken(member.getId(), member.getMemberNo(), member.getName());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("memberId", member.getId());
        result.put("memberNo", member.getMemberNo());
        result.put("name", member.getName());
        result.put("balance", member.getBalance());
        result.put("level", member.getLevel());

        return AjaxResult.success(result);
    }

    /**
     * 临时卡开台（无需登录）
     */
    @PostMapping("/usage/tempStart")
    public AjaxResult tempStart(@RequestBody Map<String, Object> params)
    {
        Long machineId = params.get("machineId") != null ? Long.valueOf(params.get("machineId").toString()) : null;
        if (machineId == null) { return AjaxResult.error("请选择机位"); }
        HashMap<String, Object> result = usageRecordService.startUsage(machineId, null);
        return AjaxResult.success(result);
    }

    /**
     * 临时卡结账（无需Token，根据recordId）
     */
    @PostMapping("/usage/tempEnd")
    public AjaxResult tempEnd(@RequestBody Map<String, Object> params)
    {
        Long recordId = params.get("recordId") != null ? Long.valueOf(params.get("recordId").toString()) : null;
        if (recordId == null) { return AjaxResult.error("记录ID不能为空"); }
        HashMap<String, Object> result = usageRecordService.endUsage(recordId);
        return AjaxResult.success(result);
    }

    /**
     * 上机开台（会员登录后调用）
     */
    @PostMapping("/usage/start")
    public AjaxResult startUsage(@RequestHeader("Member-Token") String token,
                                  @RequestBody Map<String, Object> params)
    {
        // 验证Token
        if (!MemberJwtUtil.validateToken(token))
        {
            return AjaxResult.error(401, "登录已过期，请重新登录");
        }

        Long memberId = MemberJwtUtil.getMemberId(token);
        Long machineId = params.get("machineId") != null ? Long.valueOf(params.get("machineId").toString()) : null;

        if (machineId == null)
        {
            return AjaxResult.error("请选择机位");
        }

        HashMap<String, Object> result = usageRecordService.startUsage(machineId, memberId);
        return AjaxResult.success(result);
    }

    /**
     * 会员下机结算（主动退出时调用）
     */
    @PostMapping("/usage/end")
    public AjaxResult endUsage(@RequestHeader("Member-Token") String token)
    {
        if (!MemberJwtUtil.validateToken(token))
        {
            return AjaxResult.error(401, "登录已过期");
        }
        Long memberId = MemberJwtUtil.getMemberId(token);
        // 查找当前上机记录
        NetcafeUsageRecord query = new NetcafeUsageRecord();
        query.setMemberId(memberId);
        query.setStatus(NetcafeConstants.USAGE_STATUS_ACTIVE);
        List<NetcafeUsageRecord> list = usageRecordService.selectNetcafeUsageRecordList(query);
        if (list.isEmpty())
        {
            return AjaxResult.success("无进行中的上机记录");
        }
        HashMap<String, Object> result = usageRecordService.endUsage(list.get(0).getId());
        return AjaxResult.success(result);
    }

    /**
     * 心跳保活（每15秒调用一次，防止异常断线）
     */
    @PostMapping("/heartbeat")
    public AjaxResult heartbeat(@RequestHeader("Member-Token") String token,
                                 @RequestBody Map<String, Object> params)
    {
        if (!MemberJwtUtil.validateToken(token))
        {
            return AjaxResult.error(401, "登录已过期");
        }
        Long recordId = params.get("recordId") != null ? Long.valueOf(params.get("recordId").toString()) : null;
        if (recordId == null)
        {
            return AjaxResult.success("ok");
        }
        // Redis写入心跳，60秒过期
        String key = HEARTBEAT_KEY + recordId;
        redisCache.setCacheObject(key, "1", 60, TimeUnit.SECONDS);
        return AjaxResult.success("ok");
    }

    /**
     * 获取空闲机位列表（公开接口，会员登录前调用）
     */
    @GetMapping("/machines")
    public AjaxResult idleMachines()
    {
        NetcafeMachine query = new NetcafeMachine();
        query.setStatus(NetcafeConstants.MACHINE_STATUS_IDLE);
        List<NetcafeMachine> list = machineService.selectNetcafeMachineList(query);
        List<Map<String, Object>> result = list.stream().map(m -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", m.getId());
            item.put("machineNo", m.getMachineNo());
            item.put("type", m.getType());
            item.put("pricePerHour", m.getPricePerHour());
            return item;
        }).collect(Collectors.toList());
        return AjaxResult.success(result);
    }

    /**
     * 会员信息查询
     */
    @GetMapping("/info")
    public AjaxResult info(@RequestHeader("Member-Token") String token)
    {
        if (!MemberJwtUtil.validateToken(token))
        {
            return AjaxResult.error(401, "登录已过期，请重新登录");
        }

        Long memberId = MemberJwtUtil.getMemberId(token);
        NetcafeMember member = memberService.selectNetcafeMemberById(memberId);
        if (member == null)
        {
            return AjaxResult.error("会员不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("memberId", member.getId());
        result.put("memberNo", member.getMemberNo());
        result.put("name", member.getName());
        result.put("balance", member.getBalance());
        result.put("level", member.getLevel());
        result.put("totalSpent", member.getTotalSpent());
        result.put("status", member.getStatus());

        return AjaxResult.success(result);
    }

    /**
     * 查询当前上机记录
     */
    @GetMapping("/usage/current")
    public AjaxResult currentUsage(@RequestHeader("Member-Token") String token)
    {
        if (!MemberJwtUtil.validateToken(token))
        {
            return AjaxResult.error(401, "登录已过期");
        }
        Long memberId = MemberJwtUtil.getMemberId(token);
        NetcafeUsageRecord query = new NetcafeUsageRecord();
        query.setMemberId(memberId);
        query.setStatus(NetcafeConstants.USAGE_STATUS_ACTIVE);
        List<NetcafeUsageRecord> list = usageRecordService.selectNetcafeUsageRecordList(query);
        if (list.isEmpty())
        {
            return AjaxResult.success(null);
        }
        NetcafeUsageRecord record = list.get(0);
        Map<String, Object> result = new HashMap<>();
        result.put("recordId", record.getId());
        result.put("machineId", record.getMachineId());
        result.put("machineNo", record.getMachineNo());
        result.put("startTime", record.getStartTime());
        return AjaxResult.success(result);
    }

    /**
     * 会员充值
     */
    @PostMapping("/recharge")
    public AjaxResult recharge(@RequestHeader("Member-Token") String token,
                                @RequestBody Map<String, Object> params)
    {
        if (!MemberJwtUtil.validateToken(token))
        {
            return AjaxResult.error(401, "登录已过期");
        }
        Long memberId = MemberJwtUtil.getMemberId(token);
        java.math.BigDecimal amount = new java.math.BigDecimal(params.get("amount").toString());
        String payType = params.get("payType") != null ? params.get("payType").toString() : "1";

        NetcafeRechargeRecord record = new NetcafeRechargeRecord();
        record.setMemberId(memberId);
        record.setAmount(amount);
        record.setPayType(payType);
        record.setOperator("会员自助充值");
        record.setRemark("会员端在线充值");
        rechargeRecordService.insertNetcafeRechargeRecord(record);

        NetcafeMember member = memberService.selectNetcafeMemberById(memberId);
        return AjaxResult.success(member.getBalance());
    }

    /**
     * 上机记录列表
     */
    @GetMapping("/records")
    public AjaxResult records(@RequestHeader("Member-Token") String token)
    {
        if (!MemberJwtUtil.validateToken(token))
        {
            return AjaxResult.error(401, "登录已过期");
        }
        Long memberId = MemberJwtUtil.getMemberId(token);
        NetcafeUsageRecord query = new NetcafeUsageRecord();
        query.setMemberId(memberId);
        query.getParams().put("beginTime", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(System.currentTimeMillis() - 7L * 24 * 3600 * 1000)));
        List<NetcafeUsageRecord> list = usageRecordService.selectNetcafeUsageRecordList(query);
        return AjaxResult.success(list);
    }

    /**
     * 商品列表（会员端，返回上架且库存>0的商品）
     */
    @GetMapping("/products")
    public AjaxResult products()
    {
        NetcafeProduct query = new NetcafeProduct();
        query.setStatus("0");
        List<NetcafeProduct> list = productService.selectNetcafeProductList(query);
        List<Map<String, Object>> result = new ArrayList<>();
        for (NetcafeProduct p : list)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            item.put("productName", p.getProductName());
            item.put("category", p.getCategory());
            item.put("price", p.getPrice());
            item.put("stock", p.getStock());
            item.put("imageUrl", p.getRemark()); // 图片URL暂用remark字段
            result.add(item);
        }
        return AjaxResult.success(result);
    }

    /**
     * 会员端购物车结算
     */
    @PostMapping("/sale/create")
    public AjaxResult createSale(@RequestHeader("Member-Token") String token,
                                  @RequestBody Map<String, Object> params)
    {
        if (!MemberJwtUtil.validateToken(token))
        {
            return AjaxResult.error(401, "登录已过期");
        }
        Long memberId = MemberJwtUtil.getMemberId(token);
        String payType = params.get("payType") != null ? params.get("payType").toString() : "1";

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itemsMap = (List<Map<String, Object>>) params.get("items");
        if (itemsMap == null || itemsMap.isEmpty())
        {
            return AjaxResult.error("购物车为空");
        }

        List<SaleItemDTO> items = new ArrayList<>();
        for (Map<String, Object> item : itemsMap)
        {
            SaleItemDTO dto = new SaleItemDTO();
            dto.setProductId(Long.valueOf(item.get("productId").toString()));
            dto.setQuantity(Integer.valueOf(item.get("quantity").toString()));
            items.add(dto);
        }

        HashMap<String, Object> result = saleOrderService.createSaleOrder(memberId, items, payType);
        return AjaxResult.success(result);
    }
}
