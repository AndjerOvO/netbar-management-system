package com.ztw.netbar.netcafe.task;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.ztw.netbar.netcafe.domain.NetcafeMember;
import com.ztw.netbar.netcafe.domain.NetcafeUsageRecord;
import com.ztw.netbar.netcafe.mapper.NetcafeMemberMapper;
import com.ztw.netbar.netcafe.mapper.NetcafeUsageRecordMapper;
import com.ztw.netbar.netcafe.service.INetcafeUsageRecordService;

/**
 * 会员强制下线定时任务（每分钟执行一次）
 *
 * @author ruoyi
 */
@Component
public class MemberForceOfflineTask
{
    private static final Logger log = LoggerFactory.getLogger(MemberForceOfflineTask.class);

    @Autowired
    private NetcafeUsageRecordMapper usageRecordMapper;

    @Autowired
    private NetcafeMemberMapper memberMapper;

    @Autowired
    private INetcafeUsageRecordService usageRecordService;

    /**
     * 每分钟扫描一次
     * 1. 余额=0且未标记 → 写入force_offline_time
     * 2. 余额>0且有标记 → 清除force_offline_time
     * 3. 余额=0且已超过5分钟 → 强制下机
     */
    @Scheduled(fixedRate = 60000)
    public void checkForceOffline()
    {
        log.debug("开始执行强制下线检查...");

        // 1. 查询所有上机中的会员记录
        NetcafeUsageRecord query = new NetcafeUsageRecord();
        query.setStatus("0");
        List<NetcafeUsageRecord> activeRecords = usageRecordMapper.selectNetcafeUsageRecordList(query);

        for (NetcafeUsageRecord record : activeRecords)
        {
            if (record.getMemberId() == null)
            {
                continue; // 跳过临时卡
            }

            NetcafeMember member = memberMapper.selectNetcafeMemberById(record.getMemberId());
            if (member == null)
            {
                continue;
            }

            if (member.getBalance().compareTo(java.math.BigDecimal.ZERO) <= 0)
            {
                // 余额归零
                if (record.getForceOfflineTime() == null)
                {
                    // 首次检测到余额为0，记录时间
                    usageRecordMapper.updateForceOfflineTime(record.getId(), new Date());
                    log.warn("会员[{}]余额归零，开始5分钟强制下线倒计时", member.getName());
                }
                // force_offline_time已设置的记录由selectForceOfflineRecords处理
            }
            else
            {
                // 余额恢复，清除倒计时
                if (record.getForceOfflineTime() != null)
                {
                    usageRecordMapper.clearForceOfflineTime(record.getMemberId());
                    log.info("会员[{}]余额恢复，清除强制下线倒计时", member.getName());
                }
            }
        }

        // 2. 查询需要强制下线的记录（force_offline_time >= 5分钟）
        List<NetcafeUsageRecord> forceList = usageRecordMapper.selectForceOfflineRecords();
        for (NetcafeUsageRecord record : forceList)
        {
            try
            {
                log.warn("会员[{}]余额不足超时，强制下线 recordId={}", record.getMemberName(), record.getId());
                usageRecordService.endUsage(record.getId());
            }
            catch (Exception e)
            {
                log.error("强制下线失败 recordId={}: {}", record.getId(), e.getMessage());
            }
        }

        if (!forceList.isEmpty())
        {
            log.warn("本次强制下线 {} 条记录", forceList.size());
        }
    }
}
