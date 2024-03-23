package cn.autumnclouds.filesystem.impl;

import cn.autumnclouds.command.CommandInterpreter;
import cn.autumnclouds.filesystem.FileSystem;
import cn.hutool.core.util.StrUtil;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.AlgorithmParameters;
import java.util.regex.Matcher;

/**
 * @author Fu Qiujie
 * @since 2024/3/16
 */


public class SimpleFileSystem implements FileSystem {
    private static final String BASE_DIRECTORY = "E:\\coding\\Java\\project\\fileSystem\\src\\main\\resources\\files\\";
    private String currentDirectory = BASE_DIRECTORY;

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
        File[] files = directory.listFiles();
        return files;
    }

    @Override
    public boolean copy(String sourcePath, String destinationPath) {
        sourcePath = convertPathIfNecessary(sourcePath);
        destinationPath = convertPathIfNecessary(destinationPath);
        Path path = Paths.get(sourcePath);
        if (Files.isDirectory(path)) {
            return copyDirectory(sourcePath, destinationPath);
        } else {
            return copyFile(sourcePath, destinationPath);
        }
    }

    public boolean copyFile(String sourcePath, String destinationPath) {
        Path source = Paths.get(sourcePath);
        Path destination = Paths.get(destinationPath);
        try {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public boolean copyDirectory(String sourcePath, String destinationPath) {
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
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.copy(file, destination.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public void encryptFile(String filePath, String encryptedFilePath) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey secretKey = keyGen.generateKey();

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            AlgorithmParameters params = cipher.getParameters();
            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

            try (FileInputStream inputStream = new FileInputStream(filePath);
                 FileOutputStream outputStream = new FileOutputStream(encryptedFilePath)) {
                outputStream.write(iv);
                byte[] inputBytes = inputStream.readAllBytes();
                byte[] encryptedBytes = cipher.doFinal(inputBytes);
                outputStream.write(encryptedBytes);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void decryptFile(String encryptedFilePath, String decryptedFilePath) {
        try {
            try (FileInputStream inputStream = new FileInputStream(encryptedFilePath);
                 FileOutputStream outputStream = new FileOutputStream(decryptedFilePath)) {
                byte[] iv = new byte[16];
                inputStream.read(iv);

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec("YourSecretKeyBytes".getBytes(), "AES"), new IvParameterSpec(iv));

                byte[] encryptedBytes = inputStream.readAllBytes();
                byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
                outputStream.write(decryptedBytes);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String convertPathIfNecessary(String path) {
        path = path.replaceAll("/", Matcher.quoteReplacement(File.separator));
        if (path.startsWith(File.separator)) {
            return BASE_DIRECTORY + path;
        }
        return currentDirectory + File.separator + path;
    }
}
