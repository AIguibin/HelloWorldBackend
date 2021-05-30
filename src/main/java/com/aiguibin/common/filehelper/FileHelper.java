package com.aiguibin.common.filehelper;

import com.aiguibin.common.character.StringHelper;
import com.aiguibin.common.datetime.DateHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.UUID;

import static java.nio.file.FileVisitResult.CONTINUE;

public class FileHelper {
    private static final Log logger = LogFactory.getLog(FileHelper.class);

    public FileHelper() {
    }

    public static String getType(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    /**
     * 删除指定的文件或目录，如果是目录，将会删除子目录及其包含的文件。
     *
     * @param filePath 待删除的文件或目录对象
     * @throws IOException 删除过程中发生的异常
     * @see #deleteFile(File)
     */
    public static void deleteFile(String filePath) throws IOException {
        deleteFile(Paths.get(filePath));
    }

    /**
     * 删除指定的文件或目录，如果是目录，将会删除子目录及其包含的文件。
     *
     * @param file 待删除的文件或目录
     * @throws IOException 删除过程中发生的异常
     */
    public static void deleteFile(Path file) throws IOException {
        Files.walkFileTree(file, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException ex) throws IOException {
                if (ex == null) {
                    Files.delete(dir);
                    return CONTINUE;
                } else {
                    // directory iteration failed
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("Visit directory fail, path: %s.", dir.getFileName().toString()), ex);
                    }
                    throw ex;
                }
            }
        });
    }

    /**
     * 删除指定的文件或目录，如果是目录，将会删除子目录及其包含的文件。
     *
     * @param file 待删除的文件或目录对象
     * @deprecated 升级成Paths实现，未来版本可能删除此方法
     */
    public static void deleteFile(File file) {
        if (file == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("The file object is null.");
            }
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("There has not any children in the path: %s.", file.getAbsolutePath()));
                }
                return;
            }
            if (files.length > 0) {
                for (File child : files) {
                    // 迭代处理子目录
                    deleteFile(child);
                    // 子目录处理完毕后，删除父目录
                    if (file.delete()) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("Delete path successfully, path: %s.", file.getAbsolutePath()));
                        }
                    }
                }
            } else {
                // 空目录，直接删除
                if (file.delete()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Delete path successfully, path: %s.", file.getAbsolutePath()));
                    }
                }
            }
        } else {
            // 文件，直接删除
            if (file.delete()) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Delete path successfully, path: %s.", file.getAbsolutePath()));
                }
            }
        }
    }

    /**
     * 将输入流中的数据保存到指定的目录中，并按照保存日期建立子目录，文件名采用UUID自动生成。
     *
     * @param filePath 目录
     * @param is       输入流
     * @return 保存的文件路径
     * @throws IOException 保存过程中发生的异常
     */
    public static String saveFile(String filePath, InputStream is) throws IOException {
        String date = DateHelper.get8TimeNow(), fileName = UUID.randomUUID().toString().replaceAll("-", "");
        Path path = Paths.get(filePath, date, fileName);
        Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
        return path.toFile().getAbsolutePath();
    }

    /**
     * 将输入流中的数据保存到指定目录中的文件中
     *
     * @param filePath 目录
     * @param fileName 文件名
     * @param is       输入流
     * @return 保存的文件路径
     * @throws IOException 保存过程中发生的异常
     */
    public static String saveFile(String filePath, String fileName, InputStream is) throws IOException {
        Path path = Paths.get(fileName, fileName);
        Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
        return path.toFile().getAbsolutePath();
    }

    /**
     * 复制指定的文件或目录
     *
     * @param source 源
     * @param target 目标
     * @throws IOException 复制过程中发生的异常
     */
    public static void copyFile(String source, String target) throws IOException {
        if (StringHelper.isEmpty(source) || StringHelper.isEmpty(target) || source.equals(target)) {
            throw new IllegalArgumentException(String.format("source: %s, target: %s. ", source, target));
        }
        copyFile(Paths.get(source), Paths.get(target));
    }

    /**
     * 复制指定的的文件或目录
     *
     * @param source 源
     * @param target 目标
     * @throws IOException 复制过程中发生的异常
     */
    public static void copyFile(Path source, Path target) throws IOException {
        Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                            throws IOException {
                        Path targetdir = target.resolve(source.relativize(dir));
                        try {
                            Files.copy(dir, targetdir);
                        } catch (FileAlreadyExistsException ex) {
                            if (!Files.isDirectory(targetdir)) {
                                throw ex;
                            }
                        }
                        return CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException {
                        Files.copy(file, target.resolve(source.relativize(file)));
                        return CONTINUE;
                    }
                });
    }

    /**
     * 获取文件长度
     *
     * @param file
     */
    public static void getFileSizeByLength(File file) {
        if (file.exists() && file.isFile()) {
            String fileName = file.getName();
            System.out.println("文件" + fileName + "的大小是：" + file.length());
        }
    }

    /**
     * 根据java.io.*的流获取文件大小
     *
     * @param file
     */
    public static void getFileSizeByIo(File file) {
        FileInputStream fis = null;
        try {
            if (file.exists() && file.isFile()) {
                String fileName = file.getName();
                fis = new FileInputStream(file);
                System.out.println("文件" + fileName + "的大小是：" + fis.available() + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据java.nio.*的流获取文件大小
     *
     * @param file
     */
    public static void getFileSizeByNio(File file) {
        FileChannel fc = null;
        try {
            if (file.exists() && file.isFile()) {
                String fileName = file.getName();
                FileInputStream fis = new FileInputStream(file);
                fc = fis.getChannel();
                System.out.println("文件" + fileName + "的大小是：" + fc.size() + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fc) {
                try {
                    fc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * <p>将文件转成base64 字符串</p>
     *
     * @param path 文件路径
     * @return
     * @throws Exception
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return new BASE64Encoder().encode(buffer);
    }

    /**
     * <p>将base64字符解码保存文件</p>
     *
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */
    public static void decoderBase64File(String base64Code, String targetPath) throws Exception {
        byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }

    /**
     * <p>将base64字符保存文本文件</p>
     *
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */
    public static void toFile(String base64Code, String targetPath) throws Exception {
        byte[] buffer = base64Code.getBytes();
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }

    /**
     * 二进制流转Base64字符串
     *
     * @param data 二进制流
     * @return string  Base64字符串
     * @throws IOException 异常
     */
    public static String getImageString(byte[] data) throws IOException {
        BASE64Encoder encoder = new BASE64Encoder();
        return data != null ? encoder.encode(data) : "";
    }

    /**
     * Base64字符串转 二进制流
     *
     * @param base64String Base64字符串
     * @return byte[] 二进制数据流
     * @throws IOException 异常
     */
    public static byte[] getStringImage(String base64String) throws IOException {
        BASE64Decoder decoder = new sun.misc.BASE64Decoder();
        return base64String != null ? decoder.decodeBuffer(base64String) : null;
    }
}
