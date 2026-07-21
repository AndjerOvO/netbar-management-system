# 网咖综合管理系统

<div align="center">

**基于 [RuoYi-Vue3](https://gitee.com/y_project/RuoYi-Vue) 二次开发 · Maven 多模块 · Spring Boot + Vue 3**

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.5-4FC08D.svg)](https://vuejs.org/)

</div>

---

## 项目简介

网咖综合管理系统是一套面向网吧/网咖场景的全流程业务管理平台，基于 RuoYi-Vue3 前后端分离框架进行深度二次开发。系统覆盖会员管理、设备分区计费、商品销售、上机实时监控、数据统计分析等核心业务模块，支持后台管理与会员自助端双入口，内置心跳保活、余额强制下线、库存防超卖等网咖特有业务逻辑。

> 原框架若依（RuoYi-Vue3）为国内最广泛使用的 Java 后台管理系统脚手架之一，本项目在其权限管理、代码生成等基础设施之上，删减了无关模块（代码生成器、定时任务框架等），新增 `netbar-netcafe` 网咖业务模块，重构了用户/角色管理（移除部门、岗位），并适配了暗黑模式与移动端响应式布局。

## 核心功能

| 模块 | 功能 |
|------|------|
| 👥 会员管理 | 正式会员 / 临时卡（24h 过期），身份证后 6 位即为登录密码 |
| 🖥️ 设备管理 | 分区管理（普通区 / 竞技区 / 包厢），机位状态实时监控 |
| ⏱️ 分段计费 | <15 分钟免单，15-30 分按半小时，≥30 分整小时向上取整 |
| 💓 心跳保活 | 会员端每 15 秒发送心跳，60 秒超时自动结账释放机位 |
| 🛑 强制下线 | 余额归零 5 分钟后自动结账，防恶意占用 |
| 🛒 商品销售 | 购物车模式，余额支付，`WHERE stock >= quantity` 防超卖扣减 |
| 💰 充值管理 | 充值记录追踪，余额变动流水 |
| 📊 数据看板 | ECharts 近 7 天网费/会员/商品销售趋势图 |
| 🔐 双 Token 隔离 | 后台 `Authorization`（JWT + Redis），会员端 `Member-Token`（JWT 24h） |
| 🌗 暗黑模式 | View Transitions API 动画切换，浅色墨绿 + 香槟金配色 |

## 技术栈

详见 [TECH_STACK.md](TECH_STACK.md)

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| JDK | 21 | 运行环境 |
| Spring Boot | 4.0.6 | 核心框架 |
| Spring Security + JWT | — | 认证鉴权 |
| MyBatis + PageHelper | 4.0.1 / 4.1.0 | 持久层 + 分页 |
| MySQL | 9.7 | 关系数据库 |
| Redis | 5.0 | 缓存 / Token 管理 |
| Druid | 1.2.28 | 数据库连接池与监控 |
| SpringDoc | 3.0.3 | API 文档 (Swagger UI) |

### 前端

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue 3 | 3.5.26 | 渐进式框架 |
| Vite | 6.4.1 | 构建工具 |
| Element Plus | 2.13.1 | UI 组件库 |
| Pinia | 3.0.4 | 状态管理 |
| ECharts | 5.6.0 | 数据图表 |
| Axios | 1.13.2 | HTTP 客户端 |

## 项目结构

```
NetBarSystem/
├── netbar-backend/                    # 后端 Maven 父工程
│   ├── netbar-admin/                  # 启动入口 + REST Controller
│   ├── netbar-common/                 # 公共工具、实体类、注解
│   ├── netbar-framework/              # Security / JWT / AOP 切面
│   ├── netbar-module-system/          # 系统业务（用户/角色/菜单）
│   └── netbar-netcafe/                # 🔥 网咖业务核心模块
├── src/                               # 前端 Vue 3 项目
│   ├── api/                           # 接口请求封装
│   ├── views/
│   │   ├── netcafe/                   # 后台管理页面
│   │   └── member/                    # 会员自助端页面
│   ├── layout/                        # 布局组件
│   ├── store/                         # Pinia 状态管理
│   └── router/                        # 路由配置
├── sql/                               # 数据库初始化脚本
└── TECH_STACK.md                      # 完整技术栈清单
```

## 环境准备

| 软件 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 21+ | Java 运行环境 |
| Maven | 3.9+ | 后端构建 |
| MySQL | 8.0+ (推荐 9.7) | 数据库 |
| Redis | 5.0+ | 缓存服务 |
| Node.js | v24+ | 前端运行环境 |
| pnpm / npm | — | 包管理器（推荐 pnpm） |

## 后端启动

### 1. 初始化数据库

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS `ry-vue` DEFAULT CHARACTER SET utf8mb4;

-- 导入初始化脚本
-- 执行项目 sql/ 目录下的 SQL 脚本
```

### 2. 修改配置文件

复制模板并填写本地环境信息：

```bash
cd netbar-backend/netbar-admin/src/main/resources/

# 复制模板（application.yml 已内置默认配置，一般无需改动）
# 如需自定义环境，创建 application-dev.yml 写入本地配置
```

关键配置项（`application.yml`）：

```yaml
spring:
  datasource:
    druid:
      master:
        url: jdbc:mysql://localhost:3306/ry-vue
        username: root
        password: 123456
  data:
    redis:
      host: localhost
      port: 6379
```

> ⚠️ `application-dev.yml` / `application-prod.yml` 已在 `.gitignore` 中忽略，请勿将含真实密码的配置文件提交到仓库。

### 3. 启动 Redis

```bash
redis-server
```

### 4. 编译并启动

```bash
cd netbar-backend

# 编译（跳过测试）
mvn clean package -DskipTests

# 启动
java -jar netbar-admin/target/netbar-admin.jar
```

启动成功后可访问：

- API 文档：http://localhost:8080/swagger-ui.html
- Druid 监控：http://localhost:8080/druid（账号 `netbar` / 密码 `123456`）

## 前端启动

### 1. 安装依赖

```bash
cd NetBarSystem

# npm
npm install

# 或 pnpm
pnpm install
```

### 2. 配置环境变量

复制 `.env.example` 为 `.env.development`（已加入 `.gitignore`）：

```bash
# .env.development
VITE_APP_TITLE = 网咖综合管理系统
VITE_APP_ENV = 'development'
VITE_APP_BASE_API = '/dev-api'
```

### 3. 启动开发服务器

```bash
npm run dev
```

默认访问地址：

| 入口 | URL | 账号 | 密码 |
|------|------|------|------|
| 后台管理 | http://localhost:8088 | admin | admin123 |
| 会员端 | http://localhost:8088/member | M001 | 011234 |

### 4. 生产构建

```bash
npm run build:prod
```

构建产物输出至 `dist/` 目录。

## 注意事项

1. **配置文件安全**：`application-dev.yml`、`application-prod.yml`、`.env.development`、`.env.production` 等含本地敏感信息的文件已加入 `.gitignore`，请自行创建。可参考 `application.yml` 中的默认配置。
2. **Redis 依赖**：后端启动强依赖 Redis，请确保 Redis 服务已启动后再运行后端。
3. **数据库初始化**：首次启动前需导入 `sql/` 目录下的初始化脚本，创建必要的数据表与初始数据。
4. **端口占用**：后端默认 `8080`，前端默认 `8088`，请确保端口未被占用。
5. **自动导入**：前端使用 `unplugin-auto-import`，Vue 和 Element Plus API 无需手动 `import`。
6. **Windows 用户**：目录路径中的反斜杠请统一使用 `/` 或双反斜杠 `\\`。

## 许可证

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

本项目基于 [RuoYi-Vue3](https://gitee.com/y_project/RuoYi-Vue) 开发，遵循 MIT 开源协议。

- 原若依框架采用 MIT 协议，版权归若依作者所有。
- 本项目新增的网咖业务模块 (`netbar-netcafe`) 同样以 MIT 协议开放。

## 联系方式

- **Issues**：如有问题或建议，请在 [GitHub Issues]() 提交。
- **PR**：欢迎提交 Pull Request，请先阅读贡献指南。

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给一个 Star！**

</div>
"# netbar-management-system" 
