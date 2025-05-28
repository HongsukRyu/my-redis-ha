package com.backend.api.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Component
public class CompressZip {

    public void compress(String path, String outputPath, String outputFileName) throws Throwable {
        File file = new File(path);
        int pos = outputFileName.lastIndexOf(".") == -1 ? outputFileName.length() : outputFileName.lastIndexOf(".");
        if (!outputFileName.substring(pos).equalsIgnoreCase(".zip")) {
            outputFileName += ".zip";
        }
        if (!file.exists()) {
            throw new FileNotFoundException(path);
        }
        try (FileOutputStream fos = new FileOutputStream(outputPath + outputFileName);
            ZipOutputStream zos = new ZipOutputStream(fos)
        ) {
            searchDirectory(file, file.getPath(), zos);
        } catch (Throwable e) {
            log.warn(e.getMessage());
            throw e;
        }
    }

    private void searchDirectory(File file, String root, ZipOutputStream zos) throws Throwable {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : Objects.requireNonNull(files)) {
                log.info("file >>>>>> " + f);
                searchDirectory(f, root, zos);
            }
        } else {
            try {
                compressZip(file, root, zos);
            } catch (Throwable e) {
                log.warn(e.getMessage());
                throw e;
            }
        }
    }

    private void compressZip(File file, String root, ZipOutputStream zos) throws Throwable {
        String zipName = file.getPath().replace(root + File.separator, "");
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipentry = new ZipEntry(zipName);
            zos.putNextEntry(zipentry);
            int length = (int) file.length();
            byte[] buffer = new byte[length];
            fis.read(buffer, 0, length);
            zos.write(buffer, 0, length);
            zos.closeEntry();
        } catch (Throwable e) {
            log.warn(e.getMessage());
            throw e;
        }
    }
}