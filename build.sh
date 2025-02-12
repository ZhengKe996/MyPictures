#!/bin/bash

# 输出颜色设置
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 打印带颜色的信息函数
print_message() {
    echo -e "${2}${1}${NC}"
}

# 检查上一个命令是否成功
check_status() {
    if [ $? -eq 0 ]; then
        print_message "$1 成功" "${GREEN}"
    else
        print_message "$1 失败" "${RED}"
        exit 1
    fi
}

# 项目根目录
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FRONTEND_DIR="${PROJECT_ROOT}/snake-fronted"
BACKEND_DIR="${PROJECT_ROOT}/snake-backed"

# 1. 构建前端项目
print_message "开始构建前端项目..." "${YELLOW}"
cd "${FRONTEND_DIR}" || exit 1

# 检查 pnpm 是否安装
if ! command -v pnpm &> /dev/null; then
    print_message "pnpm 未安装，正在安装..." "${YELLOW}"
    npm install -g pnpm
    check_status "pnpm 安装"
fi

# 安装依赖
print_message "安装前端依赖..." "${YELLOW}"
pnpm install
check_status "前端依赖安装"

# 构建项目
print_message "构建前端项目..." "${YELLOW}"
pnpm build
check_status "前端项目构建"

# # 2. 构建后端项目(弃用；使用Lombok后, 使用Idea进行打包)
# print_message "开始构建后端项目..." "${YELLOW}"
# cd "${BACKEND_DIR}" || exit 1

# # 检查 Maven 是否安装
# if ! command -v mvn &> /dev/null; then
#     print_message "Maven 未安装，请先安装 Maven" "${RED}"
#     exit 1
# fi

# # 使用 Maven 打包项目
# print_message "打包 Spring Boot 项目..." "${YELLOW}"
# # 先编译 Lombok
# mvn clean compile
# check_status "后端项目编译"

# # 再进行打包
# mvn package -DskipTests
# check_status "后端项目打包"

# # 运行 Spring Boot 项目
# print_message "启动 Spring Boot 项目..." "${YELLOW}"
# java -jar target/*.jar