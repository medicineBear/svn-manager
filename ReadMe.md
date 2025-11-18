# SVN 出入库 Web 管理应用

## 项目简介

"Svn 出入库" Web 管理应用是一个专业的远程 SVN 文件管理工具，旨在简化 SVN branches 与 trunk 之间的协作工作流。应用提供四个核心文件操作（出库/入库/出库取消/却下），并具有严格的版本控制和基于角色的访问（管理员/用户）功能。

## 技术栈

### 前端
- Vue.js 3.x + TypeScript 5.x
- Pinia - 状态管理
- Element Plus - UI 组件库
- Tailwind CSS 3.x - 样式框架
- Vite 4.x - 构建工具
- Axios - HTTP 客户端
- Vue Router - 路由管理
- pnpm - 包管理器

### 后端
- Java 11
- Spring Boot 2.7.x
- MyBatis 3.5.x
- SVNKit 1.10.x
- PostgreSQL 14+ (开发和生产环境，开发环境通过 Docker 运行)
- Maven - 构建工具

## 项目结构

```
项目根目录/
├── frontend/          # 前端项目
│   ├── src/
│   │   ├── components/  # 可复用组件
│   │   ├── store/       # Pinia 状态管理
│   │   ├── api/         # API 服务封装
│   │   ├── utils/       # 工具函数
│   │   ├── views/       # 页面组件
│   │   └── router/      # 路由配置
│   └── package.json
└── backend/           # 后端项目
    ├── src/
    │   └── main/
    │       └── java/
    │           └── com/
    │               └── isc/
    │                   └── svnmanager/
    │                       ├── controller/  # 表现层
    │                       ├── service/     # 应用层
    │                       ├── dao/        # 数据访问层
    │                       ├── entity/      # 领域层
    │                       ├── config/      # 配置类
    │                       └── util/        # 工具类
    └── pom.xml
```

## 环境要求

- Node.js 16+ (推荐使用 pnpm)
- Java 11
- Maven 3.6+

## 快速开始

### 前端开发

1. 进入前端目录：
```bash
cd frontend
```

2. 安装依赖：
```bash
pnpm install
```

3. 启动开发服务器：
```bash
pnpm dev
```

前端开发服务器将在 `http://localhost:5173` 启动。

### 后端开发

1. **启动数据库服务**（首次运行或需要重置数据库时）：
```bash
# 在项目根目录执行
docker-compose up -d

# 初始化数据库结构（首次运行）
# Windows PowerShell:
Get-Content backend/src/main/resources/schema.sql | docker exec -i svnmanager-postgres psql -U svnmanager -d svnmanager

# Linux/macOS (bash):
# docker exec -i svnmanager-postgres psql -U svnmanager -d svnmanager < backend/src/main/resources/schema.sql
```

2. 进入后端目录：
```bash
cd backend
```

3. 安装依赖（Maven 会自动下载）：
```bash
mvn install
```

4. 启动应用：
```bash
mvn spring-boot:run
```

后端 API 将在 `http://localhost:8080/api` 启动。

### 验证安装

1. 访问前端：打开浏览器访问 `http://localhost:5173`
2. 访问后端健康检查：`http://localhost:8080/api/health`

## 开发环境配置

### 前端环境变量

创建 `frontend/.env` 文件（参考 `frontend/.env.example`）：

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

### 后端环境变量

开发环境使用 PostgreSQL 数据库（通过 Docker Compose 运行）。

**启动数据库服务：**

```bash
# 在项目根目录执行
docker-compose up -d
```

数据库服务将在 `localhost:5432` 启动，默认配置：
- 数据库名：`svnmanager`
- 用户名：`svnmanager`
- 密码：`svnmanager`

可以通过环境变量覆盖默认配置（参考 `env.example` 文件）。

**停止数据库服务：**

```bash
docker-compose down
# 如需删除数据卷，使用：docker-compose down -v
```

**初始化数据库结构：**

数据库启动后，需要执行初始化脚本创建表结构：

**Windows PowerShell:**
```powershell
# 方法1：使用 Get-Content 和管道
Get-Content backend/src/main/resources/schema.sql | docker exec -i svnmanager-postgres psql -U svnmanager -d svnmanager

# 方法2：将文件复制到容器内执行
docker cp backend/src/main/resources/schema.sql svnmanager-postgres:/tmp/schema.sql
docker exec svnmanager-postgres psql -U svnmanager -d svnmanager -f /tmp/schema.sql
```

**Linux/macOS (bash):**
```bash
# 使用输入重定向
docker exec -i svnmanager-postgres psql -U svnmanager -d svnmanager < backend/src/main/resources/schema.sql
```

生产环境使用 PostgreSQL，通过 `application-prod.yml` 配置。

## 构建部署

### 前端构建

```bash
cd frontend
pnpm build
```

构建产物位于 `frontend/dist/` 目录。

### 后端构建

```bash
cd backend
mvn clean package
```

构建产物位于 `backend/target/svnmanager-1.0.0.jar`。

## 许可证

[待定]

## 联系方式

如有问题，请联系开发团队。
