package cn.autumnclouds.util;

import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 文件工具类
 *
 * @author Fu Qiujie
 * @since 2024/3/23
 */
public class FileUtils {

    /**
     * 文件拷贝, 支持进度回调
     *
     * @param src      源文件路径
     * @param dest     目标文件路径
     * @param consumer 进度回调方法
     */
    public static void fileCopyWithProgress(String src, String dest, Consumer<Double> consumer) {
        File fileIn = new File(src);
        File fileOut = new File(dest);
        try (FileInputStream fis = new FileInputStream(fileIn); FileOutputStream fos = new FileOutputStream(fileOut)) {
            byte[] buffer = new byte[1024];
            long fileSize = fileIn.length();
            long copiedBytes = 0;
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                copiedBytes += bytesRead;
                double progress = (double) copiedBytes / fileSize * 100;
                consumer.accept(progress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 压缩文件
     *
     * @param fileToZip 文件
     * @param fileName  文件名
     * @param zipOut    压缩文件输出流
     */
    public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) {
        try {
            if (fileToZip.isDirectory()) {
                if (fileName.endsWith("/")) {
                    zipOut.putNextEntry(new ZipEntry(fileName));
                    zipOut.closeEntry();
                } else {
                    zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                    zipOut.closeEntry();
                }
                File[] children = fileToZip.listFiles();
                for (File childFile : children) {
                    zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
                }
                return;
            }
            try (FileInputStream fis = new FileInputStream(fileToZip)) {
                ZipEntry zipEntry = new ZipEntry(fileName);
                zipOut.putNextEntry(zipEntry);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 解压文件
     *
     * @param filePath             压缩文件路径
     * @param decompressedFilePath 解压文件路径
     */
    public static void unzipFile(String filePath, String decompressedFilePath) {
        try (ZipInputStream zipIn = new ZipInputStream(new BufferedInputStream(new FileInputStream(filePath)))) {
            File destDir = new File(decompressedFilePath);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                String entryName = entry.getName();
                File file = new File(decompressedFilePath + File.separator + entryName);
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
                        byte[] bytes = new byte[1024];
                        int length;
                        while ((length = zipIn.read(bytes)) != -1) {
                            bos.write(bytes, 0, length);
                        }
                    }
                }
                zipIn.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成密钥(由于AES密钥长度限制为16位, 所以这里截取前16位)
     *
     * @param secretKey 密钥
     * @return SecretKeySpec
     */
    public static final SecretKeySpec generateSecretKey(String secretKey) {
        try {
            byte[] key = Arrays.copyOf(secretKey.getBytes("UTF-8"), 16);
            return new SecretKeySpec(key, "AES");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
