package dev.whatsappuser.minestom.permissions.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * development by TimoH created on 16:04:04 | 01.01.2023
 */

public class FileUtil {

    public static boolean doesFileExist(String fileName) {
        Path path = Path.of(fileName);
        return Files.exists(path);
    }

    public static void createFile(String fileName) throws IOException {
        var path = Path.of(fileName);
        File file = new File(String.valueOf(path));
        file.createNewFile();
    }

    public static void createDirectory(String path) throws IOException {
        Files.createDirectory(Path.of(path));
    }

    public static File getFile(String fileName) {
        return new File(fileName);
    }
}
