package com.ztw.netbar.netcafe.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ztw.netbar.common.core.domain.AjaxResult;
import com.ztw.netbar.common.exception.ServiceException;
import com.ztw.netbar.netcafe.constant.NetcafeConstants;
import com.ztw.netbar.netcafe.domain.NetcafeMachine;
import com.ztw.netbar.netcafe.domain.NetcafeMember;
import com.ztw.netbar.netcafe.domain.NetcafeSaleOrder;
import com.ztw.netbar.netcafe.domain.NetcafeUsageRecord;
import com.ztw.netbar.netcafe.mapper.NetcafeMachineMapper;
import com.ztw.netbar.netcafe.mapper.NetcafeMemberMapper;
import com.ztw.netbar.netcafe.mapper.NetcafeSaleOrderMapper;
import com.ztw.netbar.netcafe.mapper.NetcafeUsageRecordMapper;
import com.ztw.netbar.netcafe.service.INetcafeUsageRecordService;

/**
 * 上机记录 服务层实现
 *
 * @author ruoyi
 */
@Service
public class NetcafeUsageRecordServiceImpl implements INetcafeUsageRecordService
{
    private static final Logger log = LoggerFactory.getLogger(NetcafeUsageRecordServiceImpl.class);

    @Autowired
    private NetcafeUsageRecordMapper usageRecordMapper;

    @Autowired
    private NetcafeMachineMapper machineMapper;

    @Autowired
    private NetcafeMemberMapper memberMapper;

    @Autowired
    private NetcafeSaleOrderMapper saleOrderMapper;

    @Override
    public NetcafeUsageRecord selectNetcafeUsageRecordById(Long id)
    {
        return usageRecordMapper.selectNetcafeUsageRecordById(id);
    }

    @Override
    public List<NetcafeUsageRecord> selectNetcafeUsageRecordList(NetcafeUsageRecord record)
    {
        return usageRecordMapper.selectNetcafeUsageRecordList(record);
    }

    /**
     * 上机开台
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public HashMap<String, Object> startUsage(Long machineId, Long memberId)
    {
        // 1. 校验机器状态
        NetcafeMachine machine = machineMapper.selectNetcafeMachineById(machineId);
        if (machine == null)
        {
            throw new ServiceException("机器不存在");
        }
        if (!NetcafeConstants.MACHINE_STATUS_IDLE.equals(machine.getStatus()))
        {
            throw new ServiceException("该机器当前不可用，状态：" +
                ("1".equals(machine.getStatus()) ? "使用中" : "故障"));
        }

        // 2. 校验会员（如果提供了 memberId）
        if (memberId != null)
        {
            NetcafeMember member = memberMapper.selectNetcafeMemberById(memberId);
            if (member == null)
            {
                throw new ServiceException("会员不存在");
            }
            if (!NetcafeConstants.MEMBER_STATUS_NORMAL.equals(member.getStatus()))
            {
                throw new ServiceException("会员状态异常，无法上机");
            }
            if (member.getBalance().compareTo(NetcafeConstants.MIN_BALANCE) < 0)
            {
                throw new ServiceException("余额不足，最低余额需 ≥ " + NetcafeConstants.MIN_BALANCE + " 元，当前余额：" + member.getBalance() + " 元");
            }
        }

        // 3. 创建上机记录
        NetcafeUsageRecord record = new NetcafeUsageRecord();
        record.setMemberId(memberId);
        record.setMachineId(machineId);
        record.setStartTime(new Date());
        record.setStatus(NetcafeConstants.USAGE_STATUS_ACTIVE);
        usageRecordMapper.insertNetcafeUsageRecord(record);

        // 4. 更新机器状态为"使用中"
        machineMapper.updateNetcafeMachineStatus(machineId, NetcafeConstants.MACHINE_STATUS_IN_USE);

        HashMap<String, Object> result = new HashMap<>();
        result.put("recordId", record.getId());
        result.put("startTime", record.getStartTime());
        return result;
    }

    /**
     * 下机结算
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public HashMap<String, Object> endUsage(Long recordId)
    {
        // 1. 获取上机记录
        NetcafeUsageRecord record = usageRecordMapper.selectNetcafeUsageRecordById(recordId);
        if (record == null)
        {
            throw new ServiceException("上机记录不存在");
        }
        if (!NetcafeConstants.USAGE_STATUS_ACTIVE.equals(record.getStatus()))
        {
            throw new ServiceException("该记录已结算，请勿重复操作");
        }

        // 2. 获取机器信息
        NetcafeMachine machine = machineMapper.selectNetcafeMachineById(record.getMachineId());
        if (machine == null)
        {
            throw new ServiceException("机器信息不存在");
        }

        // 3. 计算费用（分段计费）
        Date now = new Date();
        long diffMs = now.getTime() - record.getStartTime().getTime();
        int totalMinutes = (int) (diffMs / (1000 * 60));
        BigDecimal pricePerHour = machine.getPricePerHour();
        BigDecimal totalAmount = BigDecimal.ZERO;

        if (totalMinutes < 15) {
            // <15分钟：免单（防止测试/异常数据）
            totalAmount = BigDecimal.ZERO;
        } else if (totalMinutes < 30) {
            // 15-30分钟：按半小时计费
            totalAmount = pricePerHour.multiply(new BigDecimal("0.5"));
        } else {
            // >=30分钟：按整小时向上取整
            int hours = (int) Math.ceil(totalMinutes / 60.0);
            totalAmount = pricePerHour.multiply(new BigDecimal(hours));
        }
        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);

        // 4. 结算
        record.setEndTime(now);
        record.setTotalMinutes(totalMinutes);
        record.setTotalAmount(totalAmount);
        record.setStatus(NetcafeConstants.USAGE_STATUS_ENDED);
        usageRecordMapper.updateNetcafeUsageRecord(record);

        // 5. 释放机器
        machineMapper.updateNetcafeMachineStatus(record.getMachineId(), NetcafeConstants.MACHINE_STATUS_IDLE);

        HashMap<String, Object> result = new HashMap<>();

        if (record.getMemberId() != null)
        {
            // 5a. 会员：从余额扣费
            NetcafeMember member = memberMapper.selectNetcafeMemberById(record.getMemberId());
            if (member.getBalance().compareTo(totalAmount) < 0)
            {
                throw new ServiceException("余额不足，请充值。当前余额：" + member.getBalance() + " 元，需支付：" + totalAmount + " 元");
            }
            int rows = memberMapper.deductMemberBalance(record.getMemberId(), totalAmount);
            if (rows <= 0)
            {
                throw new ServiceException("扣费失败，请重试");
            }
            result.put("payType", "余额扣费");
            result.put("memberBalance", member.getBalance().subtract(totalAmount));
        }
        else
        {
            // 5b. 临时卡：生成销售订单（pay_type=待支付标记）
            NetcafeSaleOrder order = new NetcafeSaleOrder();
            order.setTotalPrice(totalAmount);
            order.setPayType(NetcafeConstants.PAY_TYPE_CASH);
            saleOrderMapper.insertNetcafeSaleOrder(order);
            result.put("payType", "现金支付");
            result.put("orderId", order.getId());
        }

        result.put("recordId", recordId);
        result.put("totalMinutes", totalMinutes);
        result.put("pricePerHour", pricePerHour);
        result.put("totalAmount", totalAmount);
        return result;
    }
}
