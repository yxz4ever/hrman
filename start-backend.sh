#!/bin/bash

# 人力资源管理系统后端启动脚本

echo "正在启动人力资源管理系统后端服务..."
echo ""

# 进入后端目录
cd "$(dirname "$0")/backend"

# 设置UTF-8编码并启动
export JAVA_OPTS="-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8"
mvn spring-boot:run

echo "后端服务已停止"