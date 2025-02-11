# Snake Picture - 现代云图片管理平台

## 项目概述

一个基于 Spring Boot 构建的综合图片管理系统，提供图片上传、管理和 AI 驱动的图像处理功能。

## 技术栈

- **后端框架**: Spring Boot 2.7.6
- **Java 版本**: Java 11
- **数据库访问**: MyBatis-Plus
- **云存储**: 腾讯云 COS
- **AI 集成**: 阿里云 AI API
- **图像处理**: Pexels API 集成

## 核心功能

1. **用户管理**

   - 用户注册与认证
   - 基于角色的访问控制
   - 会话管理

2. **图片管理**

   - 文件上传支持
   - 基于 URL 的图片导入
   - 批量图片操作
   - 图片分类和标签

3. **空间管理**

   - 空间分配和监控
   - 使用情况分析
   - 基于类别的组织
   - 基于标签的组织

4. **AI 功能**

   - AI 驱动的图像处理
   - 图像扩展任务创建和管理
   - 图像分析能力

5. **数据分析**
   - 空间使用分析
   - 用户活动跟踪
   - 类别和标签统计
   - 排名分析

## 项目结构

```src/main/java/fun/timu/init/
├── Application.java
├── annotation/
├── aop/
├── api/
├── common/
├── config/
├── constant/
├── controller/
├── exception/
├── manager/
├── mapper/
├── model/
└── service/
```
