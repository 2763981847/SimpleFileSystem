package cn.autumnclouds.util;

import java.io.*;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Fu Qiujie
 * @since 2024/3/23
 */
public class FileUtils {

    public static void fileCopyWithProgress(String src, String dest, Consumer<Double> consumer) {
        File fileIn = new File(src);
        File fileOut = new File(dest);
        try (FileInputStream fis = new FileInputStream(fileIn); FileOutputStream fos = new FileOutputStream(fileOut)) {
            byte[] buffer = new byte[1024]; // 缓冲区大小
            long fileSize = fileIn.length(); // 文件大小
            long copiedBytes = 0; // 已拷贝字节数
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                copiedBytes += bytesRead;
                double progress = (double) copiedBytes / fileSize * 100; // 计算拷贝进度
                consumer.accept(progress); // 调用Consumer
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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


}
