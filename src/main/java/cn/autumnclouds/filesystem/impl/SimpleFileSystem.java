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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.zip.ZipOutputStream;

/**
 * @author Fu Qiujie
 * @since 2024/3/16
 */


public class SimpleFileSystem implements FileSystem {

    private static final ExecutorService THREAD_EXECUTOR = Executors.newSingleThreadExecutor();
    private static final String BASE_DIRECTORY = "E:\\coding\\Java\\project\\fileSystem\\src\\main\\resources\\files\\";
    private String currentDirectory = BASE_DIRECTORY;
    private static final String SECRET_KEY = "dxiaopwlxhsjxien";

    /**
     * 私有化构造函数实现单例，防止外部创建实例，保证程序运行时只有一个文件系统
     */
    private SimpleFileSystem() {
    }

    private static final class FileSystemHolder {
        public static final SimpleFileSystem INSTANCE = new SimpleFileSystem();
    }

    public static SimpleFileSystem getInstance() {
        return FileSystemHolder.INSTANCE;
    }

    @Override
    public boolean createDirectory(String directoryName) {
        String absolutePath = convertPathIfNecessary(directoryName);
        File newDir = new File(absolutePath);
        return newDir.mkdir();
    }

    @Override
    public boolean createFile(String fileName) {
        String absolutePath = convertPathIfNecessary(fileName);
        File newFile = new File(absolutePath);
        try {
            return newFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void cat(String fileName) {
        String absolutePath = convertPathIfNecessary(fileName);
        File file = new File(absolutePath);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] bytes = inputStream.readAllBytes();
            System.out.println(new String(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteDirectory(String directoryName) {
        String absolutePath = convertPathIfNecessary(directoryName);
        File dirToDelete = new File(absolutePath);
        if (!dirToDelete.exists()) {
            System.out.println("Directory does not exist");
            return;
        }
        try {
            Files.walkFileTree(dirToDelete.toPath(), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return FileVisitResult.TERMINATE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    try {
                        Files.delete(dir);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return FileVisitResult.TERMINATE;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void echo(String content, String fileName) {
        String absolutePath = convertPathIfNecessary(fileName);
        try (FileOutputStream outputStream = new FileOutputStream(absolutePath)) {
            outputStream.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean changeDirectory(String directoryName) {
        String absolutePath = convertPathIfNecessary(directoryName);
        if (Files.notExists(Paths.get(absolutePath))) {
            return false;
        }
        currentDirectory = absolutePath;
        return true;
    }

    @Override
    public File[] listContents() {
        File directory = new File(currentDirectory);
        return directory.listFiles();
    }

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

    public void copyFile(String sourcePath, String destinationPath) {
        copyFile(sourcePath, destinationPath, false);
    }

    public void copyFile(String sourcePath, String destinationPath, boolean silent) {
        Path basePath = Paths.get(BASE_DIRECTORY);
        Consumer<Double> consumer = progress -> {
        };
        if (!silent) {
            consumer = progress -> System.out.printf("Copy  %s  to  %s,  progress: %.2f %%\n", basePath.relativize(Paths.get(sourcePath)), basePath.relativize(Paths.get(destinationPath)), progress);
        }
        FileUtils.fileCopyWithProgress(sourcePath, destinationPath, consumer);
    }

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
                        e.printStackTrace();
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
            throw new RuntimeException(e);
        }
    }

    public void copyDirectory(String sourcePath, String destinationPath) {
        copyDirectory(sourcePath, destinationPath, false);
    }

    @Override
    public void encryptFile(String filePath, String encryptedFilePath) {
        filePath = convertPathIfNecessary(filePath);
        encryptedFilePath = convertPathIfNecessary(encryptedFilePath);
        File inputFile = new File(filePath);
        File encryptedFile = new File(encryptedFilePath);
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            try (FileInputStream fis = new FileInputStream(inputFile); CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(encryptedFile), cipher);) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    cos.write(buffer, 0, bytesRead);
                }
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | IOException | InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void decryptFile(String filePath, String decryptedFilePath) {
        filePath = convertPathIfNecessary(filePath);
        decryptedFilePath = convertPathIfNecessary(decryptedFilePath);
        File encryptedFile = new File(filePath);
        File decryptedFile = new File(decryptedFilePath);
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            try (CipherInputStream cis = new CipherInputStream(new FileInputStream(encryptedFile), cipher); FileOutputStream fos = new FileOutputStream(decryptedFile);) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = cis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | IOException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void compressFile(String filePath, String compressedFilePath) {
        filePath = convertPathIfNecessary(filePath);
        compressedFilePath = convertPathIfNecessary(compressedFilePath);
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(compressedFilePath))) {
            File fileToZip = new File(filePath);
            FileUtils.zipFile(fileToZip, fileToZip.getName(), zipOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decompressFile(String filePath, String decompressedFilePath) {
        filePath = convertPathIfNecessary(filePath);
        decompressedFilePath = convertPathIfNecessary(decompressedFilePath);
        FileUtils.unzipFile(filePath, decompressedFilePath);
    }

    private String convertPathIfNecessary(String path) {
        path = path.replaceAll("/", Matcher.quoteReplacement(File.separator));
        if (path.startsWith(File.separator)) {
            return BASE_DIRECTORY + path;
        }
        return currentDirectory + File.separator + path;
    }
}
