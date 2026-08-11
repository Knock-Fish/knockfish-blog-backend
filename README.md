# knockfish-blog-backend

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-green)
![MyBatis](https://img.shields.io/badge/MyBatis-3.0.5-red)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Redis](https://img.shields.io/badge/Redis-Lettuce-red)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36)
![License](https://img.shields.io/badge/license-MIT-blue)

## 相关项目

| 项目 | 说明 |
| --- | --- |
| **knockfish-blog-backend** | **Spring Boot 后端服务（当前仓库）** |
| [knockfish-blog-frontend](https://github.com/Knock-Fish/knockfish-blog-frontend) | Vue 3 前台展示端 |
| [knockfish-blog-admin](https://github.com/Knock-Fish/knockfish-blog-admin) | Vue 3 后台管理端 |
| [knockfish-blog-agent](https://github.com/Knock-Fish/knockfish-blog-agent) | FastAPI + LangGraph AI Agent |

KnockFish 博客系统的后端服务，基于 Spring Boot 构建，为前台展示、后台管理及 AI Agent 提供统一的数据与能力支撑。

## 技术栈

| 分类 | 选型 |
| --- | --- |
| 框架 | Spring Boot 3.5.13 / Java 17 |
| 持久层 | MyBatis 3.0.5 + PageHelper 1.4.6 |
| 数据库 | MySQL（HikariCP 连接池） |
| 缓存 | Redis（Lettuce） + Caffeine 本地缓存 |
| 安全 | Spring Security + JWT（auth0 java-jwt 4.4.0）+ BCrypt |
| 对象存储 | AWS S3 SDK（兼容 Cloudflare R2） |
| 文档 | SpringDoc OpenAPI（Swagger UI） |
| 工具库 | Hutool、MapStruct、Lombok、Jsoup、webp-imageio |
| 定时任务 | SpringBoot 内置 @Scheduled |
| 日志 | Logback（按级别 / 按天分文件输出至 logs/） |

## 功能模块

- 认证授权：登录签发 JWT、基于角色的权限校验（@RequiresPermission 注解 + AOP 切面）
- 文章管理：草稿机制、标签绑定、归档、分页查询
- 分类 / 标签 / 笔记 / 友链 / 站点导航：完整的 CRUD
- 代码片段：按代码分类归档
- 文件存储：头像、封面、文章图片上传至 Cloudflare R2，WebP 格式自动处理
- 文件引用：file_reference 表统一管理 R2 文件与业务对象的绑定关系
- 文件清理：@Scheduled 定时任务（默认每天凌晨 2 点）扫描并清理孤儿文件
- 仪表盘：文章趋势、最新活动、统计概览
- Agent 接口：AgentController（@PublicApi 开放）提供 14 个只读端点供 AI Agent 调用
- 用户 / 角色 / 权限：RBAC 模型，支持权限树

## 目录结构

```
src/main/java/com/knockfish/
├── annotation/         # 自定义注解（@PublicApi、@RequiresPermission、@Log、密码校验）
├── aspect/             # AOP 切面（操作日志）
├── common/             # 通用返回结果 Result
├── config/             # SecurityConfig、R2FileConfig、UploadConfig
├── controller/         # 控制层（16 个 Controller）
├── convert/            # MapStruct 实体/DTO/VO 转换器
├── dto/                # 请求 DTO（按业务模块分包）
├── entity/             # 数据库实体
├── enums/              # 状态枚举
├── exception/          # 全局异常处理
├── repository/         # MyBatis Mapper 接口
├── scheduler/          # 定时任务（FileCleanupScheduler）
├── security/           # JWT 过滤器、用户DetailsService、权限过滤器
├── service/            # Service 接口 + impl 实现
├── utils/              # 工具类（JwtUtil、FileUtil、ImageWebpProcessor 等）
└── vo/                 # 响应 VO（按业务模块分包）

src/main/resources/
├── repository/         # MyBatis XML 映射文件
├── sql/                # 数据库优化索引脚本
├── application.yml     # 应用配置
└── logback-spring.xml  # 日志配置
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

### 配置

修改 `src/main/resources/application.yml`，重点关注以下配置项：

- `spring.datasource`：MySQL 连接信息
- `spring.data.redis`：Redis 连接信息
- `jwt.secret` / `jwt.expiration`：JWT 密钥与过期时间
- `cloud.aws.s3`：Cloudflare R2 的 endpoint、bucket、access-key、secret-key、cdn-domain
- `upload.dir`：本地临时上传目录（avatar/cover/article/note/background）

### 数据库初始化

根目录下的 `knockfish_blog.sql` 为完整建库脚本，导入至 MySQL 后即可使用。

### 运行

```sh
./mvnw spring-boot:run
```

或使用 IDE 直接运行 `KnockfishBlogBackendApplication` 主类（已添加 @EnableScheduling）。

启动成功后：

- 服务端口：8081
- Swagger UI：http://localhost:8081/swagger-ui.html
- API 文档：http://localhost:8081/v3/api-docs

## 接口约定

- 统一返回结构：`Result<T>`（code / message / data）
- 需登录接口：请求头携带 `Authorization: Bearer <jwt>`
- 需权限接口：Controller 方法或类上标注 `@RequiresPermission("权限编码")`
- 公开接口：Controller 类上标注 `@PublicApi`
- Agent 专用接口：`/api/agent/**` 前缀，均为只读，由 AgentService 提供，方法统一添加 `@Transactional(readOnly = true)`

## 开发约定

- 新增接口时若 Service 层不存在对应方法需同步新增，并编写相应 DTO 与 VO
- 在原有架构上开发，不修改已写好的代码
- MyBatis XML 中使用 `<`、`>` 必须转义
- MyBatis XML 的 resultType 不可引用内部类，需使用独立类
- 文件清理遵循「先查询 → 删物理文件 → 删数据库记录」流程，删除前打印完整日志
- 文章 / 笔记删除以 file_reference 表为真实文件来源，content 兜底
