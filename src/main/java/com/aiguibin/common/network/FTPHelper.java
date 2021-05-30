package com.aiguibin.common.network;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FTPHelper {
    private static final Log logger = LogFactory.getLog(FTPHelper.class);
    private static FTPClient ftpClient;

    /**
     * 连接登录FTP服务器
     *
     * @param server   FTP服务器
     * @param port     端口号
     * @param user     用户名
     * @param password 密码
     * @param path     路径
     */
    public static void connectServer(String server, int port, String user, String password, String path) {
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(server, port);
            ftpClient.setControlEncoding("GBK");
            ftpClient.login(user, password);
            // 检验登陆操作的返回码是否正确
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                ftpClient.disconnect();
            }
            //切换至工作目录
            if (path.length() != 0) {
                ftpClient.changeWorkingDirectory(path);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("FTP登陆成功并切换至%s工作目录.", path));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (logger.isErrorEnabled()) {
                logger.error(String.format("FTP连接失败.\r\nserver:%s,\r\nport:%d,\r\nuser:%s," +
                        "\r\npassword:%s,\r\npath:%s\r\n", server, port, user, password, path), ex);
            }
        }
    }

    /**
     * 上传文件
     *
     * @param fileName 文件名称
     * @param newName  新文件名
     * @return true/false
     * @throws IOException IO异常
     */
    public synchronized static boolean uploadFile(String fileName, String newName) throws IOException {
        boolean flag;
        InputStream iStream = null;
        try {
            iStream = new FileInputStream(fileName);
            // 转换成二进制形式文件
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            flag = ftpClient.storeFile(newName, iStream);
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        } finally {
            if (iStream != null) {
                iStream.close();
            }
        }
        return flag;
    }

    /**
     * 上传文件
     *
     * @param fileName 文件名称
     * @return true/false
     * @throws IOException IO 异常
     */
    public synchronized static boolean uploadFile(String fileName) throws IOException {
        return uploadFile(fileName, fileName);
    }

    /**
     * 上传文件
     *
     * @param iStream 文件流
     * @param newName 文件名
     * @return true/false
     * @throws IOException IO异常
     */
    public synchronized static boolean uploadFile(InputStream iStream, String newName) throws IOException {
        boolean flag;
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            flag = ftpClient.storeFile(newName, iStream);
        } catch (IOException e) {
            flag = false;
            return flag;
        } finally {
            if (iStream != null) {
                iStream.close();
            }
        }
        return flag;
    }

    /**
     * 下载存储到本地
     *
     * @param remoteFileName 远程文件名
     * @param localFileName  存储文件名
     * @return true/false
     * @throws IOException IO异常
     */
    public static synchronized boolean download(String remoteFileName, String localFileName) throws IOException {
        boolean flag = false;
        File outfile = new File(localFileName);
        OutputStream oStream = null;
        try {
            oStream = new FileOutputStream(outfile);
            ftpClient.enterLocalPassiveMode();
            /**
             * Utils.containCharacter(remoteFileName)
             * 判定远程文件是否存在
             */
            if (true) {
                FTPFile[] fs = ftpClient.listFiles();
                for (FTPFile ff : fs) {
                    String fileName = new String(ff.getName().getBytes("UTF-8"), "GBK");
                    if (fileName.equals(remoteFileName)) {
                        flag = ftpClient.retrieveFile(ff.getName(), oStream);
                        break;
                    }
                }
            } else {
                flag = ftpClient.retrieveFile(remoteFileName, oStream);
            }
        } catch (IOException e) {
            flag = false;
            return flag;
        } finally {
            oStream.close();
        }
        return flag;
    }

    /**
     * 下载获取文件流
     *
     * @param sourceFileName 源文件名称
     * @return 文件输入流
     * @throws IOException IO异常
     */
    public static synchronized InputStream downFile(String sourceFileName) throws IOException {
        return ftpClient.retrieveFileStream(sourceFileName);
    }

    /**
     * 设置文件类型
     *
     * @param fileType 文件类型代码
     * @throws IOException IO异常
     */
    public void setFileType(int fileType) throws IOException {
        ftpClient.setFileType(fileType);
    }

    /**
     * 关闭连接
     *
     * @throws IOException IO异常
     */
    public static void closeServer() {
        if (ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("FTP服务连接关闭失败", ex);
                }
            }
        }
    }

    /**
     * 代替CMD中的命令 cd 切换路径
     *
     * @param path 路径
     * @return true/false
     * @throws IOException IO异常
     */
    public static boolean changeDirectory(String path) throws IOException {
        return ftpClient.changeWorkingDirectory(path);
    }

    /**
     * 代替CMD中的命令 mkdir 创建文件夹
     *
     * @param pathName 文件名称
     * @return true/false
     * @throws IOException IO异常
     */
    public static boolean createDirectory(String pathName) throws IOException {
        return ftpClient.makeDirectory(pathName);
    }

    /**
     * 代替CMD中的命令 rm 删除文件夹
     *
     * @param path 文件夹路径
     * @return true/false
     * @throws IOException IO异常
     */
    public boolean removeDirectory(String path) throws IOException {
        return ftpClient.removeDirectory(path);
    }

    /**
     * 删除目录操作
     *
     * @param path  路径
     * @param isAll true/false
     * @return true/false
     * @throws IOException IO异常
     */
    public synchronized boolean removeDirectory(String path, boolean isAll)
            throws IOException {
        if (!isAll) {
            return removeDirectory(path);
        }
        ftpClient.enterLocalPassiveMode();
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr == null || ftpFileArr.length == 0) {
            return removeDirectory(path);
        }
        for (FTPFile ftpFile : ftpFileArr) {
            String name = ftpFile.getName();
            if (ftpFile.isDirectory()) {
                System.out.println("* [directory]Delete subPath [" + path + "/"
                        + name + "]");
                removeDirectory(path + "/" + name, true);
            } else if (ftpFile.isFile()) {
                System.out.println("* [file]Delete file [" + path + "/" + name
                        + "]");
                deleteFile(path + "/" + name);
            } else if (ftpFile.isSymbolicLink()) {
                // do something
            } else if (ftpFile.isUnknown()) {
                // do something
            }
        }
        return ftpClient.removeDirectory(path);
    }

    /**
     * 代替CMD命令中的 rm 删除文件
     *
     * @param pathName 文件名称
     * @return true/false
     * @throws IOException IO异常
     */
    public static boolean deleteFile(String pathName) throws IOException {
        return ftpClient.deleteFile(pathName);
    }

    /**
     * 判断目录是否存在
     *
     * @param path 文件路径
     * @return true/false
     * @throws IOException IO异常
     */
    public synchronized static boolean existDirectory(String path)
            throws IOException {
        boolean flag = false;
        ftpClient.enterLocalPassiveMode();
        FTPFile[] ftpFileArr = ftpClient.listFiles("");
        for (FTPFile ftpFile : ftpFileArr) {
            if (ftpFile.isDirectory()
                    && ftpFile.getName().equalsIgnoreCase(path)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 获取目录下所有文件
     *
     * @param path 路径
     * @return 文件名称集合
     * @throws IOException IO异常
     */
    public synchronized static List<String> getFileList(String path)
            throws IOException {
        ftpClient.enterLocalPassiveMode();
        FTPFile[] ftpFiles = ftpClient.listFiles(path);
        List<String> retList = new ArrayList<String>();
        if (ftpFiles == null || ftpFiles.length == 0) {
            return retList;
        }
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.isFile()) {
                retList.add(ftpFile.getName());
            }
        }
        return retList;
    }
    /**
     * 获取目录下所有文件大小
     *
     * @param path 路径
     * @return 文件大小集合
     * @throws IOException IO异常
     */
    public synchronized static List<Long> getFileSizeList(String path) throws IOException {
        ftpClient.enterLocalPassiveMode();
        FTPFile[] ftpFiles = ftpClient.listFiles(path);
        List<Long> retList = new ArrayList<>();
        if (ftpFiles == null || ftpFiles.length == 0) {
            return retList;
        }
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.isFile()) {
                retList.add(ftpFile.getSize());
            }
        }
        return retList;
    }

    /**
     * 获取目录下所有文件
     *
     * @param path 路径
     * @return 文件名称集合
     * @throws IOException IO异常
     */
    public synchronized static long getFileSize(String path) throws IOException {
        return getFileSizeList(path).get(0);
    }

    /**
     * 检验指定路径的文件是否存在ftp服务器中
     *
     * @param remoteFileName 文件名称
     * @return 存在返回true，不存在返回false
     */
    public static boolean isFTPFileExist(String remoteFileName) {
        try {
            ftpClient.enterLocalPassiveMode();
            InputStream is = ftpClient.retrieveFileStream(new String(remoteFileName.getBytes("GBK"),
                    FTP.DEFAULT_CONTROL_ENCODING));
            if (is == null || ftpClient.getReplyCode() == FTPReply.FILE_UNAVAILABLE) {
                return false;
            }
            if (is != null) {
                is.close();
                ftpClient.completePendingCommand();
            }
            return true;
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("-检查文件错误-文件名:%s.", remoteFileName), ex);
            }
        }
        return false;
    }
}
