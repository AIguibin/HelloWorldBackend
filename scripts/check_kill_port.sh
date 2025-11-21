#!/bin/bash

# 检查是否提供了端口参数
if [ $# -ne 1 ]; then
    echo "使用方法: $0 <端口号>"
    echo "示例: $0 8080"
    exit 1
fi

PORT="$1"

# 验证端口号是否有效
if ! [[ "$PORT" =~ ^[0-9]+$ ]] || [ "$PORT" -lt 1 ] || [ "$PORT" -gt 65535 ]; then
    echo "错误: 端口号必须是1-65535之间的数字"
    exit 1
fi

echo "正在检查端口 $PORT 的占用情况..."

# 查找占用端口的进程
PID=$(lsof -ti:$PORT 2>/dev/null)

if [ -z "$PID" ]; then
    # 如果lsof不可用，尝试使用netstat
    PID=$(netstat -tulpn 2>/dev/null | grep ":$PORT " | awk '{print $7}' | cut -d'/' -f1)
    
    if [ -z "$PID" ]; then
        echo "端口 $PORT 未被任何进程占用"
        exit 0
    fi
fi

# 显示占用端口的进程信息
echo "以下进程正在使用端口 $PORT:"
if command -v lsof &> /dev/null; then
    lsof -i:$PORT
else
    netstat -tulpn | grep ":$PORT "
fi

echo ""
echo "进程PID: $PID"

# 确认是否杀死进程
read -p "是否要杀死这些进程? (y/N): " confirm
if [[ "$confirm" =~ ^[Yy]$ ]]; then
    # 杀死进程（支持多个PID，用空格分隔的情况）
    for pid in $PID; do
        if kill -9 "$pid" 2>/dev/null; then
            echo "已杀死进程 $pid"
        else
            echo "错误: 无法杀死进程 $pid (可能权限不足)"
        fi
    done
    
    # 再次检查端口是否还被占用
    sleep 1
    if lsof -ti:$PORT &>/dev/null || netstat -tulpn 2>/dev/null | grep -q ":$PORT "; then
        echo "警告: 端口 $PORT 可能仍被占用"
    else
        echo "端口 $PORT 现已释放"
    fi
else
    echo "操作已取消"
fi