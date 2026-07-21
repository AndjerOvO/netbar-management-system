# 网咖综合管理系统

基于 RuoYi-Vue3（Spring Boot 4.0.6 + Vue 3）二次开发的网咖管理系统。

## 项目路径

```
C:\Users\17591\Desktop\NetBarSystem\
```

## 启动方式

### 一键启动

双击 `C:\Users\17591\Desktop\NetBarSystem\start.bat`

### 手动启动

```bash
# 1. Redis
C:\Users\17591\redis\redis-server.exe C:\Users\17591\redis\redis.windows.conf

# 2. 后端 (端口 8080)
java -jar C:\Users\17591\Desktop\NetBarSystem\netbar-backend\netbar-admin\target\netbar-admin.jar

# 3. 前端 (端口 8088)
cd C:\Users\17591\Desktop\NetBarSystem
npm run dev
```

### 停止

```bash
taskkill /F /IM java.exe
C:\Users\17591\redis\redis-cli.exe shutdown
taskkill /F /IM node.exe  # 前端
```

## 访问地址

| 入口 | URL | 账号 | 密码 |
|------|------|------|------|
| 后台管理 | http://localhost:8088 | admin | admin123 |
| 会员端 | http://localhost:8088/member | M001 | 011234 |
| API 文档 | http://localhost:8080/swagger-ui.html | — | — |
| Druid 监控 | http://localhost:8080/druid | netbar | 123456 |

## 测试会员

| 姓名 | 卡号 | 身份证号 | 密码（身份证后6位） |
|------|------|------|------|
| 张三 | M001 | 330102199001011234 | 011234 |
| 李四 | M002 | 330102199002022345 | 022345 |
| 王五 | M003 | 330102199003033456 | 033456 |

## 环境配置

| 组件 | 版本/路径 |
|------|------|
| Java | 21.0.11 (C:\Program Files\Java\jdk-21.0.11) |
| Maven | 3.9.16 (C:\Users\17591\Desktop\getjob\apache-maven-3.9.16) |
| Node.js | v24.18.0 |
| MySQL | 9.7 (端口 3306, root/YOUR_DB_PASSWORD) |
| Redis | 5.0.14.1 (端口 6379, 无密码) |

## 后端模块结构

```
netbar-backend\
├── netbar-admin/       # 启动入口 + Controller
│   └── src/main/java/com/ztw/netbar/
│       ├── RuoYiApplication.java          # Spring Boot 启动类
│       ├── RuoYiServletInitializer.java   # Servlet 初始化
│       └── web/
│           ├── controller/
│           │   ├── netcafe/    # 网咖业务 Controller（会员/机器/商品/上机/销售/充值）
│           │   ├── member/      # 会员端 Controller（登录/开台/心跳/商品）
│           │   ├── monitor/     # 系统监控 Controller
│           │   └── system/      # 系统管理 Controller
│           └── core/config/     # Swagger 等配置
├── netbar-common/       # 公共工具类
│   └── com.ztw.netbar.common/
│       ├── config/      # RuoYiConfig 等配置类
│       ├── annotation/  # 自定义注解
│       ├── utils/       # 工具类（文件/Excel/IP）
│       └── xss/         # XSS 过滤
├── netbar-framework/    # 框架核心（Security/JWT/配置）
│   └── com.ztw.netbar.framework/
│       ├── aspectj/     # AOP 切面（日志/数据权限/限流）
│       ├── config/      # Security 等配置
│       └── web/service/ # Token 服务等
├── netbar-module-system/       # 系统业务（用户/角色/菜单）
│   └── com.ztw.netbar.system/
│       ├── domain/      # 实体类
│       ├── mapper/      # MyBatis Mapper
│       └── service/     # 业务逻辑
└── netbar-netcafe/      # 🔥 网咖业务模块
    └── com.ztw.netbar.netcafe/
        ├── constant/    # 常量
        ├── domain/      # 实体类
        ├── dto/         # 传输对象
        ├── mapper/      # MyBatis Mapper
        ├── service/     # 业务逻辑
        ├── task/        # 定时任务（心跳超时/强制下线）
        └── util/        # 会员JWT工具类
    └── resources/mapper/netcafe/  # Mapper XML
```

**Maven 坐标**

| 项目 | 值 |
|------|------|
| groupId | `com.yourname.netbar` |
| artifactId | `netbar-system` |
| version | `3.9.2` |
| Java 包名 | `com.ztw.netbar` |

已删除模块：`ruoyi-generator`（代码生成）、`ruoyi-quartz`（定时任务框架）

## 前端目录结构

```
src/
├── api/
│   ├── member/         # 会员端 API
│   │   └── index.js    # 登录/开台/商品/购物车/心跳
│   ├── netcafe/        # 网咖管理 API
│   │   ├── member.js   # 会员管理
│   │   ├── machine.js  # 设备管理
│   │   ├── product.js  # 商品管理
│   │   ├── sale.js     # 上机/销售/充值
│   │   └── dashboard.js
│   ├── monitor/        # 系统监控 API
│   └── system/         # 系统管理 API
├── views/
│   ├── netcafe/        # 后台管理页面
│   │   ├── member/     # 会员管理（含开临时卡）
│   │   ├── machine/    # 设备管理
│   │   ├── product/    # 商品管理
│   │   ├── usage/      # 上机记录
│   │   ├── order/      # 销售订单
│   │   └── recharge/   # 充值记录
│   ├── member/         # 会员端页面
│   │   ├── login.vue   # 会员登录（卡号+密码）
│   │   └── dashboard.vue # 会员仪表盘（计时/余额/购物/价格表）
│   ├── monitor/        # 系统监控
│   └── system/         # 系统管理
├── assets/styles/      # 全局样式（浅色高级风）
└── router/index.js     # 路由配置
```

## 数据库表

### 网咖业务表（前缀 netcafe_）

| 表名 | 说明 |
|------|------|
| netcafe_member | 会员（member_type: 0正式/1临时卡） |
| netcafe_machine | 机器 |
| netcafe_usage_record | 上机记录（force_offline_time 强制下线计时） |
| netcafe_product | 商品 |
| netcafe_sale_order | 销售订单 |
| netcafe_sale_item | 销售明细 |
| netcafe_recharge_record | 充值记录 |

### 系统表（若依框架）

| 表名 | 说明 |
|------|------|
| sys_user | 后台用户 |
| sys_role | 角色 |
| sys_menu | 菜单/权限 |
| sys_dept | 部门 |
| sys_config | 系统参数 |

## 常用命令

```bash
# 编译后端（不打包）
cd C:\Users\17591\Desktop\NetBarSystem\netbar-backend
mvn compile -DskipTests

# 编译+打包后端
mvn package -DskipTests

# 停止所有 Java 进程
taskkill /F /IM java.exe

# 测试 Redis 连接
C:\Users\17591\redis\redis-cli.exe ping

# MySQL 连接
"C:\Program Files\MySQL\MySQL Server 9.7\bin\mysql.exe" -u root -p123456 ry-vue

# 前端开发模式
cd C:\Users\17591\Desktop\NetBarSystem
npm run dev
```

## 关键设计

- **身份证后6位即密码**：新增会员时自动取身份证后6位 BCrypt 加密
- **临时卡**：店员后台开具，卡号=TEMP+时间戳，24h 过期，密码=身份证后6位
- **分段计费**：<15分钟免单，15-30分钟半小时价，≥30分钟整小时向上取整
- **心跳保活**：15秒一次，60秒超时自动结账（防关浏览器不释放机位）
- **强制下线**：余额归零5分钟后自动结账
- **会员Token**：Member-Token 头，24h 有效，不存 Redis
- **购物车**：余额支付，库存防超卖扣减，异步低库存预警
- **双Token隔离**：后台管理用 `Authorization`（JWT + Redis），会员端用 `Member-Token`（JWT 24h 不存 Redis）