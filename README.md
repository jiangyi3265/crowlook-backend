# Crowlook Backend

Crowlook 项目的统一后端服务，为管理后台和用户端提供内容、评论、认证与权限 API。

## 项目简介

本仓库基于 RuoYi-Vue 3.9.1 的 Java 后端整理而成，已将旧版内置 Vue 2 前端拆出，并实现 Crowlook 内容中台。管理端可维护作品、分类、首页/发现页/品牌页模块与评论审核；用户端通过匿名公开 API 获取已发布内容、提交待审核评论。系统同时保留登录鉴权、用户/角色/菜单、字典、日志、服务监控和缓存监控等后台基础能力。

后端配置中的数据库密码、Redis 密码、JWT 密钥和 Druid 控制台密码均通过环境变量注入，不在仓库中保存真实凭据。

## 技术栈

- Java 8、Maven 多模块工程
- Spring Boot 2.5.15、Spring Security、JWT
- MyBatis、PageHelper
- MySQL、Druid 连接池
- Redis
- Springfox / Swagger 3
- Logback、Fastjson2、Apache POI

## 关联仓库

| 项目 | 说明 | GitHub |
| --- | --- | --- |
| crowlook-backend | 后端服务 | [crowlook-backend](https://github.com/jiangyi3265/crowlook-backend) |
| crowlook-admin | 管理后台 | [crowlook-admin](https://github.com/jiangyi3265/crowlook-admin) |
| crowlook-app | 用户端 | [crowlook-app](https://github.com/jiangyi3265/crowlook-app) |

## 快速启动

准备 JDK 8、Maven、MySQL 和 Redis。先创建名为 `ha` 的数据库，并依次导入 [sql/ry_20250522.sql](sql/ry_20250522.sql) 与 [sql/crowlook_content.sql](sql/crowlook_content.sql)。第二个脚本会创建内容业务表、管理菜单与用户端分类。如需使用其他库名，可通过 `DB_URL` 覆盖默认连接地址。

启动前至少设置数据库密码与 JWT 密钥；生产环境还应设置 Druid 控制台密码，并按需设置 Redis 密码：

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your-database-password"
$env:JWT_SECRET="replace-with-a-long-random-secret"
$env:DRUID_ADMIN_PASSWORD="replace-with-a-strong-password"
$env:REDIS_PASSWORD=""
```

构建并启动：

```bash
mvn clean package -DskipTests
java -jar ruoyi-admin/target/ruoyi-admin.jar
```

服务默认监听 `http://localhost:8080`。数据库连接、文件上传目录和 Redis 地址可在 `ruoyi-admin/src/main/resources/` 下的配置中查看或通过部署环境覆盖。

公开内容接口无需登录，主要包括：

- `GET /api/module/page.json`：首页、发现页、品牌页及专题页
- `GET /api/category/list.json`：分类树
- `GET /api/post/list.json`、`GET /api/post/get.json`：作品列表与详情
- `POST /api/post/comment.json`：用户端提交评论，默认进入待审核状态

首次启动后，可在 `crowlook-admin` 的“页面编排”中导入 `crowlook-app/data/snapshot.json`，将现有页面、作品和历史评论迁移到数据库。

## 项目结构

```text
ruoyi-admin/       Spring Boot 启动模块、控制器与运行配置
ruoyi-framework/   安全认证、Web、数据权限和基础框架配置
ruoyi-system/      系统业务以及 Crowlook 作品、分类、页面、评论领域
ruoyi-common/      通用模型、注解、工具和公共依赖
ruoyi-generator/   代码生成模块源码（当前未装配）
ruoyi-quartz/      定时任务模块源码（当前未装配）
sql/               系统初始化与 Crowlook 内容中台脚本
```

## 简历描述示例

参与 Crowlook 三端内容平台后端建设，基于 Spring Boot、Spring Security、MyBatis、MySQL 与 Redis 实现作品/分类/页面编排、评论审核及公开内容 API，打通运营后台与 H5、微信小程序的数据链路。
