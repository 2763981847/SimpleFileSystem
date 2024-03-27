package cn.autumnclouds.filesystem;

import java.io.File;

/**
 * 定义基本文件系统操作的接口。
 * 实现该接口的类应提供用于与文件和目录交互的方法。
 *
 * @author Fu Qiujie
 * @since 2024/3/17
 */
public interface FileSystem {

    /**
     * 默认用于加密和解密的密钥。
     */
    String DEFAULT_SECRET_KEY = "aaaaaaaaaaaaaaaa";

    /**
     * 创建具有指定名称的目录。
     *
     * @param directoryName 要创建的目录名称。
     */
    void createDirectory(String directoryName);

    /**
     * 创建具有指定名称的文件。
     *
     * @param fileName 要创建的文件名称。
     */
    void createFile(String fileName);

    /**
     * 显示文件的内容。
     *
     * @param fileName 要显示的文件名称。
     */
    void cat(String fileName);

    /**
     * 删除具有指定文件或目录
     *
     * @param path  要删除的文件或目录的路径。
     */
    void delete(String path);

    /**
     * 将内容写入文件。
     *
     * @param content    要写入文件的内容。
     * @param fileName   要写入的文件名称。
     */
    void echo(String content, String fileName);

    /**
     * 列出当前目录的内容。
     *
     * @return 表示目录内容的 File 对象数组。
     */
    File[] listContents();

    /**
     * 将当前目录更改为指定目录。
     *
     * @param directoryName 要更改到的目录名称。
     */
    void changeDirectory(String directoryName);

    /**
     * 将文件或目录从源路径复制到目标路径。
     *
     * @param sourcePath      源文件或目录的路径。
     * @param destinationPath 目标目录的路径。
     * @param bg              是否在后台执行复制操作。
     */
    void copy(String sourcePath, String destinationPath, boolean bg);

    /**
     * 将文件或目录从源路径复制到目标路径。
     *
     * @param sourcePath      源文件或目录的路径。
     * @param destinationPath 目标目录的路径。
     */
    default void copy(String sourcePath, String destinationPath) {
        copy(sourcePath, destinationPath, false);
    }

    /**
     * 使用指定的密钥加密文件。
     *
     * @param filePath         要加密的文件路径。
     * @param encryptedFilePath 保存加密文件的路径。
     * @param secretKey        用于加密的密钥。
     */
    void encryptFile(String filePath, String encryptedFilePath, String secretKey);

    /**
     * 使用默认密钥加密文件。
     *
     * @param filePath         要加密的文件路径。
     * @param encryptedFilePath 保存加密文件的路径。
     */
    default void encryptFile(String filePath, String encryptedFilePath) {
        encryptFile(filePath, encryptedFilePath, DEFAULT_SECRET_KEY);
    }

    /**
     * 使用指定的密钥解密加密文件。
     *
     * @param encryptedFilePath 要解密的加密文件路径。
     * @param decryptedFilePath 保存解密文件的路径。
     * @param secretKey          用于解密的密钥。
     */
    void decryptFile(String encryptedFilePath, String decryptedFilePath, String secretKey);

    /**
     * 使用默认密钥解密加密文件。
     *
     * @param encryptedFilePath 要解密的加密文件路径。
     * @param decryptedFilePath 保存解密文件的路径。
     */
    default void decryptFile(String encryptedFilePath, String decryptedFilePath) {
        decryptFile(encryptedFilePath, decryptedFilePath, DEFAULT_SECRET_KEY);
    }

    /**
     * 将文件压缩为压缩文件。
     *
     * @param filePath           要压缩的文件路径。
     * @param compressedFilePath 保存压缩文件的路径。
     */
    void compressFile(String filePath, String compressedFilePath);

    /**
     * 将压缩文件解压为原始形式。
     *
     * @param filePath             要解压缩的压缩文件路径。
     * @param decompressedFilePath 保存解压缩文件的路径。
     */
    void decompressFile(String filePath, String decompressedFilePath);
}