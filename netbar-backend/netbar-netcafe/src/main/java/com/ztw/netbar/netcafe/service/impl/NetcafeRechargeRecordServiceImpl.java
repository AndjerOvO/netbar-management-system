package com.ztw.netbar.netcafe.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ztw.netbar.common.exception.ServiceException;
import com.ztw.netbar.netcafe.constant.NetcafeConstants;
import com.ztw.netbar.netcafe.domain.NetcafeMember;
import com.ztw.netbar.netcafe.domain.NetcafeRechargeRecord;
import com.ztw.netbar.netcafe.mapper.NetcafeMemberMapper;
import com.ztw.netbar.netcafe.mapper.NetcafeRechargeRecordMapper;
import com.ztw.netbar.netcafe.service.INetcafeRechargeRecordService;

/**
 * 充值记录 服务层实现
 *
 * @author ruoyi
 */
@Service
public class NetcafeRechargeRecordServiceImpl implements INetcafeRechargeRecordService
{
    @Autowired
    private NetcafeRechargeRecordMapper rechargeRecordMapper;

    @Autowired
    private NetcafeMemberMapper memberMapper;

    @Override
    public NetcafeRechargeRecord selectNetcafeRechargeRecordById(Long id)
    {
        return rechargeRecordMapper.selectNetcafeRechargeRecordById(id);
    }

    @Override
    public List<NetcafeRechargeRecord> selectNetcafeRechargeRecordList(NetcafeRechargeRecord record)
    {
        return rechargeRecordMapper.selectNetcafeRechargeRecordList(record);
    }

    /**
     * 充值：增加会员余额 + 写入充值记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertNetcafeRechargeRecord(NetcafeRechargeRecord record)
    {
        // 1. 校验会员
        NetcafeMember member = memberMapper.selectNetcafeMemberById(record.getMemberId());
        if (member == null)
        {
            throw new ServiceException("会员不存在");
        }
        if (!NetcafeConstants.MEMBER_STATUS_NORMAL.equals(member.getStatus()))
        {
            throw new ServiceException("会员状态异常，无法充值（状态：" +
                ("1".equals(member.getStatus()) ? "冻结" : "已注销") + "）");
        }

        // 2. 增加余额
        int rows = memberMapper.rechargeMemberBalance(record.getMemberId(), record.getAmount());
        if (rows <= 0)
        {
            throw new ServiceException("充值失败");
        }

        // 3. 写入充值记录
        return rechargeRecordMapper.insertNetcafeRechargeRecord(record);
    }
}
