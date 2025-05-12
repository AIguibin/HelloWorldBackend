package com.aiguibin.core.common;


import com.aiguibin.core.config.SvnConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.wc.*;


import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class SvnUtils {

    private final String svnUrl;
    private final String username;
    private final String password;

    @Autowired
    public SvnUtils(SvnConfig svnConfig) {
        this.svnUrl = svnConfig.getUrl();
        this.username = svnConfig.getUsername();
        this.password = svnConfig.getPassword();
    }

    //日志声明
    private static final Log logger = LogFactory.getLog(SvnUtils.class);

    static {
        // 初始化SVN协议
        SVNRepositoryFactoryImpl.setup();
    }


    /**
     * 确保文件存在，若不存在则从SVN检出
     */
    public void ensureFileCheckedOut(String localFilePath) throws Exception {
        File file = new File(localFilePath);
        if (file.exists()) return;

        logger.info("文件不存在，尝试从SVN检出: " + localFilePath);
        File parentDir = file.getParentFile();
        if (parentDir == null) throw new Exception("无效的文件路径");

        try {
            new FileAccessor().ensureDirectoryExists(parentDir.getAbsolutePath());
            checkout(parentDir.getAbsolutePath());
        } catch (IOException | SVNException e) {
            logger.error("操作失败", e);
            throw e;
        }

        if (!file.exists()) {
            logger.error("检出后文件仍不存在: " + localFilePath);
            throw new Exception("SVN检出未生成目标文件");
        }
    }

    /**
     * 从SVN检出目录
     *
     * @param localPath 本地存储路径
     */
    public void checkout(String localPath) throws SVNException {
        SVNClientManager clientManager = createClientManager();
        SVNUpdateClient updateClient = clientManager.getUpdateClient();
        SVNURL url = SVNURL.parseURIEncoded(this.svnUrl);
        updateClient.doCheckout(url, new File(localPath), SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, true);
    }

    /**
     * 上传文件到SVN目录（类似svn import命令）
     *
     * @param localFile     本地文件路径
     * @param svnTargetUrl  SVN目标地址
     * @param commitMessage 提交信息
     */
    public void upload(String localFile, String svnTargetUrl, String commitMessage) throws SVNException {
        SVNClientManager clientManager = createClientManager(); // 使用配置的账号密码
        SVNCommitClient commitClient = clientManager.getCommitClient();
        commitClient.doImport(
                new File(localFile),
                SVNURL.parseURIEncoded(svnTargetUrl),
                commitMessage, null, true, true, SVNDepth.INFINITY
        );
    }

    /**
     * 批量上传文件到SVN目录（类似svn import命令）
     *
     * @param localFiles    本地文件路径列表
     * @param svnTargetUrl  SVN目标地址
     * @param commitMessage 提交信息
     */
    public void uploadFiles(List<String> localFiles, String svnTargetUrl, String commitMessage) throws Exception {
        SVNClientManager clientManager = createClientManager();
        SVNCommitClient commitClient = clientManager.getCommitClient();
        SVNURL url = SVNURL.parseURIEncoded(svnTargetUrl);

        for (String localFile : localFiles) {
            File file = new File(localFile);
            if (!file.exists()) throw new Exception("文件不存在: " + localFile);
            commitClient.doImport(file, url, commitMessage, null, true, true, SVNDepth.INFINITY);
        }
    }

    /**
     * 创建SVN客户端管理器实例，用于执行SVN操作
     *
     * @return 配置了默认选项和认证信息的SVN客户端管理器
     * @implNote 1. 使用SVNWCUtil工具类创建：
     * - ISVNOptions: 默认SVN配置（参数true表示启用工作副本兼容性检查）
     * - ISVNAuthenticationManager: 基础认证管理器（支持密码认证方式）
     * 2. 密码使用char[]类型传递，符合安全规范（避免String驻留内存）
     */
    private SVNClientManager createClientManager() {
        ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(
                this.username,
                this.password.toCharArray() // 使用注入的密码
        );
        return SVNClientManager.newInstance(options, authManager);
    }

}