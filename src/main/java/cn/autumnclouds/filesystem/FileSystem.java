package cn.autumnclouds.filesystem;

import java.io.File;
import java.io.IOException;

/**
 * @author Fu Qiujie
 * @since 2024/3/17
 */
public interface FileSystem {
    boolean createDirectory(String directoryName);

    boolean createFile(String fileName);


    void cat(String fileName);

    void deleteDirectory(String directoryName);

    void echo(String content, String fileName);

    File[] listContents();

    boolean changeDirectory(String directoryName);

    void copy(String sourcePath, String destinationPath, boolean bg);

    default void copy(String sourcePath, String destinationPath) {
        copy(sourcePath, destinationPath, false);
    }

    void encryptFile(String filePath, String encryptedFilePath);

    void decryptFile(String encryptedFilePath, String decryptedFilePath);

    void compressFile(String filePath, String compressedFilePath);

    void decompressFile(String filePath, String decompressedFilePath);


}
