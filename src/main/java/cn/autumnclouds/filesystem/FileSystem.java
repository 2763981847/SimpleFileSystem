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

    File[] listContents();

    boolean changeDirectory(String directoryName);

    boolean copy(String sourcePath, String destinationPath);

    void encryptFile(String filePath, String encryptedFilePath);

    void decryptFile(String encryptedFilePath, String decryptedFilePath);


}
