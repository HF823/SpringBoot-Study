# TaskHub 任务管理系统

一个基于 Spring Boot 4 的多用户任务管理系统后端，支持用户注册登录、项目管理、任务管理、状态流转，并通过 ownerId 实现严格的数据隔离。

## 技术栈

| 类别 | 技术 |
|---|---|
| 语言 | Java 17 |
| 框架 | Spring Boot 4.1.1 |
| 安全 | Spring Security 7 + JWT (jjwt 0.12.6) |
| 持久层 | Spring Data JPA + Hibernate 7 |
| 数据库 | MySQL 8 |
| 缓存 | Redis 7 |
| 接口文档 | SpringDoc OpenAPI 3 (Swagger UI) |
| 校验 | Jakarta Validation |
| 测试 | JUnit 5 + Mockito + MockMvc + H2 |
| 构建 | Maven |

## 功能列表

- 用户注册、登录（JWT 无状态鉴权）
- 密码使用 BCrypt 加密存储
- 项目 CRUD，按用户隔离
- 任务 CRUD，支持状态流转（TODO → DOING → DONE）
- 任务列表支持分页、状态过滤、关键词搜索
- 两级数据隔离：Task → Project → User
- 统一响应格式 `{ code, message, data }`
- 全局异常处理，业务异常与系统异常分级
- 参数校验（`@NotBlank`、`@Size`、`@Min`、`@Max`、`@Future`）
- Redis 缓存热点数据（`@Cacheable / @CachePut / @CacheEvict`）
- 定时任务统计（`@Scheduled`）
- AOP 请求日志记录

## 接口文档

启动项目后访问：

```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON：

```
http://localhost:8080/v3/api-docs
```

## 快速开始

### 1. 环境准备

- JDK 17+
- Maven 3.8+
- MySQL 8
- Redis 7

### 2. 建库

```sql
CREATE DATABASE taskhub
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

### 3. 配置

编辑 `src/main/resources/application.properties`：

```properties
# 数据源
spring.datasource.url=jdbc:mysql://localhost:3306/taskhub?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=你的密码

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# JWT
jwt.secret=change-this-to-a-long-random-secret-at-least-32-bytes
jwt.expiration-minutes=60
```

### 4. 启动 Redis

```bash
redis-server.exe
```

### 5. 运行项目

方式一：IDEA 中运行 `FourthStudyApplication`

方式二：打包后运行

```bash
mvn clean package -DskipTests
java -jar target/Second-0.0.1-SNAPSHOT.jar
```

### 6. 访问

- Swagger UI：http://localhost:8080/swagger-ui.html
- 默认端口：8080

## 使用示例

### 注册

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "alice",
  "password": "123456"
}
```

### 登录

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "alice",
  "password": "123456"
}
```

返回：

```json
{
   "code": 0,
   "message": "成功",
   "data": {
      "token": "eyJhbGciOiJIUzI1NiJ9..."
   }
}
```

### 带 token 访问受保护接口

```http
GET /api/projects
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## 数据库设计

### 表结构

| 表 | 说明 |
|---|---|
| `users` | 用户表 |
| `project` | 项目表，`owner_id` 关联 `users.id` |
| `task` | 任务表，`project_id` 关联 `project.id` |

### 关系图

```
User 1 ──── N Project 1 ──── N Task
```

### 关键字段

**users**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| username | VARCHAR(50) | 唯一 |
| password | VARCHAR(100) | BCrypt 哈希 |
| role | VARCHAR(20) | USER / ADMIN |

**project**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| name | VARCHAR(100) | 项目名 |
| description | VARCHAR(500) | 描述 |
| owner_id | BIGINT | 所属用户，建索引 |
| created_at | DATETIME | 创建时间 |

**task**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| project_id | BIGINT | 所属项目，建索引 |
| title | VARCHAR(200) | 标题 |
| description | VARCHAR(1000) | 描述 |
| status | VARCHAR(20) | TODO / DOING / DONE |
| priority | INT | 1~5，默认 3 |
| due_date | DATETIME | 截止时间 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

## 项目结构

```
com.example.demo
├── FourthStudyApplication.java     启动类
├── common/                         公共组件
│   ├── ApiResponse.java            统一响应体
│   ├── ErrorCode.java              错误码枚举
│   ├── BusinessException.java      业务异常
│   └── GlobalExceptionHandler.java 全局异常处理
├── config/                         配置
│   ├── LogAspect.java              AOP 请求日志
│   └── CacheConfig.java            缓存配置
├── security/                       安全模块
│   ├── SecurityConfig.java         Spring Security 配置
│   ├── JwtService.java             JWT 签发与解析
│   ├── JwtAuthFilter.java          JWT 认证过滤器
│   ├── JwtProperties.java          JWT 配置绑定
│   └── SecurityExceptionHandler.java  401/403 统一响应
├── user/                           用户模块
│   ├── User.java
│   ├── UserRepository.java
│   ├── UserService.java
│   ├── AuthController.java
│   └── RegisterRequest.java
├── project/                        项目模块
│   ├── Project.java
│   ├── ProjectRepository.java
│   ├── ProjectService.java
│   ├── ProjectController.java
│   ├── CreateProjectRequest.java
│   └── ProjectResponse.java
├── task/                           任务模块
│   ├── Task.java
│   ├── TaskStatus.java
│   ├── TaskRepository.java
│   ├── TaskService.java
│   ├── TaskController.java
│   ├── CreateTaskRequest.java
│   ├── UpdateTaskRequest.java
│   └── TaskResponse.java
└── job/                            定时任务
    └── TodoStatsJob.java
```

## 数据隔离策略

系统采用**基于 ownerId 的数据隔离**：

1. 每个 Project 记录 `owner_id`，标识归属用户
2. 每个 Task 通过 `project_id` 间接归属用户
3. 所有查询接口均带 ownerId 条件：
   - 项目列表：`findByOwnerId(ownerId, pageable)`
   - 项目详情：`findByIdAndOwnerId(id, ownerId)`
   - 任务列表：先校验 `ownProject(projectId)`，再查任务
   - 任务详情：`ownTask(taskId)`，沿着 Task → Project → User 链式校验

4. **跨用户访问统一返回 404，而不是 403**，防止攻击者通过状态码探测资源是否存在

### 隔离验证

用两个账号测试，互相访问对方资源，应全部返回 404：

```http
GET /api/projects/{别人的项目id}
Authorization: Bearer <你的token>
```

返回：

```json
{
  "code": 404,
  "message": "项目不存在",
  "data": null
}
```

## 核心设计说明

### 为什么不使用 `@ManyToOne`

实体中使用 `Long ownerId` 和 `Long projectId` 外键字段，而不是 JPA 关联注解，原因：

- 查询简单可控，避免 N+1 问题
- 不依赖懒加载，避免 LazyInitializationException
- 序列化容易，不产生循环引用
- 便于跨模块调用，避免实体间强耦合

### 为什么返回 DTO 而不是实体

- 明确 API 契约，避免暴露内部字段
- 实体变更不影响接口
- 便于单元测试

### 统一响应格式

所有接口返回：

```json
{
   "code": 0,
   "message": "成功",
   "data": { ... }
}
```

错误码：

| code | 含义 |
|---|---|
| 0 | 成功 |
| 400 | 请求参数错误 |
| 404 | 资源不存在 |
| 409 | 资源冲突 |
| 500 | 服务器内部错误 |

## 后续规划

- [ ] 标签模块（Task 与 Tag 多对多）
- [ ] 任务评论功能
- [ ] 文件附件上传
- [ ] Docker Compose 一键部署
- [ ] 接口限流
- [ ] 链路追踪

## 作者

- 姓名：你的名字
- GitHub：https://github.com/your-username
- 邮箱：your-email@example.com

## License

MIT