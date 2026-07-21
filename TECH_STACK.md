# 网咖综合管理系统 - 技术栈

## 后端

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 17 (JDK 21) |
| 框架 | Spring Boot | 4.0.6 |
| 安全 | Spring Security + JWT | jjwt 0.9.1 |
| ORM | MyBatis (Spring Boot Starter) | 4.0.1 |
| 分页 | PageHelper | 4.1.0 |
| 数据库 | MySQL | 9.7 |
| 连接池 | Druid (Alibaba) | 1.2.28 |
| 缓存 | Redis (Lettuce) | 5.0.14 |
| 接口文档 | SpringDoc (OpenAPI 3 + Swagger UI) | 3.0.3 |
| 校验 | Hibernate Validator / Jakarta Validation | — |
| JSON | Fastjson2 (Alibaba) | 2.0.62 |
| Excel | Apache POI | 4.1.2 |
| 系统监控 | OSHI | 7.3.0 |
| 模板引擎 | Apache Velocity | 2.3 |
| 验证码 | Kaptcha | 2.3.3 |
| 用户代理解析 | YAUAA | 8.1.1 |

## 前端

| 类别 | 技术 | 版本 |
|------|------|------|
| 框架 | Vue 3 | 3.5.26 |
| 构建工具 | Vite | 6.4.1 |
| 路由 | Vue Router | 4.6.4 |
| 状态管理 | Pinia | 3.0.4 |
| UI 组件库 | Element Plus | 2.13.1 |
| 图标 | @element-plus/icons-vue | 2.3.2 |
| HTTP 请求 | Axios | 1.13.2 |
| 图表 | ECharts | 5.6.0 |
| CSS 预处理器 | Sass (sass-embedded) | 1.97.2 |
| 实用工具 | @vueuse/core | 14.1.0 |
| 富文本编辑器 | @vueup/vue-quill (Quill 2) | 1.2.0 |
| 拖拽 | vuedraggable | 4.1.0 |
| 剪贴板 | clipboard | 2.0.11 |
| 搜索 | Fuse.js | 7.1.0 |
| 加密 | jsencrypt | 3.3.2 |
| 代码美化 | js-beautify | 1.15.4 |
| Cookie | js-cookie | 3.0.5 |
| 进度条 | nprogress | 0.2.0 |
| 文件保存 | file-saver | 2.0.5 |
| 头像裁剪 | vue-cropper | 1.1.1 |
| 自动导入 | unplugin-auto-import | 0.18.6 |
| 压缩 | vite-plugin-compression (gzip) | 0.5.1 |
| SVG 图标 | vite-plugin-svg-icons | 2.0.1 |

## 基础设施

| 类别 | 技术 | 版本 |
|------|------|------|
| 构建 | Maven | 3.9.16 |
| 运行环境 | JDK | 21 |
| Node.js | — | v24 |
| MySQL | — | 9.7 |
| Redis | — | 5.0.14 |

## 项目架构

基于 RuoYi-Vue3 二次开发，Maven 多模块结构：

```
netbar-backend/
├── netbar-admin/            # 启动入口 + REST Controller
├── netbar-common/           # 公共工具、实体、注解
├── netbar-framework/        # Security、JWT、AOP 切面
├── netbar-module-system/    # 系统业务（用户/角色/菜单）
└── netbar-netcafe/          # 网咖业务（会员/设备/计费/商品）
```

## 核心功能

- 会员管理（正式会员 + 临时卡）
- 设备管理（分区 + 计费）
- 上机计费（分段计费 + 心跳保活 + 强制下线）
- 商品销售（购物车 + 余额支付 + 库存防超卖）
- 充值管理
- 数据看板（ECharts 7 天趋势）
- 会员端独立登录（JWT 双 Token 隔离）
- 暗黑模式（View Transitions API 动画切换）
