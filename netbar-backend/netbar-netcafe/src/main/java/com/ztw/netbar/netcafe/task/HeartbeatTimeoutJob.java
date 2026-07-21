package com.ztw.netbar.netcafe.task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.ztw.netbar.common.core.redis.RedisCache;
import com.ztw.netbar.netcafe.constant.NetcafeConstants;
import com.ztw.netbar.netcafe.domain.NetcafeUsageRecord;
import com.ztw.netbar.netcafe.mapper.NetcafeUsageRecordMapper;
import com.ztw.netbar.netcafe.service.INetcafeUsageRecordService;

/**
 * 心跳超时扫描：每分钟检查上机中但超过60秒未上报心跳的记录，自动结账
 *
 * @author ruoyi
 */
@Component
public class HeartbeatTimeoutJob
{
    private static final Logger log = LoggerFactory.getLogger(HeartbeatTimeoutJob.class);

    private static final String HEARTBEAT_KEY = "heartbeat:usage:";

    @Autowired
    private NetcafeUsageRecordMapper usageRecordMapper;

    @Autowired
    private INetcafeUsageRecordService usageRecordService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 每分钟扫描一次，检查心跳超时
     * Cron: 每分钟的第0秒执行
     */
    @Scheduled(cron = "0 * * * * ?")
    public void scanHeartbeatTimeout()
    {
        // 查询所有上机中的记录
        NetcafeUsageRecord query = new NetcafeUsageRecord();
        query.setStatus(NetcafeConstants.USAGE_STATUS_ACTIVE);
        List<NetcafeUsageRecord> activeRecords = usageRecordMapper.selectNetcafeUsageRecordList(query);

        Set<Long> timeoutIds = new HashSet<>();
        for (NetcafeUsageRecord record : activeRecords)
        {
            if (record.getMemberId() == null) continue; // 跳过临时卡
            String key = HEARTBEAT_KEY + record.getId();
            String val = redisCache.getCacheObject(key);
            if (val == null)
            {
                // 心跳超时，标记需要强制下线
                timeoutIds.add(record.getId());
                log.warn("心跳超时 recordId={}, memberId={}, 机器={}", record.getId(), record.getMemberId(), record.getMachineId());
            }
        }

        // 执行强制结账
        for (Long recordId : timeoutIds)
        {
            try
            {
                usageRecordService.endUsage(recordId);
                log.warn("异常断线自动结账 recordId={}", recordId);
            }
            catch (Exception e)
            {
                log.error("自动结账失败 recordId={}: {}", recordId, e.getMessage());
            }
        }

        if (!timeoutIds.isEmpty())
        {
            log.warn("本次心跳超时自动结账 {} 条记录", timeoutIds.size());
        }
    }
}
