package com.ztw.netbar.netcafe.constant;

import java.math.BigDecimal;

/**
 * 网咖业务常量
 *
 * @author ruoyi
 */
public class NetcafeConstants
{
    /** 会员最低余额（元） */
    public static final BigDecimal MIN_BALANCE = new BigDecimal("10.00");

    /** 上机状态：上机中 */
    public static final String USAGE_STATUS_ACTIVE = "0";

    /** 上机状态：已下机 */
    public static final String USAGE_STATUS_ENDED = "1";

    /** 机器状态：空闲 */
    public static final String MACHINE_STATUS_IDLE = "0";

    /** 机器状态：使用中 */
    public static final String MACHINE_STATUS_IN_USE = "1";

    /** 机器状态：故障 */
    public static final String MACHINE_STATUS_FAULT = "2";

    /** 会员状态：正常 */
    public static final String MEMBER_STATUS_NORMAL = "0";

    /** 会员状态：冻结 */
    public static final String MEMBER_STATUS_FROZEN = "1";

    /** 支付方式：现金 */
    public static final String PAY_TYPE_CASH = "0";

    /** 支付方式：余额 */
    public static final String PAY_TYPE_BALANCE = "1";

    /** 支付方式：微信 */
    public static final String PAY_TYPE_WECHAT = "2";
}
