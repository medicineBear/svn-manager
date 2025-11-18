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
- SQLite 3.40+ (开发环境)
- PostgreSQL 14+ (生产环境)
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

1. 进入后端目录：
```bash
cd backend
```

2. 安装依赖（Maven 会自动下载）：
```bash
mvn install
```

3. 启动应用：
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

开发环境默认使用 SQLite 数据库，数据库文件位于 `backend/data/svnmanager.db`。

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
