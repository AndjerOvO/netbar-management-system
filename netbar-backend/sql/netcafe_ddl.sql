-- ============================================
-- RuoYi-Vue 网咖管理系统 DDL
-- 模块: ruoyi-netcafe
-- 表前缀: netcafe_
-- ============================================

-- ----------------------------
-- 1. 会员表
-- ----------------------------
DROP TABLE IF EXISTS netcafe_member;
CREATE TABLE netcafe_member (
    id              BIGINT(20)      NOT NULL AUTO_INCREMENT  COMMENT '会员ID',
    member_no       VARCHAR(32)     NOT NULL                 COMMENT '会员编号（唯一）',
    name            VARCHAR(64)     NOT NULL                 COMMENT '会员姓名',
    phone           VARCHAR(20)     DEFAULT NULL             COMMENT '手机号码',
    password        VARCHAR(128)    DEFAULT NULL             COMMENT '会员密码（BCrypt加密）',
    balance         DECIMAL(12,2)   NOT NULL DEFAULT 0.00    COMMENT '账户余额',
    total_spent     DECIMAL(12,2)   NOT NULL DEFAULT 0.00    COMMENT '累计消费金额',
    level           CHAR(1)         NOT NULL DEFAULT '0'     COMMENT '会员等级（0普通 1黄金 2钻石）',
    status          CHAR(1)         NOT NULL DEFAULT '0'     COMMENT '会员状态（0正常 1冻结 2注销）',
    create_by       VARCHAR(64)     DEFAULT ''              COMMENT '创建者',
    create_time     DATETIME        DEFAULT NULL             COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT ''              COMMENT '更新者',
    update_time     DATETIME        DEFAULT NULL             COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT NULL             COMMENT '备注',
    del_flag        CHAR(1)         DEFAULT '0'              COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    UNIQUE INDEX idx_member_no (member_no),
    INDEX idx_member_phone (phone),
    INDEX idx_member_name (name),
    INDEX idx_member_level (level),
    INDEX idx_member_status (status),
    CHECK (balance >= 0)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='网咖会员表';


-- ----------------------------
-- 2. 机器表
-- ----------------------------
DROP TABLE IF EXISTS netcafe_machine;
CREATE TABLE netcafe_machine (
    id              BIGINT(20)      NOT NULL AUTO_INCREMENT  COMMENT '机器ID',
    machine_no      VARCHAR(32)     NOT NULL                 COMMENT '机器编号（唯一）',
    type            VARCHAR(20)     NOT NULL                 COMMENT '机器类型（普通区 竞技区 包厢）',
    price_per_hour  DECIMAL(8,2)    NOT NULL                 COMMENT '每小时单价',
    status          CHAR(1)         NOT NULL DEFAULT '0'     COMMENT '机器状态（0空闲 1使用中 2故障）',
    create_by       VARCHAR(64)     DEFAULT ''              COMMENT '创建者',
    create_time     DATETIME        DEFAULT NULL             COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT ''              COMMENT '更新者',
    update_time     DATETIME        DEFAULT NULL             COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT NULL             COMMENT '备注',
    del_flag        CHAR(1)         DEFAULT '0'              COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    UNIQUE INDEX idx_machine_no (machine_no),
    INDEX idx_machine_type (type),
    INDEX idx_machine_status (status)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='网咖机器表';


-- ----------------------------
-- 3. 上机记录表
-- ----------------------------
DROP TABLE IF EXISTS netcafe_usage_record;
CREATE TABLE netcafe_usage_record (
    id              BIGINT(20)      NOT NULL AUTO_INCREMENT  COMMENT '记录ID',
    member_id       BIGINT(20)      DEFAULT NULL             COMMENT '会员ID（临时卡为NULL）',
    machine_id      BIGINT(20)      NOT NULL                 COMMENT '机器ID',
    start_time      DATETIME        NOT NULL                 COMMENT '上机时间',
    end_time        DATETIME        DEFAULT NULL             COMMENT '下机时间',
    total_minutes   INT(11)         DEFAULT 0                COMMENT '上机总时长（分钟）',
    total_amount    DECIMAL(10,2)   DEFAULT 0.00             COMMENT '上机总费用',
    status          CHAR(1)         NOT NULL DEFAULT '0'     COMMENT '上机状态（0上机中 1已下机）',
    create_by       VARCHAR(64)     DEFAULT ''              COMMENT '创建者',
    create_time     DATETIME        DEFAULT NULL             COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT ''              COMMENT '更新者',
    update_time     DATETIME        DEFAULT NULL             COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT NULL             COMMENT '备注',
    del_flag        CHAR(1)         DEFAULT '0'              COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    INDEX idx_usage_member_id (member_id),
    INDEX idx_usage_machine_id (machine_id),
    INDEX idx_usage_status (status),
    INDEX idx_usage_start_time (start_time)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='网上记录表';


-- ----------------------------
-- 4. 商品表
-- ----------------------------
DROP TABLE IF EXISTS netcafe_product;
CREATE TABLE netcafe_product (
    id              BIGINT(20)      NOT NULL AUTO_INCREMENT  COMMENT '商品ID',
    product_name    VARCHAR(128)    NOT NULL                 COMMENT '商品名称',
    category        VARCHAR(32)     NOT NULL                 COMMENT '商品分类（饮料 零食 外设）',
    price           DECIMAL(10,2)   NOT NULL                 COMMENT '商品售价',
    stock           INT(11)         NOT NULL DEFAULT 0       COMMENT '库存数量',
    warning_stock   INT(11)         NOT NULL DEFAULT 10      COMMENT '库存预警阈值',
    status          CHAR(1)         NOT NULL DEFAULT '0'     COMMENT '上架状态（0上架 1下架）',
    create_by       VARCHAR(64)     DEFAULT ''              COMMENT '创建者',
    create_time     DATETIME        DEFAULT NULL             COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT ''              COMMENT '更新者',
    update_time     DATETIME        DEFAULT NULL             COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT NULL             COMMENT '备注',
    del_flag        CHAR(1)         DEFAULT '0'              COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    INDEX idx_product_name (product_name),
    INDEX idx_product_category (category),
    INDEX idx_product_status (status)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='网咖商品表';


-- ----------------------------
-- 5. 销售订单表
-- ----------------------------
DROP TABLE IF EXISTS netcafe_sale_order;
CREATE TABLE netcafe_sale_order (
    id              BIGINT(20)      NOT NULL AUTO_INCREMENT  COMMENT '订单ID',
    member_id       BIGINT(20)      DEFAULT NULL             COMMENT '会员ID（散客为NULL）',
    total_price     DECIMAL(12,2)   NOT NULL DEFAULT 0.00    COMMENT '订单总价',
    pay_type        CHAR(1)         NOT NULL DEFAULT '0'     COMMENT '支付方式（0现金 1余额 2微信）',
    create_by       VARCHAR(64)     DEFAULT ''              COMMENT '创建者',
    create_time     DATETIME        DEFAULT NULL             COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT ''              COMMENT '更新者',
    update_time     DATETIME        DEFAULT NULL             COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT NULL             COMMENT '备注',
    del_flag        CHAR(1)         DEFAULT '0'              COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    INDEX idx_order_member_id (member_id),
    INDEX idx_order_pay_type (pay_type),
    INDEX idx_order_create_time (create_time)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='网咖销售订单表';


-- ----------------------------
-- 6. 销售订单明细表
-- ----------------------------
DROP TABLE IF EXISTS netcafe_sale_item;
CREATE TABLE netcafe_sale_item (
    id              BIGINT(20)      NOT NULL AUTO_INCREMENT  COMMENT '明细ID',
    order_id        BIGINT(20)      NOT NULL                 COMMENT '订单ID',
    product_id      BIGINT(20)      NOT NULL                 COMMENT '商品ID',
    quantity        INT(11)         NOT NULL DEFAULT 1       COMMENT '购买数量',
    price_at_time   DECIMAL(10,2)   NOT NULL                 COMMENT '下单时商品单价',
    create_by       VARCHAR(64)     DEFAULT ''              COMMENT '创建者',
    create_time     DATETIME        DEFAULT NULL             COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT ''              COMMENT '更新者',
    update_time     DATETIME        DEFAULT NULL             COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT NULL             COMMENT '备注',
    del_flag        CHAR(1)         DEFAULT '0'              COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    INDEX idx_item_order_id (order_id),
    INDEX idx_item_product_id (product_id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='网咖销售订单明细表';


-- ----------------------------
-- 7. 充值记录表
-- ----------------------------
DROP TABLE IF EXISTS netcafe_recharge_record;
CREATE TABLE netcafe_recharge_record (
    id              BIGINT(20)      NOT NULL AUTO_INCREMENT  COMMENT '充值记录ID',
    member_id       BIGINT(20)      NOT NULL                 COMMENT '会员ID',
    amount          DECIMAL(12,2)   NOT NULL                 COMMENT '充值金额',
    pay_type        CHAR(1)         NOT NULL DEFAULT '0'     COMMENT '支付方式（0现金 1微信）',
    operator        VARCHAR(64)     NOT NULL                 COMMENT '操作人',
    create_by       VARCHAR(64)     DEFAULT ''              COMMENT '创建者',
    create_time     DATETIME        DEFAULT NULL             COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT ''              COMMENT '更新者',
    update_time     DATETIME        DEFAULT NULL             COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT NULL             COMMENT '备注',
    del_flag        CHAR(1)         DEFAULT '0'              COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (id),
    INDEX idx_recharge_member_id (member_id),
    INDEX idx_recharge_create_time (create_time),
    CHECK (amount > 0)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='网咖充值记录表';
