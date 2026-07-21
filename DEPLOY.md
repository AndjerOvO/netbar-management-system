# 网咖综合管理系统 - 本地部署指南

> 适用环境：Windows 10+ / macOS / Linux  
> 目标：从零开始搭建开发环境，成功启动前后端并访问系统。

---

## 1. 前提条件

### 1.1 必需软件

| 软件 | 最低版本 | 推荐版本 | 下载地址 |
|------|----------|----------|----------|
| JDK | 17 | **21** | [Adoptium](https://adoptium.net/) / [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) |
| Maven | 3.6 | **3.9+** | [Apache Maven](https://maven.apache.org/download.cgi) |
| MySQL | 8.0 | **9.7** | [MySQL Community](https://dev.mysql.com/downloads/mysql/) |
| Redis | 5.0 | **5.0+** | [Redis for Windows](https://github.com/tporadowski/redis/releases) 或 Linux `apt install redis` |
| Node.js | 18 | **24** | [Node.js](https://nodejs.org/) |

### 1.2 环境验证

安装完成后，在终端中逐一验证：

```bash
java -version        # 应输出 21.x
mvn -version         # 应输出 3.9.x
mysql --version      # 应输出 9.7.x
redis-cli ping       # 应输出 PONG
node -v              # 应输出 v24.x
npm -v               # 应输出 10.x+
```

### 1.3 工具推荐

| 工具 | 用途 |
|------|------|
| [IntelliJ IDEA](https://www.jetbrains.com/idea/) | 后端 Java 开发 |
| [VS Code](https://code.visualstudio.com/) | 前端 Vue 开发 |
| [Navicat](https://www.navicat.com/) / [DBeaver](https://dbeaver.io/) | 数据库管理 |
| [Another Redis Desktop Manager](https://github.com/qishibo/AnotherRedisDesktopManager) | Redis 可视化 |

---

## 2. 克隆项目

```bash
git clone https://github.com/YOUR_USERNAME/netbar-system.git
cd netbar-system
```

目录结构概览：

```
netbar-system/
├── netbar-backend/          # 后端 Maven 父工程
│   ├── netbar-admin/        # 启动模块
│   ├── netbar-common/       # 公共模块
│   ├── netbar-framework/    # 框架模块
│   ├── netbar-module-system/ # 系统业务模块
│   ├── netbar-netcafe/      # 网咖业务模块
│   └── sql/                 # 数据库脚本
├── src/                     # 前端 Vue 3 源码
├── package.json             # 前端依赖
└── vite.config.js           # Vite 配置
```

---

## 3. 数据库初始化

### 3.1 创建数据库

使用 MySQL 客户端连接并执行：

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS `ry-vue`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

-- 创建用户（可选，生产环境建议创建专用用户）
-- CREATE USER 'netbar'@'localhost' IDENTIFIED BY 'YOUR_PASSWORD';
-- GRANT ALL PRIVILEGES ON `ry-vue`.* TO 'netbar'@'localhost';
-- FLUSH PRIVILEGES;
```

### 3.2 导入初始化脚本

```bash
# Windows（CMD / PowerShell）
"C:\Program Files\MySQL\MySQL Server 9.7\bin\mysql.exe" -u root -p ry-vue < netbar-backend\sql\ry_20240601.sql

# macOS / Linux
mysql -u root -p ry-vue < netbar-backend/sql/ry_20240601.sql
```

> 💡 SQL 文件位于 `netbar-backend/sql/` 目录。如果包含多个脚本，请按文件名顺序依次执行（先建表、再导数据）。

### 3.3 验证数据库

```sql
-- 检查表是否创建成功
USE `ry-vue`;
SHOW TABLES;
```

预期应看到 `sys_user`、`sys_role`、`sys_menu`、`netcafe_member`、`netcafe_machine` 等表。

---

## 4. 后端启动

### 4.1 配置环境变量（可选）

确保 JDK 和 Maven 已正确配置：

```bash
# Windows（设置环境变量）
set JAVA_HOME=C:\Program Files\Java\jdk-21
set MAVEN_HOME=C:\apache-maven-3.9

# macOS / Linux
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
export MAVEN_HOME=/opt/apache-maven-3.9
```

### 4.2 配置数据库与 Redis

进入资源配置目录：

```bash
cd netbar-backend/netbar-admin/src/main/resources/
```

#### 方式一：直接修改 application.yml（开发环境推荐）

编辑 `application.yml`，找到以下配置块并修改：

```yaml
# 数据库（application-druid.yml 中）
spring:
  datasource:
    druid:
      master:
        url: jdbc:mysql://localhost:3306/ry-vue?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=Asia/Shanghai
        username: root
        password: YOUR_DB_PASSWORD       # ← 改为你的数据库密码

  # Redis（application.yml 中）
  data:
    redis:
      host: localhost
      password: YOUR_REDIS_PASSWORD     # ← 改为你的 Redis 密码，无密码则留空
```

#### 方式二：使用模板文件

```bash
# 复制示例配置
cp application-example.yml application-dev.yml

# 编辑 application-dev.yml，填入真实配置
# 启动时指定 profile：
# java -jar netbar-admin.jar --spring.profiles.active=dev
```

> ⚠️ `application-dev.yml` 已加入 `.gitignore`，不会提交到 Git 仓库。

#### 配置阿里云 Maven 镜像（加速依赖下载）

编辑 `~/.m2/settings.xml`（或 `%USERPROFILE%\.m2\settings.xml`）：

```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <name>Aliyun Maven</name>
    <mirrorOf>central</mirrorOf>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

### 4.3 启动 Redis

```bash
# Windows
redis-server.exe

# macOS
redis-server

# Linux
sudo systemctl start redis
```

验证：

```bash
redis-cli ping
# 应输出：PONG
```

### 4.4 编译项目

```bash
# 回到后端根目录
cd netbar-backend

# 首次编译（下载依赖）
mvn clean install -DskipTests

# 后续增量编译
mvn compile -DskipTests
```

### 4.5 启动应用

#### IDE 启动（推荐 IntelliJ IDEA）

1. 打开 `netbar-backend/` 目录作为 Maven 项目
2. 等待 Maven 依赖下载完成
3. 找到 `netbar-admin/src/main/java/com/ztw/netbar/RuoYiApplication.java`
4. 右键 → **Run 'RuoYiApplication'**

#### 命令行启动

```bash
cd netbar-backend
mvn package -DskipTests
java -jar netbar-admin/target/netbar-admin.jar
```

### 4.6 验证启动成功

启动日志中出现以下内容表示成功：

```
Started RuoYiApplication in xx.xxx seconds
(♥◠‿◠)ﾉﾞ  网咖管理系统启动成功   ლ(´ڡ`ლ)ﾞ
```

访问以下地址验证：

| 地址 | 说明 |
|------|------|
| http://localhost:8080/swagger-ui.html | API 文档 |
| http://localhost:8080/druid | 数据库监控（账号见配置） |

---

## 5. 前端启动

### 5.1 安装依赖

```bash
# 回到项目根目录
cd netbar-system

# npm
npm install

# 或 pnpm（推荐，速度更快）
npm install -g pnpm
pnpm install
```

### 5.2 配置环境变量

```bash
# 复制示例文件
cp .env.example .env.development
```

编辑 `.env.development`：

```env
# 页面标题
VITE_APP_TITLE = 网咖综合管理系统

# 开发环境
VITE_APP_ENV = 'development'

# 后端 API 地址（Vite 代理转发到 http://localhost:8080）
VITE_APP_BASE_API = '/dev-api'
```

> ⚠️ `.env.development` 已加入 `.gitignore`，不会提交到 Git 仓库。

### 5.3 启动开发服务器

```bash
npm run dev
```

首次启动后浏览器会自动打开，如未打开手动访问：

```
http://localhost:8088
```

### 5.4 构建生产版本

```bash
npm run build:prod
```

构建产物输出至 `dist/`，部署到 Nginx 等 Web 服务器即可。

---

## 6. 登录验证

| 入口 | URL | 账号 | 密码 |
|------|------|------|------|
| 后台管理 | http://localhost:8088 | admin | admin123 |
| 会员端 | http://localhost:8088/member | M001 | 011234 |

---

## 7. 常见问题

### 7.1 端口被占用

```bash
# Windows — 查找占用 8080 端口的进程
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# macOS / Linux
lsof -i :8080
kill -9 <PID>
```

### 7.2 MySQL 时区错误

错误信息：`The server time zone value '...' is unrecognized`

**解决方案**：在数据库连接 URL 末尾添加时区参数：

```
jdbc:mysql://localhost:3306/ry-vue?...&serverTimezone=Asia/Shanghai
```

或执行 SQL 设置全局时区：

```sql
SET GLOBAL time_zone = '+8:00';
```

### 7.3 Redis 连接失败

错误信息：`Unable to connect to localhost:6379`

**解决方案**：

1. 确认 Redis 已启动：`redis-cli ping`
2. 如果 Redis 设置了密码，在 `application.yml` 中配置 `spring.data.redis.password`
3. Linux 下检查保护模式：编辑 `redis.conf`，设置 `protected-mode no`（仅开发环境）
4. Windows 下重新启动 Redis 服务

### 7.4 Maven 依赖下载缓慢

在 `~/.m2/settings.xml` 中配置阿里云镜像（见 [4.2 节](#42-配置环境变量可选)）。

### 7.5 前端启动报错 `Cannot find module 'xxx'`

```bash
# 清除缓存重新安装
rm -rf node_modules package-lock.json
npm install
```

### 7.6 登录报错 "验证码错误"

- 确保 Redis 正常运行（验证码存储在 Redis 中）
- 检查 Redis 连接配置是否正确

### 7.7 菜单不显示或 403 错误

可能是缓存问题，重启后端后使用**无痕模式**重新登录。

---

## 8. 一键启动脚本（Windows）

项目根目录下提供了 `start.bat`：

```batch
@echo off
title NetBar

echo NetBar System Starting...

echo Step 1: Redis
start "Redis" C:\Users\17591\redis\redis-server.exe C:\Users\17591\redis\redis.windows.conf

echo Step 2: Backend
start "Backend" java -jar C:\Users\17591\Desktop\NetBarSystem\netbar-backend\netbar-admin\target\netbar-admin.jar

echo Step 3: Frontend
cd /d C:\Users\17591\Desktop\NetBarSystem
start "Frontend" cmd /c npm run dev

echo Done.
echo Admin: http://localhost:8088
echo Member: http://localhost:8088/member
pause
```

> ⚠️ 请根据你的实际路径修改脚本中的目录地址。

---

<div align="center">

**🎉 部署完成！如有问题请提交 [GitHub Issues]()。**

</div>
