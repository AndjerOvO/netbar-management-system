# 网咖综合管理系统 - 项目记忆快照

## 项目身份
基于 RuoYi-Vue3 (Spring Boot 4.0.6 + Vue 3.5) 二次开发的网咖管理系统。
原框架：若依 v3.9.2 前后端分离版
新模块：netbar-netcafe（com.ztw.netbar.netcafe）

## 项目信息

| 项 | 值 |
|------|------|
| 根目录 | `C:\Users\17591\Desktop\NetBarSystem\` |
| 后端目录 | `netbar-backend/` |
| 站点名称 | 网咖综合管理系统 |
| Maven groupId | `com.yourname.netbar` |
| Maven artifactId | `netbar-system` |
| Java 包名 | `com.ztw.netbar` |
| 前端包名 | `netbar-system` |

## 核心业务
- 会员管理（正式会员 + 临时卡）
- 设备管理（普通区/竞技区/包厢）
- 上机计费（分段计费：<15分免单/15-30分半小时/≥30分整小时）
- 商品销售（购物车 + 余额支付 + 库存防超卖）
- 充值管理
- 数据看板（ECharts 7天趋势）
- 强制下线（余额归零5分钟 + 心跳超时60秒）
- 会员端独立登录（JWT 24h有效，不存 Redis）

## 后端模块（5个）
netbar-admin / netbar-common / netbar-framework / netbar-module-system / netbar-netcafe

## 前端风格
浅色高级风：墨绿(#2d5a4b) + 香槟金(#c9a84c) + 侧边栏深蓝灰(#1a2332)

## 关键账号
后台: admin/admin123
会员: M001/330102199001011234/011234 (张三 钻石)
     M002/330102199002022345/022345 (李四 黄金)
     M003/330102199003033456/033456 (王五 普通)

## 环境
Java 21 / Maven 3.9.16 / Node v24 / MySQL 9.7(root/YOUR_DB_PASSWORD) / Redis 5.0.14.1

## 启动
双击 `C:\Users\17591\Desktop\NetBarSystem\start.bat`
或手动: Redis → `java -jar netbar-backend/netbar-admin/target/netbar-admin.jar` → `npm run dev`

## 已删除模块
ruoyi-generator（代码生成）、ruoyi-quartz（定时任务）
- 定时任务改用 @Scheduled（在 netbar-netcafe/task/ 下）
- 前端系统工具菜单已全部删除

## 新项目继承指引
1. 复制 netbar-netcafe 模块结构和 pom 配置模式
2. 后台页面放 `src/views/netcafe/`，API 放 `src/api/netcafe/`
3. Controller 继承 BaseController，分页用 `startPage()` + `getDataTable()`
4. 权限注解 `@PreAuthorize("@ss.hasPermi('netcafe:xxx:yyy')")`
5. 前端 API 用 `import request from '@/utils/request'`
6. 会员端独立 axios 实例，不走 admin 拦截器
7. 会员 JWT 工具类在 `netbar-netcafe/util/MemberJwtUtil.java`
8. Maven 模块依赖引用 `com.yourname.netbar:netbar-xxx`

## 内存记忆（供 /memory 检索）
关键决策：
- 密码=身份证后6位 BCrypt 加密 [[member-password]]
- 临时卡店员开具，24h过期 [[temp-card]]
- 分段计费 [[billing-rules]]
- 心跳保活机制 [[heartbeat]]
- 会员端与后台双Token隔离 [[dual-auth]]
- 防超卖库存扣减 SQL: WHERE stock >= quantity [[inventory-deduct]]
