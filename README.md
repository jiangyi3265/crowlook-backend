# Crowlook Backend

Crowlook 项目的后端基础服务，为运营管理端提供认证、权限、系统配置与监控等 API 能力。

## 项目简介

本仓库基于 RuoYi-Vue 3.9.1 的 Java 后端整理而成，已将旧版内置 Vue 2 前端拆出，并停用定时任务与代码生成模块的默认装配，保留登录鉴权、用户/角色/菜单、部门、岗位、字典、参数、通知、日志、服务监控和缓存监控等后台基础能力。当前 SQL 是面向二次开发的极简空壳数据集，可作为 Crowlook 业务后台的服务端基础。

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

准备 JDK 8、Maven、MySQL 和 Redis。先创建名为 `ha` 的数据库，并导入 [sql/ry_20250522.sql](sql/ry_20250522.sql)。如需使用其他库名，可通过 `DB_URL` 覆盖默认连接地址。

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

## 项目结构

```text
ruoyi-admin/       Spring Boot 启动模块、控制器与运行配置
ruoyi-framework/   安全认证、Web、数据权限和基础框架配置
ruoyi-system/      用户、角色、菜单、部门、字典等系统业务
ruoyi-common/      通用模型、注解、工具和公共依赖
ruoyi-generator/   代码生成模块源码（当前未装配）
ruoyi-quartz/      定时任务模块源码（当前未装配）
sql/               极简初始化数据库脚本
```

## 简历描述示例

参与 Crowlook 管理系统后端基础架构建设，基于 Spring Boot、Spring Security、MyBatis、MySQL 与 Redis 实现统一认证、权限控制及系统管理 API，并完成前后端仓库拆分与敏感配置环境变量化。

