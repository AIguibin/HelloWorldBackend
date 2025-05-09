package com.aiguibin.core.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 */
public class FileAccessor {


    //日志声明
    private static final Log logger = LogFactory.getLog(FileAccessor.class);

    // 获取项目根目录（IDE中通常是项目根路径，打包后可能是JAR所在目录）
    private static final String projectRoot = System.getProperty("user.dir");

    public Path getProjectRootFolderPath(String folderName) {
        // 拼接自定义文件夹路径
        Path projectRootFolderPath=Paths.get(projectRoot, folderName);
        // 检查文件夹是否存在
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("警告：%s目录不存在！", folderName));
            // 或更简洁的写法（依赖日志库是否支持参数化）：
            // logger.debug("警告：{}目录不存在！", folderName);
        }
        return projectRootFolderPath;
    }
}
