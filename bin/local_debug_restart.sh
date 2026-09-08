#!/bin/bash

# ============================================
# 本地调试重启脚本 - aiguibin-platform-arch
# 版本: 1.0
# 作者: 自动生成
# ============================================

# 配置参数
PROJECT_NAME="aiguibin-platform-arch"
JAR_NAME="aiguibin-platform-arch.jar"
PORT=8080  # 修改为您的应用端口
MAVEN_SETTINGS="D:\Maven\settings-aiguibin.xml"
MAVEN_REPO="E:\Repository\Local"
PROJECT_ROOT=$(pwd)
LOGS_DIR="${PROJECT_ROOT}/logs"
PID_FILE="${PROJECT_ROOT}/${PROJECT_NAME}.pid"
JVM_OPTIONS="-server -Xms512m -Xmx1024m -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${LOGS_DIR}/heapdump.hprof -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 创建日志目录
create_logs_dir() {
    if [ ! -d "${LOGS_DIR}" ]; then
        mkdir -p "${LOGS_DIR}"
        print_info "创建日志目录: ${LOGS_DIR}"
    fi
}

# 停止当前运行的应用
stop_application() {
    if [ -f "${PID_FILE}" ]; then
        PID=$(cat "${PID_FILE}")
        if ps -p ${PID} > /dev/null 2>&1; then
            print_info "正在停止进程 ${PID}..."
            kill ${PID}
            
            # 等待最多10秒
            for i in {1..10}; do
                if ! ps -p ${PID} > /dev/null 2>&1; then
                    print_success "应用已停止"
                    rm -f "${PID_FILE}"
                    return 0
                fi
                sleep 1
            done
            
            # 强制杀死
            print_warning "正常停止失败，尝试强制停止..."
            kill -9 ${PID} 2>/dev/null
            rm -f "${PID_FILE}"
        else
            print_warning "PID文件存在但进程不存在，清理PID文件"
            rm -f "${PID_FILE}"
        fi
    fi
    
    # 通过端口再次检查
    PORT_PID=$(lsof -ti:${PORT} 2>/dev/null || netstat -ano | grep ${PORT} | grep LISTEN | awk '{print $5}' | head -1)
    if [ ! -z "${PORT_PID}" ]; then
        print_info "发现端口 ${PORT} 的进程 ${PORT_PID}，正在停止..."
        kill -9 ${PORT_PID} 2>/dev/null
        print_success "端口 ${PORT} 已释放"
    fi
    
    return 0
}

# 检查端口是否被占用
check_port() {
    if lsof -Pi :${PORT} -sTCP:LISTEN -t >/dev/null 2>&1 || (netstat -ano 2>/dev/null | grep ${PORT} | grep LISTEN >/dev/null); then
        print_error "端口 ${PORT} 已被占用！"
        return 1
    fi
    return 0
}

# 清理旧的构建文件
clean_old_build() {
    print_info "清理旧的构建文件..."
    rm -rf "${PROJECT_ROOT}/target/${JAR_NAME}"
}

# Maven构建项目
build_project() {
    print_info "开始Maven构建..."
    
    # 使用Maven Wrapper如果存在，否则直接使用mvn
    if [ -f "deploy/mvnw" ]; then
        MVN_CMD="deploy/mvnw"
    else
        MVN_CMD="mvn"
    fi
    
    ${MVN_CMD} -gs "${MAVEN_SETTINGS}" \
        -Dmaven.repo.local="${MAVEN_REPO}" \
        -T 1C \
        clean package \
        -DskipTests \
        -U \
        -Dmaven.compile.fork=true \
        -Dmaven.test.skip=true \
        -Dmaven.compiler.fork=true \
        -Dmaven.clean.failOnError=false
    
    if [ $? -eq 0 ]; then
        print_success "Maven构建成功！"
        return 0
    else
        print_error "Maven构建失败！"
        return 1
    fi
}

# 启动应用
start_application() {
    if [ ! -f "target/${JAR_NAME}" ]; then
        print_error "未找到可执行jar文件: target/${JAR_NAME}"
        return 1
    fi
    
    print_info "启动应用..."
    print_info "JVM参数: ${JVM_OPTIONS}"
    print_info "日志目录: ${LOGS_DIR}"
    
    # 设置日志文件路径（按日期）
    LOG_DATE=$(date +%Y-%m-%d)
    LOG_FILE="${LOGS_DIR}/${PROJECT_NAME}_${LOG_DATE}.log"
    
    # 启动应用并记录PID
    nohup java ${JVM_OPTIONS} \
        -Dspring.profiles.active=local \
        -Dlogging.file.path="${LOGS_DIR}" \
        -Dlogging.file.name="${LOGS_DIR}/${PROJECT_NAME}.log" \
        -Dlogging.logback.rollingpolicy.file-name-pattern="${LOGS_DIR}/${PROJECT_NAME}_%d{yyyy-MM-dd}.log" \
        -Dlogging.logback.rollingpolicy.max-history=30 \
        -jar target/${JAR_NAME} > "${LOG_FILE}" 2>&1 &
    
    APP_PID=$!
    echo ${APP_PID} > "${PID_FILE}"
    
    print_success "应用已启动，PID: ${APP_PID}"
    print_info "日志输出: ${LOG_FILE}"
    
    # 等待应用启动
    print_info "等待应用启动..."
    sleep 3
    
    # 检查启动状态
    if ps -p ${APP_PID} > /dev/null 2>&1; then
        print_success "应用启动成功！PID: ${APP_PID}"
        print_info "请查看日志: tail -f ${LOG_FILE}"
        return 0
    else
        print_error "应用可能启动失败，请检查日志: ${LOG_FILE}"
        return 1
    fi
}

# 显示应用状态
show_status() {
    if [ -f "${PID_FILE}" ]; then
        PID=$(cat "${PID_FILE}")
        if ps -p ${PID} > /dev/null 2>&1; then
            print_success "应用正在运行，PID: ${PID}"
            
            # 显示内存使用情况
            if command -v jps &> /dev/null; then
                jps -l | grep ${PID}
            fi
            
            # 显示端口占用
            print_info "端口 ${PORT} 状态:"
            lsof -i:${PORT} 2>/dev/null || netstat -ano | grep ${PORT} 2>/dev/null || echo "无法检查端口状态"
            
            # 显示日志文件大小
            LOG_SIZE=$(du -h "${LOGS_DIR}/"*.log 2>/dev/null | tail -1 || echo "0")
            print_info "日志大小: ${LOG_SIZE}"
        else
            print_error "应用未运行（PID文件存在但进程不存在）"
        fi
    else
        print_error "应用未运行"
    fi
}

# 显示日志
show_logs() {
    LOG_DATE=$(date +%Y-%m-%d)
    LOG_FILE="${LOGS_DIR}/${PROJECT_NAME}_${LOG_DATE}.log"
    
    if [ -f "${LOG_FILE}" ]; then
        print_info "显示日志: ${LOG_FILE}"
        tail -100 "${LOG_FILE}"
    else
        print_error "今日日志文件不存在: ${LOG_FILE}"
        # 查找最新的日志文件
        LATEST_LOG=$(ls -t "${LOGS_DIR}/"*.log 2>/dev/null | head -1)
        if [ ! -z "${LATEST_LOG}" ]; then
            print_info "显示最新日志: ${LATEST_LOG}"
            tail -100 "${LATEST_LOG}"
        else
            print_error "未找到日志文件"
        fi
    fi
}

# 清理日志
clean_logs() {
    print_info "清理30天前的日志..."
    find "${LOGS_DIR}" -name "*.log" -mtime +30 -delete
    find "${LOGS_DIR}" -name "*.log.*" -mtime +30 -delete
    find "${LOGS_DIR}" -name "heapdump.hprof" -mtime +7 -delete
    print_success "日志清理完成"
}

# 主菜单
show_menu() {
    echo "============================================"
    echo "  ${PROJECT_NAME} 本地调试管理脚本"
    echo "============================================"
    echo "  1. 停止应用"
    echo "  2. 构建项目"
    echo "  3. 重启应用（停止->构建->启动）"
    echo "  4. 仅启动应用"
    echo "  5. 显示应用状态"
    echo "  6. 查看日志"
    echo "  7. 清理日志"
    echo "  8. 一键重启（推荐）"
    echo "  0. 退出"
    echo "============================================"
    echo -n "请选择操作 [0-8]: "
}

# 一键重启（完整流程）
restart_all() {
    print_info "开始一键重启流程..."
    
    # 1. 停止应用
    stop_application
    
    # 2. 清理旧构建
    clean_old_build
    
    # 3. 构建项目
    build_project
    if [ $? -ne 0 ]; then
        print_error "构建失败，终止流程"
        return 1
    fi
    
    # 4. 检查端口
    check_port
    if [ $? -ne 0 ]; then
        print_error "端口检查失败"
        return 1
    fi
    
    # 5. 启动应用
    start_application
    if [ $? -eq 0 ]; then
        print_success "一键重启完成！"
        return 0
    else
        print_error "一键重启失败"
        return 1
    fi
}

# 主函数
main() {
    create_logs_dir
    
    case "$1" in
        "stop")
            stop_application
            ;;
        "build")
            build_project
            ;;
        "start")
            start_application
            ;;
        "restart")
            stop_application
            sleep 2
            start_application
            ;;
        "status")
            show_status
            ;;
        "logs")
            show_logs
            ;;
        "clean")
            clean_logs
            ;;
        "full-restart")
            restart_all
            ;;
        *)
            # 交互式菜单
            while true; do
                show_menu
                read choice
                case $choice in
                    1) stop_application ;;
                    2) build_project ;;
                    3) stop_application && sleep 2 && start_application ;;
                    4) start_application ;;
                    5) show_status ;;
                    6) show_logs ;;
                    7) clean_logs ;;
                    8) restart_all ;;
                    0) print_info "退出脚本"; exit 0 ;;
                    *) print_error "无效选择" ;;
                esac
                echo ""
                echo "按Enter键继续..."
                read
            done
            ;;
    esac
}

# 脚本入口
main "$@"