package cn.autumnclouds.filesystem.impl;

import cn.autumnclouds.filesystem.FileSystem;
import cn.autumnclouds.util.FileUtils;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.zip.ZipOutputStream;

/**
 * 基于控制台的基本文件系统操作的实现。
 *
 * @author Fu Qiujie
 * @since 2024/3/16
 */


public class SimpleFileSystem implements FileSystem {

    // 线程池, 用于后台执行文件拷贝
    private static final ExecutorService THREAD_EXECUTOR = Executors.newSingleThreadExecutor();

    // 基础目录，所有文件系统操作都在这个目录下进行
    private static final String BASE_DIRECTORY = "E:\\coding\\Java\\project\\fileSystem\\src\\main\\resources\\files\\";

    // 当前工作目录
    private String currentDirectory = BASE_DIRECTORY;

    /**
     * 私有化构造函数实现单例，防止外部创建实例，保证程序运行时只有一个文件系统
     */
    private SimpleFileSystem() {
    }

    // 静态内部类实现单例
    private static final class FileSystemHolder {
        public static final SimpleFileSystem INSTANCE = new SimpleFileSystem();
    }

    /**
     * 获取文件系统实例。
     *
     * @return 文件系统实例。
     */
    public static SimpleFileSystem getInstance() {
        return FileSystemHolder.INSTANCE;
    }

    /**
     * 创建具有指定名称的目录。
     *
     * @param directoryName 要创建的目录名称。
     */
    @Override
    public void createDirectory(String directoryName) {
        String absolutePath = convertPathIfNecessary(directoryName);
        File newDir = new File(absolutePath);
        newDir.mkdir();
    }

    /**
     * 创建具有指定名称的文件。
     *
     * @param fileName 要创建的文件名称。
     */
    @Override
    public void createFile(String fileName) {
        String absolutePath = convertPathIfNecessary(fileName);
        File newFile = new File(absolutePath);
        try {
            newFile.createNewFile();
        } catch (IOException e) {
            System.out.println("failed to create file");
        }
    }

    /**
     * 显示文件的内容。
     *
     * @param fileName 要显示的文件名称。
     */
    @Override
    public void cat(String fileName) {
        String absolutePath = convertPathIfNecessary(fileName);
        File file = new File(absolutePath);
        if (!file.exists()) {
            System.out.println("file does not exist");
            return;
        }
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] bytes = inputStream.readAllBytes();
            System.out.println(new String(bytes));
        } catch (IOException e) {
            System.out.println("failed to read file");
        }
    }

    /**
     * 删除具有指定名称的目录。
     *
     * @param directoryName 要删除的目录名称。
     */
    @Override
    public void deleteDirectory(String directoryName) {
        String absolutePath = convertPathIfNecessary(directoryName);
        File file = new File(absolutePath);
        if (!file.exists()) {
            System.out.println("directory or file does not exist");
            return;
        }
        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).map(child -> child.toPath().toString()).forEach(this::deleteDirectory);
        } else {
            if (!file.delete()) {
                System.out.println("failed to delete file");
            }
        }
    }

    /**
     * 将内容写入文件。
     *
     * @param content  要写入文件的内容。
     * @param fileName 要写入的文件名称。
     */
    @Override
    public void echo(String content, String fileName) {
        String absolutePath = convertPathIfNecessary(fileName);
        try (FileOutputStream outputStream = new FileOutputStream(absolutePath)) {
            outputStream.write(content.getBytes());
        } catch (IOException e) {
            System.out.println("failed to write file");
        }
    }

    /**
     * 将当前目录更改为指定目录。
     *
     * @param directoryName 要更改到的目录名称。
     */
    @Override
    public void changeDirectory(String directoryName) {
        String absolutePath = convertPathIfNecessary(directoryName);
        if (Files.notExists(Paths.get(absolutePath))) {
            System.out.println("Directory does not exist");
            return;
        }
        currentDirectory = absolutePath;
    }

    /**
     * 列出当前目录的内容。
     *
     * @return 表示目录内容的 File 对象数组。
     */
    @Override
    public File[] listContents() {
        File directory = new File(currentDirectory);
        return directory.listFiles();
    }

    /**
     * 复制文件或目录。
     *
     * @param sourcePath      源路径。
     * @param destinationPath 目标路径。
     * @param bg              是否在后台运行。
     */
    @Override
    public void copy(String sourcePath, String destinationPath, boolean bg) {
        Runnable runnable = () -> {
            String src = convertPathIfNecessary(sourcePath);
            String dest = convertPathIfNecessary(destinationPath);
            File srcFile = new File(src);
            if (!srcFile.exists()) {
                System.out.println("Source file does not exist");
                return;
            }
            if (srcFile.isDirectory()) {
                copyDirectory(src, dest, bg);
            } else {
                copyFile(src, dest, bg);
            }
        };
        if (bg) {
            THREAD_EXECUTOR.submit(runnable);
        } else {
            runnable.run();
        }
    }

    /**
     * 复制文件。
     *
     * @param sourcePath      源文件路径。
     * @param destinationPath 目标文件路径。
     * @param silent          是否静默复制（静默复制时不在控制台打印进度）。
     */
    public void copyFile(String sourcePath, String destinationPath, boolean silent) {
        Path basePath = Paths.get(BASE_DIRECTORY);
        Consumer<Double> consumer = progress -> {
        };
        if (!silent) {
            consumer = progress -> System.out.printf("Copy  %s  to  %s,  progress: %.2f %%\n", basePath.relativize(Paths.get(sourcePath)), basePath.relativize(Paths.get(destinationPath)), progress);
        }
        FileUtils.fileCopyWithProgress(sourcePath, destinationPath, consumer);
    }

    /**
     * 复制目录。
     *
     * @param sourcePath      源目录路径。
     * @param destinationPath 目标目录路径。
     * @param silent          是否静默复制（静默复制时不在控制台打印进度）。
     */
    public void copyDirectory(String sourcePath, String destinationPath, boolean silent) {
        Path source = Paths.get(sourcePath);
        Path destination = Paths.get(destinationPath);
        try {
            Files.walkFileTree(source, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    Path targetPath = destination.resolve(source.relativize(dir));
                    try {
                        Files.createDirectories(targetPath);
                    } catch (IOException e) {
                        System.out.println("failed to create directory");
                        return FileVisitResult.TERMINATE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    copyFile(file.toAbsolutePath().toString(), destination.resolve(source.relativize(file)).toString(), silent);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.out.println("failed to copy directory");
        }
    }


    /**
     * 加密文件。
     *
     * @param filePath          文件路径。
     * @param encryptedFilePath 加密后文件路径。
     * @param secretKey         密钥。
     */
    @Override
    public void encryptFile(String filePath, String encryptedFilePath, String secretKey) {
        filePath = convertPathIfNecessary(filePath);
        encryptedFilePath = convertPathIfNecessary(encryptedFilePath);
        File inputFile = new File(filePath);
        File encryptedFile = new File(encryptedFilePath);
        try {
            SecretKeySpec secretKeySpec = FileUtils.generateSecretKey(secretKey);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            try (FileInputStream fis = new FileInputStream(inputFile); CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(encryptedFile), cipher);) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    cos.write(buffer, 0, bytesRead);
                }
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | IOException | InvalidKeyException e) {
            System.out.println("failed to encrypt file");
        }
    }

    /**
     * 解密文件。
     *
     * @param encryptedFilePath 加密文件路径。
     * @param decryptedFilePath 解密后文件路径。
     * @param secretKey         密钥。
     */
    @Override
    public void decryptFile(String encryptedFilePath, String decryptedFilePath, String secretKey) {
        encryptedFilePath = convertPathIfNecessary(encryptedFilePath);
        decryptedFilePath = convertPathIfNecessary(decryptedFilePath);
        File encryptedFile = new File(encryptedFilePath);
        File decryptedFile = new File(decryptedFilePath);
        try {
            SecretKeySpec secretKeySpec = FileUtils.generateSecretKey(secretKey);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            try (CipherInputStream cis = new CipherInputStream(new FileInputStream(encryptedFile), cipher); FileOutputStream fos = new FileOutputStream(decryptedFile);) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = cis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | IOException | InvalidKeyException e) {
            System.out.println("failed to decrypt file");
        }
    }

    /**
     * 压缩文件。
     *
     * @param filePath           文件路径。
     * @param compressedFilePath 压缩后文件路径。
     */
    @Override
    public void compressFile(String filePath, String compressedFilePath) {
        filePath = convertPathIfNecessary(filePath);
        compressedFilePath = convertPathIfNecessary(compressedFilePath);
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(compressedFilePath))) {
            File fileToZip = new File(filePath);
            FileUtils.zipFile(fileToZip, fileToZip.getName(), zipOut);
        } catch (IOException e) {
            System.out.println("failed to compress file");
        }
    }

    /**
     * 解压文件。
     *
     * @param filePath             压缩文件路径。
     * @param decompressedFilePath 解压文件路径。
     */
    @Override
    public void decompressFile(String filePath, String decompressedFilePath) {
        filePath = convertPathIfNecessary(filePath);
        decompressedFilePath = convertPathIfNecessary(decompressedFilePath);
        FileUtils.unzipFile(filePath, decompressedFilePath);
    }

    /**
     * 对路径进行相应转换
     *
     * @param path 要转换的路径。
     * @return 转换后的路径。
     */
    private String convertPathIfNecessary(String path) {
        path = path.replaceAll("/", Matcher.quoteReplacement(File.separator));
        if (path.startsWith(File.separator)) {
            return BASE_DIRECTORY + path;
        }
        return currentDirectory + File.separator + path;
    }
}
